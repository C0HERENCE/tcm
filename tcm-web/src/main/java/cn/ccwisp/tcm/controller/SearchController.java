package cn.ccwisp.tcm.controller;

import cn.ccwisp.tcm.common.api.CommonResult;
import cn.ccwisp.tcm.search.service.SearchEsService;
import org.elasticsearch.search.SearchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.parameters.P;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

// TODO 简单搜索，高级搜索
@RequestMapping("search")
@RestController
public class SearchController {
    private final SearchEsService searchEsService;

    public SearchController(SearchEsService searchEsService) {
        this.searchEsService = searchEsService;
    }

    @GetMapping("")
    public CommonResult<Map<String, Object>> searchResult(@RequestParam String keyword, @RequestParam int size, @RequestParam int page) throws IOException {
        return CommonResult.success(searchEsService.GlobalSearch(keyword, page * size, size));
    }

    @PostMapping("/advanced")
    public CommonResult<Map<String, Object>> advancedSearchResult(@RequestBody Map<String, Object> map)
            throws IOException {
        int size = (int) map.get("size");
        int page = (int) map.get("page");
        ArrayList<String> array = (ArrayList<String>) map.get("indices");
        String[] indices = new String[array.size()];
        array.toArray(indices);
        ArrayList<Map<String, Object>> hash = (ArrayList<Map<String, Object>>) map.get("keywordFields");
        HashMap<String, List<String>> keywordFields = new HashMap<>();
        for (Map<String, Object> stringListMap : hash) {
            if (((int) stringListMap.get("enabled")) != 1)
                continue;
            ArrayList<String> treeValue = (ArrayList<String>) stringListMap.get("treeValue");
            if (treeValue.contains("chineseName")) {
                treeValue.add("chinesename");
                treeValue.add("name");
            }
            keywordFields.put((String) stringListMap.get("keyword"), treeValue);
        }
        return CommonResult.success(searchEsService.AdvancedSearch(indices, page * size, size, keywordFields));
    }
}
