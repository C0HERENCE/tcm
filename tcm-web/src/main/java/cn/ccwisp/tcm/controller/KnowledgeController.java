package cn.ccwisp.tcm.controller;

import cn.ccwisp.tcm.common.api.CommonResult;
import cn.ccwisp.tcm.generated.domain.Kms;
import cn.ccwisp.tcm.generated.domain.KmsCategory;
import cn.ccwisp.tcm.generated.domain.KmsKnowledge;
import cn.ccwisp.tcm.generated.service.impl.KmsCategoryServiceImpl;
import cn.ccwisp.tcm.generated.service.impl.KmsKnowledgeServiceImpl;
import cn.ccwisp.tcm.generated.service.impl.KmsServiceImpl;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

// TODO 连接Es
@RestController
@RequestMapping("knowledge")
public class KnowledgeController {
    @Autowired
    private KmsServiceImpl kmsService;

    @GetMapping("/categories")
    public CommonResult getCategoriesByType(@RequestParam("type") int type) {
        QueryWrapper<Kms> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("category_type", type)
        .select("DISTINCT subCategoryId", "subCategoryChineseName", "parentCategoryId", "parentCategoryChineseName");
        return CommonResult.success(kmsService.list(queryWrapper));
    }

    @GetMapping("/category/{id}")
    public CommonResult getKnowledgeByCategoryId(@PathVariable("id") int categoryId) {
        QueryWrapper<Kms> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("subCategoryId", categoryId)
        .select("id", "chineseName");
        return CommonResult.success(kmsService.list(queryWrapper));
    }

    @GetMapping("/herb/{id}")
    public CommonResult getHerbById(@PathVariable("id") int id) {
        return CommonResult.success(1);
    }

    @GetMapping("/symptom/{id}")
    public CommonResult getSymptomById(@PathVariable("id") int id) {
        return CommonResult.success(1);
    }

    @GetMapping("/prescription/{id}")
    public CommonResult getPrescriptionById(@PathVariable("id") int id) {
        return CommonResult.success(1);
    }
}
