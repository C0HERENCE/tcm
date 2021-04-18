package cn.ccwisp.tcm.service;

import cn.ccwisp.tcm.generated.domain.*;
import cn.ccwisp.tcm.generated.service.impl.*;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class ForumService {
    private final FmsCategoryServiceImpl fmsCategoryService;
    private final FmsServiceImpl fmsService;
    private final FmsThreadServiceImpl fmsThreadService;
    private final FmsThreadKmsKnowledgeServiceImpl fmsThreadKmsKnowledgeService;
    private final FmsCommentServiceImpl fmsCommentService;
    private final FmsThreadTypeServiceImpl threadTypeService;
    public ForumService(FmsCategoryServiceImpl fmsCategoryService, FmsServiceImpl fmsService, FmsThreadServiceImpl fmsThreadService, FmsThreadKmsKnowledgeServiceImpl fmsThreadKmsKnowledgeService, FmsCommentServiceImpl fmsCommentService, FmsThreadTypeServiceImpl threadTypeService) {
        this.fmsCategoryService = fmsCategoryService;
        this.fmsService = fmsService;
        this.fmsThreadService = fmsThreadService;
        this.fmsThreadKmsKnowledgeService = fmsThreadKmsKnowledgeService;
        this.fmsCommentService = fmsCommentService;
        this.threadTypeService = threadTypeService;
    }

    public List<FmsCategory> getAllCategories() {
        return fmsCategoryService.list();
    }

    public List<Fms> getAllThreadByCategoryIdAndTypeId(int categoryId, int typeId, boolean sort, boolean asc, boolean byCreateTime) {
        QueryWrapper<Fms> queryWrapper = new QueryWrapper<>();
        queryWrapper
                .eq("categoryId", categoryId)
                .eq("enabled", 1)
                .orderBy(sort, asc, byCreateTime? "createTime": "modifyTime");
        if (typeId != 0)
            queryWrapper.eq("typeId", typeId);
        return fmsService.list(queryWrapper);
    }

    public int addNewThread(FmsThread fmsThread) {
        fmsThreadService.save(fmsThread);
        return fmsThread.getId();
    }

    public void addRelatedKnowledge(int t, List<Integer> k) {
        List<FmsThreadKmsKnowledge> fmsThreadKmsKnowledgeList = new ArrayList<>();
        for (Integer integer : k) {
            FmsThreadKmsKnowledge fmsThreadKmsKnowledge = new FmsThreadKmsKnowledge();
            fmsThreadKmsKnowledge.setThreadid(t);
            fmsThreadKmsKnowledge.setKnowledgeid(integer);
            fmsThreadKmsKnowledge.setEnabled(1);
            fmsThreadKmsKnowledgeList.add(fmsThreadKmsKnowledge);
        }
        fmsThreadKmsKnowledgeService.saveBatch(fmsThreadKmsKnowledgeList);
    }

    public List<FmsThreadKmsKnowledge> getRelatedKnowledge(int threadId) {
        QueryWrapper<FmsThreadKmsKnowledge> queryWrapper = new QueryWrapper<>();
        queryWrapper
                .eq("threadId", threadId)
                .eq("enabled", 1);
        return fmsThreadKmsKnowledgeService.list(queryWrapper);
    }

    public int addNewComment(FmsComment fmsComment) {
        fmsCommentService.save(fmsComment);
        return fmsComment.getId();
    }

    public List<FmsThreadType> getAllThreadTypes() {
        return threadTypeService.list();
    }

    public List<FmsComment> getAllCommentsByThreadId(int threadId) {
        QueryWrapper<FmsComment> queryWrapper = new QueryWrapper<>();
        queryWrapper
                .eq("threadId", threadId)
                .eq("enabled", 1);
        return fmsCommentService.list(queryWrapper);
    }

    public Fms getThreadById(int threadId) {
        return fmsService.getById(threadId);
    }
}
