package cn.ccwisp.tcm.search.service;

import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class RecommendSearch {

    private final RestHighLevelClient restHighLevelClient;

    public RecommendSearch(RestHighLevelClient restHighLevelClient) {
        this.restHighLevelClient = restHighLevelClient;
    }

    public List<Object> getRelatedFromHerb(String herbName) throws IOException {
        List<Object> recommends = new ArrayList<>();
        {
            SearchSourceBuilder sourceBuilder = new SearchSourceBuilder()
                    .from(0)
                    .size(5)
                    .fetchSource(
                            new String[]{"id", "chinesename", "chineseName", "name", "intro", "introduction", "picturepath", "thumb", "picturePath", "thumbnail"},
                            new String[]{})
                    .query(QueryBuilders.multiMatchQuery(herbName, "jcomposition"));
            SearchResponse prescriptions = restHighLevelClient.search(
                    new SearchRequest("prescriptions").source(sourceBuilder), RequestOptions.DEFAULT);
            for (SearchHit hit : prescriptions.getHits()) {
                Map<String, Object> sourceAsMap = hit.getSourceAsMap();
                sourceAsMap.put("type", hit.getIndex());
                recommends.add(sourceAsMap);
            }
        }
        {
            SearchSourceBuilder sourceBuilder = new SearchSourceBuilder()
                    .from(0)
                    .size(5)
                    .fetchSource(
                            new String[]{"id", "chinesename", "chineseName", "name", "intro", "introduction", "picturepath", "thumb", "picturePath", "thumbnail"},
                            new String[]{})
                    .query(QueryBuilders.multiMatchQuery(herbName, "chinesename"));
            SearchResponse prescriptions = restHighLevelClient.search(
                    new SearchRequest("herbs_info_v2").source(sourceBuilder), RequestOptions.DEFAULT);
            for (SearchHit hit : prescriptions.getHits()) {
                Map<String, Object> sourceAsMap = hit.getSourceAsMap();
                sourceAsMap.put("type", hit.getIndex());
                recommends.add(sourceAsMap);
            }
        }
        return recommends;
    }

    public List<Object> getRelatedFromPrescriptions(String name) throws IOException {
        List<Object> recommends = new ArrayList<>();
        {
            SearchSourceBuilder sourceBuilder = new SearchSourceBuilder()
                    .from(0)
                    .size(5)
                    .fetchSource(
                            new String[]{"id", "chinesename", "chineseName", "name", "intro", "introduction", "picturepath", "thumb", "picturePath", "thumbnail"},
                            new String[]{})
                    .query(QueryBuilders.multiMatchQuery(name, "relatedprescription"));
            SearchResponse herbs = restHighLevelClient.search(new SearchRequest("herbs_info_v2").source(sourceBuilder), RequestOptions.DEFAULT);
            for (SearchHit hit : herbs.getHits()) {
                Map<String, Object> sourceAsMap = hit.getSourceAsMap();
                sourceAsMap.put("type", hit.getIndex());
                recommends.add(sourceAsMap);
            }
        }
        {
            SearchSourceBuilder sourceBuilder = new SearchSourceBuilder()
                    .from(0)
                    .size(5)
                    .fetchSource(
                            new String[]{"id", "chinesename", "chineseName", "name", "intro", "introduction", "picturepath", "thumb", "picturePath", "thumbnail"},
                            new String[]{})
                    .query(QueryBuilders.multiMatchQuery(name, "name"));
            SearchResponse herbs = restHighLevelClient.search(new SearchRequest("prescriptions").source(sourceBuilder), RequestOptions.DEFAULT);
            for (SearchHit hit : herbs.getHits()) {
                Map<String, Object> sourceAsMap = hit.getSourceAsMap();
                sourceAsMap.put("type", hit.getIndex());
                recommends.add(sourceAsMap);
            }
        }
        return recommends;
    }
}
