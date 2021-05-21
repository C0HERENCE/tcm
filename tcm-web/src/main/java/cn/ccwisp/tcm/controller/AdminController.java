package cn.ccwisp.tcm.controller;

import cn.ccwisp.tcm.common.api.CommonResult;
import cn.ccwisp.tcm.dto.FmsCommentDto;
import cn.ccwisp.tcm.generated.domain.*;
import cn.ccwisp.tcm.generated.service.impl.UmsAdminLogsServiceImpl;
import cn.ccwisp.tcm.generated.service.impl.UmsServiceImpl;
import cn.ccwisp.tcm.generated.service.impl.UmsUserServiceImpl;
import cn.ccwisp.tcm.search.service.AdminEsService;
import cn.ccwisp.tcm.search.service.SearchEsService;
import cn.ccwisp.tcm.service.AdminService;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.elasticsearch.action.delete.DeleteRequest;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.xcontent.XContentType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.*;

@RestController
@RequestMapping("admin")
public class AdminController {
    private final UmsServiceImpl umsService;
    private final AdminEsService adminEsService;
    private final SearchEsService searchEsService;
    private final AdminService adminService;

    public AdminController(UmsServiceImpl umsService, AdminEsService adminEsService, SearchEsService searchEsService, UmsUserServiceImpl umsUserService, AdminService adminService, UmsAdminLogsServiceImpl adminLogsService) {
        this.umsService = umsService;
        this.adminEsService = adminEsService;
        this.searchEsService = searchEsService;
        this.adminService = adminService;
        this.adminLogsService = adminLogsService;
    }
    public String convertStreamToString(MultipartFile file) throws IOException {
        StringBuilder sb;
        try (InputStream inputStream = file.getInputStream()) {
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
            sb = new StringBuilder();

            String line = null;
            try {
                while ((line = reader.readLine()) != null) {
                    sb.append(line).append("\n");
                }
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                try {
                    inputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        return sb.toString();
    }
    @GetMapping("/allUser")
    public CommonResult<List<Ums>> GetUserInfo() {
        return CommonResult.success(umsService.list(new QueryWrapper<Ums>().eq("isAdmin", 0)));
    }

    @GetMapping("/allKnowledge")
    public CommonResult<Map<String, Object>> GetKnowledge(@RequestParam String index, @RequestParam int page, @RequestParam int size) throws IOException {
        return CommonResult.success(adminEsService.GetKnowledge(index, page, size));
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
        return CommonResult.success(searchEsService.AdminAdvancedSearch(indices, page * size, size, keywordFields));
    }

    @GetMapping("/user/roles")
    public CommonResult<List<UmsUserRole>> getUserRoles(@RequestParam Integer userId) {
        return CommonResult.success(adminService.getUserRoles(userId));
    }

    @PostMapping("/user/role/add")
    public CommonResult<Object> addRole(@RequestParam Integer userId, @RequestParam String roleName) {
        adminService.addRole(userId, roleName);
        return CommonResult.success("操作已完成");
    }

    @PostMapping("/user/role/remove")
    public CommonResult<Object> removeRole(@RequestParam Integer userId, @RequestParam String roleName) {
        adminService.removeRole(userId, roleName);
        return CommonResult.success("操作已完成");
    }


    @PostMapping("/user/enabled")
    public CommonResult<Object> setUserEnable(@RequestParam Integer userId, @RequestParam Integer enabled) {
        adminService.setUserEnable(userId, enabled);
        return CommonResult.success("操作已完成");
    }


    @PostMapping("/category/update")
    public CommonResult<Object> updateCategory(@RequestParam Integer CategoryId, @RequestParam String title, @RequestParam String english, @RequestParam String intro) {
        adminService.updateCategory(CategoryId, title, english, intro);
        return CommonResult.success("操作已完成");
    }

    @PostMapping("/category/add")
    public CommonResult<Object> addCategory(@RequestParam String Title, @RequestParam String english, @RequestParam String intro) {
        adminService.AddCategory(Title, english, intro);
        return CommonResult.success("操作已完成");
    }

    @PostMapping("/thread/setCategory")
    public CommonResult<Object> SetThreadType(@RequestParam Integer threadId, @RequestParam Integer TypeId) {
        adminService.SetThreadType(threadId, TypeId);
        return CommonResult.success("操作已完成");
    }

    @PostMapping("/thread/delete")
    public CommonResult<Object> deleteThread(@RequestParam Integer id, @RequestParam Integer enabled) {
        adminService.DeleteThread(id, enabled);
        return CommonResult.success("操作已完成");
    }

    @PostMapping("/comment/delete")
    public CommonResult<Object> deleteComment(@RequestParam Integer id, @RequestParam Integer enabled) {
        adminService.DeleteComment(id, enabled);
        return CommonResult.success("操作已完成");
    }

    @PostMapping("/knowledge")
    public CommonResult<Object> updateEs(String index, String id, String field, String value) throws IOException {
        adminService.updateEs(index, id, field, value);
        return CommonResult.success("操作已完成");
    }

    @Autowired
    private RestHighLevelClient restHighLevelClient;

    @PostMapping("/knowledge2")
    public CommonResult<Object> batchUploading(@RequestParam("file") MultipartFile upfile, @RequestParam String esindex) throws IOException {
        if (upfile.getSize()>0) {
            String json = convertStreamToString(upfile);
            List<HashMap> lists = JSON.parseArray(json, HashMap.class);
            System.out.println(json);
            for (HashMap<String, Object> j : lists) {
                if (!j.containsKey("id")) {
                    continue;
                }
                IndexRequest source = new IndexRequest(esindex)
                        .source(j)
                        .id(j.get("id").toString());
                restHighLevelClient.index(source, RequestOptions.DEFAULT);
            }
        }
        return CommonResult.success("操作成功");
    }

    @GetMapping("/categories")
    public CommonResult<List<FmsCategory>> GetAllCategories() {
        return CommonResult.success(adminService.GetAllCategories());
    }

    @PostMapping("/category/enable")
    public CommonResult<String> EnableCategory(@RequestParam Integer categoryId, @RequestParam Integer enabled) {
        adminService.enableCategory(categoryId, enabled);
        return CommonResult.success("操作已完成");
    }

    @GetMapping("/threadComments")
    public CommonResult<List<FmsCommentDto>> getCommentsByThreadId(@RequestParam Integer threadId){
        return CommonResult.success(adminService.getAllCommentsByThreadId(threadId));
    }

    @GetMapping("/allThread")
    public CommonResult<List<Fms>> getAllThread(
            @RequestParam String keyword,
            @RequestParam Integer categoryId,
            @RequestParam Integer typeId,
            @RequestParam Integer authorId
    ) {
        return CommonResult.success(adminService.getThreads(keyword, categoryId, typeId, authorId));
    }

    @GetMapping("/feedback")
    public CommonResult<List<KmsFeedback>> GetAllFeedback() {
        return CommonResult.success(adminService.getAllFeedback());
    }
    @PostMapping("/feedback")
    public CommonResult<Object> ProcessFeedback(@RequestParam Integer feedbackId, @RequestParam String note) {
        adminService.processFeedback(feedbackId, note);
        return CommonResult.success("处理成功");
    }
    private final UmsAdminLogsServiceImpl adminLogsService;
    @GetMapping("/statistics/admin")
    public CommonResult<List<UmsAdminLogs>> GetAdminLogs(){
        return CommonResult.success(adminLogsService.list(new QueryWrapper<UmsAdminLogs>().orderByDesc("createtime")));
    }

    @GetMapping("/statistics/forum")
    public CommonResult<Map<String, Object>> GetForumStatistics(){
        return CommonResult.success(adminService.GetForumStatistics());
    }


    @GetMapping("/admins")
    public CommonResult<List<Ums>> getAllAdmins() {
        return CommonResult.success(adminService.getAllAdmins());
    }

    @GetMapping("/adminRoles")
    public CommonResult<List<UmsRoles>> getRolesByAdminId(@RequestParam Integer adminId) {
        return CommonResult.success(adminService.getUserRolesById(adminId));
    }

    @PostMapping("/setRoles")
    public CommonResult<Object> setRoles(@RequestBody HashMap<String, Object> roles) {
        adminService.setRole((int)roles.get("adminId"), (ArrayList<String>)roles.get("roles"));
        return CommonResult.success("操作成功");
    }

    @PostMapping("/deleteEs")
    public CommonResult<Object> deleteEs(@RequestParam String index, @RequestParam String id) throws IOException {
        restHighLevelClient.delete(new DeleteRequest(index).id(id), RequestOptions.DEFAULT);
        return CommonResult.success("删除成功");
    }
}
