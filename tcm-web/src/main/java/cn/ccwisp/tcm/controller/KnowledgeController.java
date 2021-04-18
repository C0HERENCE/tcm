package cn.ccwisp.tcm.controller;

import cn.ccwisp.tcm.common.api.CommonResult;
import cn.ccwisp.tcm.dto.CategoryWithChildResult;
import cn.ccwisp.tcm.dto.KnowledgePinyinResult;
import cn.ccwisp.tcm.generated.domain.Kms;
import cn.ccwisp.tcm.service.KnowledgeService;
import net.sourceforge.pinyin4j.PinyinHelper;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

// TODO 连接Es
@RestController
@RequestMapping("knowledge")
public class KnowledgeController {

    private final KnowledgeService knowledgeService;

    public KnowledgeController(KnowledgeService knowledgeService) {
        this.knowledgeService = knowledgeService;
    }

    @GetMapping("/categories")
    public CommonResult<List<CategoryWithChildResult>> getCategoriesByType(@RequestParam("type") int type) {
        List<Kms> kmsByCategoryId = knowledgeService.getKmsCategoryByTypeId(type);
        HashMap<String, ArrayList<Kms>> children = new HashMap<>();
        for (Kms kms : kmsByCategoryId) {
            if (!children.containsKey(kms.getParentcategorychinesename()))
                children.put(kms.getParentcategorychinesename(), new ArrayList<>());
            children.get(kms.getParentcategorychinesename()).add(kms);
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
    public CommonResult<KnowledgePinyinResult> getKnowledgeByCategoryId(@PathVariable("id") int categoryId) {
        KnowledgePinyinResult data = new KnowledgePinyinResult();
        for (Kms kms : knowledgeService.getKmsByCategoryId(categoryId)) {
            String s = PinyinHelper.toHanyuPinyinStringArray(kms.getChinesename().charAt(0))[0];
            char initial = (char) (s.charAt(0) - 32);
            if (!data.getMap().containsKey(initial))
                data.getMap().put(initial, new ArrayList<>());
            data.getMap().get(initial).add(kms);
        }
        return CommonResult.success(data);
    }
}
