package cn.ccwisp.tcm.service;

import cn.ccwisp.tcm.generated.domain.Kms;
import cn.ccwisp.tcm.generated.domain.KmsCategory;
import cn.ccwisp.tcm.generated.domain.KmsFeedback;
import cn.ccwisp.tcm.generated.service.impl.KmsFeedbackServiceImpl;
import cn.ccwisp.tcm.generated.service.impl.KmsServiceImpl;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class KnowledgeService {
    private final KmsServiceImpl kmsService;
    private final KmsFeedbackServiceImpl feedbackService;

    public KnowledgeService(KmsServiceImpl kmsService, KmsFeedbackServiceImpl feedbackService) {
        this.kmsService = kmsService;
        this.feedbackService = feedbackService;
    }

    public List<Kms> getKmsByCategoryId(int categoryId) {
        QueryWrapper<Kms> queryWrapper = new QueryWrapper<>();
        queryWrapper
                .eq("subCategoryId", categoryId);
        return kmsService.list(queryWrapper);
    }

    public List<Kms> getKmsCategoryByTypeId(int type) {
        QueryWrapper<Kms> queryWrapper = new QueryWrapper<>();
        queryWrapper
                .eq("category_type", type)
                .select("DISTINCT subCategoryId", "subCategoryChineseName", "parentCategoryId", "parentCategoryChineseName",
                        "subCategoryLatinName", "parentCategoryLatinName")
                .orderBy(true, true, "subCategoryId");
        return kmsService.list(queryWrapper);
    }

    public int getEsIdByKnowledgeId(int knowledgeId) {
        QueryWrapper<Kms> queryWrapper = new QueryWrapper<>();
        queryWrapper
                .select("esId")
                .eq("id", knowledgeId);
        Kms kms = kmsService.getOne(queryWrapper);
        return kms.getEsid();
    }

    public void AddFeedback(KmsFeedback feedback) {
        feedbackService.save(feedback);
    }
}
