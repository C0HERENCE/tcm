package cn.ccwisp.tcm.search.service;

import org.elasticsearch.action.get.GetRequest;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.*;

import static cn.ccwisp.tcm.search.service.utils.RemoveHtml.getTextFromHtml;

@Service
public class KnowledgeEsService {
    private final RestHighLevelClient restHighLevelClient;
    private final TranslateService translateService;
    private final RecommendSearch recommendSearch;
    public KnowledgeEsService(RestHighLevelClient restHighLevelClient, TranslateService translateService, RecommendSearch recommendSearch) {
        this.restHighLevelClient = restHighLevelClient;
        this.translateService = translateService;
        this.recommendSearch = recommendSearch;
    }

    public Map<String, Object> getSymptom(String id, String lang) throws IOException {
        GetRequest getRequest = new GetRequest("disease", id);
        GetResponse documentFields = restHighLevelClient.get(getRequest, RequestOptions.DEFAULT);
        if (Objects.equals(lang, "en-US")) {
            return TranslateDocumentMap(documentFields);
        } else
            return documentFields.getSourceAsMap();
    }



    public Map<String, Object> getHerb(String id, String lang) throws IOException {
        GetRequest getRequest = new GetRequest("herbs_info_v2", id);
        GetResponse documentFields = restHighLevelClient.get(getRequest, RequestOptions.DEFAULT);
        Map<String, Object> result = documentFields.getSourceAsMap();
        if (Objects.equals(lang, "en-US")) {
            return TranslateDocumentMap(documentFields);
        } else
            return documentFields.getSourceAsMap();
    }
    public Map<String, Object> getPrescription(String id, String lang) throws IOException {
        GetRequest getRequest = new GetRequest("prescriptions", id);
        GetResponse documentFields = restHighLevelClient.get(getRequest, RequestOptions.DEFAULT);
        if (Objects.equals(lang, "en-US")) {
            return TranslateDocumentMap(documentFields);
        } else
            return documentFields.getSourceAsMap();
    }

    private Map<String, Object> TranslateDocumentMap(GetResponse documentFields) {
        Map<String, Object> sourceAsMap = documentFields.getSourceAsMap();
        List<Map<String, String>> attempts = new ArrayList<>();
        HashMap<String,String> little = new HashMap<>();
        Integer byteSum = 0;
        for (Map.Entry<String, Object> entry : sourceAsMap.entrySet()) {
            Object value = entry.getValue();
            if (value == null || value.getClass() != String.class)
                continue;
            String str = getTextFromHtml((String) value);
            if (str.getBytes().length + byteSum > 5900) {
                attempts.add((Map<String, String>) little.clone()); // 保存little
                byteSum = str.getBytes().length; // 再次初始化byteSum
                little.clear(); // 再次初始化little
            } else {
                byteSum += str.getBytes().length;
            }
            little.put(entry.getKey(), str);
        }
        if (little.size()>0) {
            attempts.add((Map<String, String>) little.clone());
            little.clear();
        }
        sourceAsMap.clear();
        List<String> excludeKeys = new ArrayList<>();
        for (Map<String, String> attempt : attempts) {
            StringBuilder needTranslate = new StringBuilder();
            for (Map.Entry<String, String> stringStringEntry : attempt.entrySet()) {
                String replace = stringStringEntry.getValue().replace('\n', ' ');
                if (replace.trim().equals("")){
                    excludeKeys.add(stringStringEntry.getKey());
                    continue;
                }
                needTranslate.append(replace).append('\n');
            }
            String[] split = translateService.Translate(needTranslate.toString()).split("\n");
            int i = 0;
            for (Map.Entry<String, String> stringStringEntry : attempt.entrySet()) {
                if (excludeKeys.contains(stringStringEntry.getKey())) continue;
                if (i>=split.length){
                    System.out.println("Translate error");
                    stringStringEntry.setValue(null);
                    continue;
                }
                stringStringEntry.setValue(split[i]);
                i++;
            }
            sourceAsMap.putAll(attempt);
        }
        for (String excludeKey : excludeKeys) {
            if (sourceAsMap.containsKey(excludeKey))
                sourceAsMap.remove(excludeKey);
        }
        return sourceAsMap;
    }
}
