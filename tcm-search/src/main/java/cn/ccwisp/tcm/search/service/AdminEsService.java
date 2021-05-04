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
public class AdminEsService {
    private final RestHighLevelClient restHighLevelClient;

    public AdminEsService(RestHighLevelClient restHighLevelClient) {
        this.restHighLevelClient = restHighLevelClient;
    }

    public Map<String, Object> GetKnowledge(String index, int page, int size) throws IOException {
        SearchResponse herbs_info_v2 = restHighLevelClient.search(new SearchRequest(index)
                .source(new SearchSourceBuilder()
                        .query(QueryBuilders.matchAllQuery())
                        .size(size)
                        .from((page - 1) * size)), RequestOptions.DEFAULT);
        Map<String, Object> result = new HashMap<>();
        result.put("total", herbs_info_v2.getHits().getTotalHits());
        result.put("list", new ArrayList<>());
        for (SearchHit hit : herbs_info_v2.getHits()) {
            ((ArrayList) result.get("list")).add(hit.getSourceAsMap());
        }
        return result;
    }
}
