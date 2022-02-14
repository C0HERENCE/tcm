package cn.ccwisp.tcm.search.service;

import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.text.Text;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightField;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class SearchEsService {
    private final RestHighLevelClient restHighLevelClient;

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
            .multiMatchQuery(keyword, "chineseName",
                    "chinesename", "name", "introduction", "intro"))
    // fetchSource 传入的第一个参数是要查询字段的名称数组.
    .fetchSource(
            // 全局搜索仅需要词条名称、图片、介绍即可。
            new String[]{"id","chinesename", "intro",
                    "picturepath", "thumb", "introduction", "chineseName", "name"},
            new String[]{""})
    // 可以利用 highlighter 实现搜索结果的高亮显示
    .highlighter(new HighlightBuilder()
            .preTags("<em style=\"font-style:normal;color:red\">") // 高亮前缀
            .postTags("</em>") // 高亮后缀
            // 高亮字段
            .field("chineseName").field("name")
            .field("chinesename").field("introduction"))
), RequestOptions.DEFAULT);
// 调用getStringObjectMap函数将多个搜索结果成id为Key，结果为Value的哈希表
return getStringObjectMap(searchResponse);
    }

    private Map<String, Object> getStringObjectMap(SearchResponse searchResponse) {
        List<Map<String, Object>> result = new ArrayList<>();
        for (SearchHit hit : searchResponse.getHits().getHits()) {
            Map<String, Object> map = hit.getSourceAsMap();
            map.put("type", hit.getIndex());
            Map<String, HighlightField> highlightFields = hit.getHighlightFields();
            Map<String, Object> highlighter = new HashMap<>();
            for (Map.Entry<String, HighlightField> stringHighlightFieldEntry : highlightFields.entrySet()) {
                ArrayList<String> sFrag = new ArrayList<>();
                for (Text fragment : stringHighlightFieldEntry.getValue().getFragments()) {
                    sFrag.add(fragment.toString());
                }
                highlighter.put(stringHighlightFieldEntry.getKey(), sFrag);
            }
            if (highlightFields.size() > 0)
                map.put("highlighter", highlighter);
            else
                map.put("highlighter", new ArrayList<>());
            result.add(map);
        }
        Map<String, Object> ans = new HashMap<>();
        ans.put("result", result);
        ans.put("total", searchResponse.getHits().getTotalHits());
        return ans;
    }


    // 高级搜索
    public Map<String, Object> AdvancedSearch(String[] indices, int from, int size, Map<String, List<String>> keywords) throws IOException {
        SearchSourceBuilder sourceBuilder = new SearchSourceBuilder()
                .from(from)
                .size(size)
                .fetchSource(
                        new String[]{"id", "chinesename", "chineseName", "name", "intro", "introduction", "picturepath", "thumb", "picturePath", "thumbnail"},
                        new String[]{""})
                .highlighter(new HighlightBuilder()
                        .preTags("<em style=\"font-style:normal;color:red\">")
                        .postTags("</em>")
                        .field("chineseName")
                        .field("name")
                        .field("chinesename")
                        .field("introduction"));
        for (Map.Entry<String, List<String>> keywordsFields : keywords.entrySet()) {
            String[] x = new String[keywordsFields.getValue().size()];
            keywordsFields.getValue().toArray(x);
            sourceBuilder.query(QueryBuilders.multiMatchQuery(keywordsFields.getKey(), x));
        }
        SearchResponse searchResponse = restHighLevelClient.search(new SearchRequest()
                .indices(indices)
                .source(sourceBuilder), RequestOptions.DEFAULT);
        return getStringObjectMap(searchResponse);
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
}
