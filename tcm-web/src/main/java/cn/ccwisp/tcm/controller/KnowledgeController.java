package cn.ccwisp.tcm.controller;

import cn.ccwisp.tcm.bo.TcmUserDetails;
import cn.ccwisp.tcm.common.api.CommonResult;
import cn.ccwisp.tcm.dto.CategoryWithChildResult;
import cn.ccwisp.tcm.dto.KnowledgePinyinResult;
import cn.ccwisp.tcm.generated.domain.Kms;
import cn.ccwisp.tcm.generated.domain.KmsFeedback;
import cn.ccwisp.tcm.search.service.KnowledgeEsService;
import cn.ccwisp.tcm.search.service.RecommendSearch;
import cn.ccwisp.tcm.service.KnowledgeService;
import net.sourceforge.pinyin4j.PinyinHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

// TODO 连接Es
@RestController
@RequestMapping("knowledge")
public class KnowledgeController {

    private final KnowledgeService knowledgeService;
    private final KnowledgeEsService knowledgeEsService;

    public KnowledgeController(KnowledgeService knowledgeService, KnowledgeEsService knowledgeEsService, RecommendSearch recommendSearch) {
        this.knowledgeService = knowledgeService;
        this.knowledgeEsService = knowledgeEsService;
        this.recommendSearch = recommendSearch;
    }

    @GetMapping("/categories")
    public CommonResult<List<CategoryWithChildResult>> getCategoriesByType(@RequestParam("type") int type,
                                                                           @RequestHeader("Accept-Language") String lang) {
        List<Kms> kmsByCategoryId = knowledgeService.getKmsCategoryByTypeId(type); // 获得所有分类 type = herb|prescription|symptom
        HashMap<String, ArrayList<Kms>> children = new HashMap<>();
        if (Objects.equals(lang, "en-US")) {
            for (Kms kms : kmsByCategoryId) {
                if (!children.containsKey(kms.getParentcategorylatinname()))
                    children.put(kms.getParentcategorylatinname(), new ArrayList<>());
                children.get(kms.getParentcategorylatinname()).add(kms);
            }
        } else {
            for (Kms kms : kmsByCategoryId) {
                if (!children.containsKey(kms.getParentcategorychinesename()))
                    children.put(kms.getParentcategorychinesename(), new ArrayList<>());
                children.get(kms.getParentcategorychinesename()).add(kms);
            }
        }
        AtomicInteger i = new AtomicInteger(1000);

        ArrayList<CategoryWithChildResult> childResults = new ArrayList<>();
        children.forEach((parentName, childrenList) -> {
            CategoryWithChildResult c = new CategoryWithChildResult();
            c.setKey(i.getAndIncrement());
            c.setTitle(parentName);
            childrenList.forEach(k -> {
                CategoryWithChildResult cc = new CategoryWithChildResult();
                cc.setKey(k.getSubcategoryid());
                if (Objects.equals(lang, "en-US"))
                    cc.setTitle(k.getSubcategorylatinname());
                else
                    cc.setTitle(k.getSubcategorychinesename());
                cc.setChildren(null);
                c.getChildren().add(cc);
            });
            childResults.add(c);
        });
        return CommonResult.success(childResults);
    }

    // 获取分类下所有条目， 拼音结果返回
    @GetMapping("/category/{id}")
    public CommonResult<KnowledgePinyinResult> getKnowledgeByCategoryId(@PathVariable("id") int categoryId,
                                                                        @RequestHeader("Accept-Language") String lang) {
        KnowledgePinyinResult data = new KnowledgePinyinResult();
        for (Kms kms : knowledgeService.getKmsByCategoryId(categoryId)) {
            String s = "";
            char initial = 'A';
            if (Objects.equals(lang, "en-US")) {
                char initial1 = kms.getLatinname().charAt(0);
                initial = (initial1 >= 'A' && initial1 <= 'Z') ? initial1 : (char) (initial1 + -32);
            } else {
                s = PinyinHelper.toHanyuPinyinStringArray(kms.getChinesename().charAt(0))[0];
                initial = (char) (s.charAt(0) - 32);
            }
            if (!data.getMap().containsKey(initial))
                data.getMap().put(initial, new ArrayList<>());
            data.getMap().get(initial).add(kms);
        }
        return CommonResult.success(data);
    }

    @GetMapping("/symptom/{id}")
    public CommonResult<Map<String, Object>> getSymptom(@PathVariable String id,
                                                        @RequestHeader("Accept-Language") String lang) throws IOException {
        return CommonResult.success(knowledgeEsService.getSymptom(id, lang));
    }

    @GetMapping("/prescription/{id}")
    public CommonResult<Map<String, Object>> getPrescription(@PathVariable String id,
                                                             @RequestHeader("Accept-Language") String lang) throws IOException {
        return CommonResult.success(knowledgeEsService.getPrescription(id, lang));
    }

    @GetMapping("/herb/{id}")
    public CommonResult<Map<String, Object>> getHerb(@PathVariable String id,
                                                     @RequestHeader("Accept-Language") String lang) throws IOException {
        return CommonResult.success(knowledgeEsService.getHerb(id, lang));
    }

    @PostMapping("/feedback")
    public CommonResult<Object> AddFeedBack(@RequestBody Map<String, Object> form) {
        KmsFeedback kmsFeedback = new KmsFeedback();
        int userId = ((TcmUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getUserId();
        kmsFeedback.setAnchor((String) form.get("anchor"));
        kmsFeedback.setCreatetime(new Date());
        kmsFeedback.setEsid(Integer.parseInt((String) form.get("esId")));
        kmsFeedback.setId(0);
        kmsFeedback.setEsindex((String) form.get("index"));
        kmsFeedback.setNote("");
        kmsFeedback.setMessage((String) form.get("message"));
        kmsFeedback.setTitle((String) form.get("title"));
        kmsFeedback.setProcessed(0);
        kmsFeedback.setOperatorid(0);
        kmsFeedback.setUserid(userId);
        knowledgeService.AddFeedback(kmsFeedback);
        return CommonResult.success("留言成功");
    }
    private final RecommendSearch recommendSearch;

    @GetMapping("/herbRelated")
    public CommonResult<List<Object>> getHerbRelated(@RequestParam String name) throws IOException {
        return CommonResult.success(recommendSearch.getRelatedFromHerb(name));
    }

    @GetMapping("/prescriptionRelated")
    public CommonResult<List<Object>> getPrescriptionRelated(@RequestParam String name) throws IOException {
        return CommonResult.success(recommendSearch.getRelatedFromPrescriptions(name));
    }
}
