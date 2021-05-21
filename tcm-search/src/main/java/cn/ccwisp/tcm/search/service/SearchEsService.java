package cn.ccwisp.tcm.search.service;

import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightField;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.*;

@Service
public class SearchEsService {
    private final RestHighLevelClient restHighLevelClient;

    private final HighlightBuilder highlightBuilder = new HighlightBuilder()
            .preTags("<em style=\"font-style:normal;color:red\">") // 高亮前缀
            .postTags("</em>") // 高亮后缀
            .field("chineseName")
            .field("name")
            .field("chinesename")
            .field("intro")
            .field("introduction");

    public SearchEsService(RestHighLevelClient restHighLevelClient) {
        this.restHighLevelClient = restHighLevelClient; // Spring Boot 注入的elasticsearch客户端
    }

    // 全局搜索功能
    public Map<String, Object> GlobalSearch(String keyword, int from, int size) throws IOException {

        SearchResponse searchResponse = restHighLevelClient.search(
                // 使用SearchRequest发起一个搜索请求， 传入要搜索的elasticsearch的index名
                new SearchRequest("disease", "prescriptions", "herbs_info_v2")
                        // 利用source 传入搜索源的条件
                        .source(new SearchSourceBuilder()
                                // from 参数为搜索结果的起始地址
                                .from(from)
                                // size 参数为搜索结果的最大结果条数
                                .size(size)
                                .query(QueryBuilders
                                        // 使用elasticsearch的multiMatchQuery来匹配不同字段
                                        .multiMatchQuery(keyword, "chineseName", "chinesename", "name", "introduction", "intro"))
                                // fetchSource 传入的第一个参数是要查询字段的名称数组.
                                .fetchSource(
                                        // 全局搜索仅需要词条名称、图片、介绍即可。
                                        new String[]{"id", "chinesename", "intro", "picturepath", "thumb", "introduction", "chineseName", "name"},
                                        new String[]{""})
                                // 可以利用 highlighter 实现搜索结果的高亮显示
                                .highlighter(highlightBuilder)
                        ), RequestOptions.DEFAULT);
// 调用getStringObjectMap函数将多个搜索结果成id为Key，结果为Value的哈希表
        return getStringObjectMap(searchResponse, true);
    }

    // 高级搜索
    public Map<String, Object> AdvancedSearch(String[] indices, int from, int size, Map<String, List<String>> keywords, boolean highlight) throws IOException {
        SearchSourceBuilder sourceBuilder = new SearchSourceBuilder()
                .from(from)
                .size(size)
                .fetchSource(
                        new String[]{"id", "chinesename", "chineseName", "name", "intro", "introduction", "picturepath", "thumb", "picturePath", "thumbnail"},
                        new String[]{""})
                .highlighter(highlightBuilder);
        BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();
        for (Map.Entry<String, List<String>> keywordsFields : keywords.entrySet()) {
            String[] x = new String[keywordsFields.getValue().size()];
            keywordsFields.getValue().toArray(x);
            boolQueryBuilder.should(QueryBuilders.multiMatchQuery(keywordsFields.getKey(), x));
        }
        sourceBuilder.query(boolQueryBuilder);
        SearchResponse searchResponse = restHighLevelClient.search(new SearchRequest()
                .indices(indices)
                .source(sourceBuilder), RequestOptions.DEFAULT);
        return getStringObjectMap(searchResponse, highlight);
    }

    // 管理员高级搜索
    public Map<String, Object> AdminAdvancedSearch(String[] indices, int from, int size, Map<String, List<String>> keywords) throws IOException {
        SearchSourceBuilder sourceBuilder = new SearchSourceBuilder()
                .from(from)
                .size(size)
                .highlighter(new HighlightBuilder()
                        .field(new HighlightBuilder.Field("chinseName")));
        for (Map.Entry<String, List<String>> keywordsFields : keywords.entrySet()) {
            String[] x = new String[keywordsFields.getValue().size()];
            keywordsFields.getValue().toArray(x);
            sourceBuilder.query(QueryBuilders.multiMatchQuery(keywordsFields.getKey(), x));
        }
        SearchResponse searchResponse = restHighLevelClient.search(new SearchRequest()
                .indices(indices)
                .source(sourceBuilder), RequestOptions.DEFAULT);
        List<Map<String, Object>> result = new ArrayList<>();
        for (SearchHit hit : searchResponse.getHits().getHits()) {
            Map<String, Object> map = hit.getSourceAsMap();
            map.put("type", hit.getIndex());
            result.add(map);
        }
        Map<String, Object> ans = new HashMap<>();
        ans.put("result", result);
        ans.put("total", searchResponse.getHits().getTotalHits());
        return ans;
    }

    private Map<String, Object> getStringObjectMap(SearchResponse searchResponse, boolean highlight) {
        List<Map<String, Object>> result = new ArrayList<>();
        for (SearchHit hit : searchResponse.getHits().getHits()) {
            Map<String, Object> map = hit.getSourceAsMap();
            map.put("type", hit.getIndex());
            if (highlight) {
                Map<String, HighlightField> highlightFields = hit.getHighlightFields();
                for (Map.Entry<String, HighlightField> stringHighlightFieldEntry : highlightFields.entrySet()) {
                    map.put(stringHighlightFieldEntry.getKey(), stringHighlightFieldEntry.getValue().getFragments()[0].toString());
                }
            }


            result.add(map);
        }
        Map<String, Object> ans = new HashMap<>();
        ans.put("result", result);
        ans.put("total", searchResponse.getHits().getTotalHits());
        return ans;
    }

}
