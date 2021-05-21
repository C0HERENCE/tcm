package cn.ccwisp.tcm.service;

import cn.ccwisp.tcm.dto.FmsCommentDto;
import cn.ccwisp.tcm.dto.FmsResponse;
import cn.ccwisp.tcm.generated.domain.*;
import cn.ccwisp.tcm.generated.service.UmsService;
import cn.ccwisp.tcm.generated.service.impl.*;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.parameters.P;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class ForumService {
    private final FmsCategoryServiceImpl fmsCategoryService;
    private final FmsServiceImpl fmsService;
    private final FmsThreadServiceImpl fmsThreadService;
    private final FmsCommentServiceImpl fmsCommentService;
    private final FmsThreadTypeServiceImpl threadTypeService;
    private final UmsCommentAgreementServiceImpl commentAgreementService;
    private final AccountService accountService;
    private final UmsServiceImpl umsService;

    public ForumService(FmsCategoryServiceImpl fmsCategoryService, FmsServiceImpl fmsService, FmsThreadServiceImpl fmsThreadService, FmsCommentServiceImpl fmsCommentService, FmsThreadTypeServiceImpl threadTypeService, UmsCommentAgreementServiceImpl commentAgreementService, AccountService accountService, UmsServiceImpl umsService) {
        this.fmsCategoryService = fmsCategoryService;
        this.fmsService = fmsService;
        this.fmsThreadService = fmsThreadService;
        this.fmsCommentService = fmsCommentService;
        this.threadTypeService = threadTypeService;
        this.commentAgreementService = commentAgreementService;
        this.accountService = accountService;
        this.umsService = umsService;
    }

    public List<FmsCategory> getAllCategories() {
        QueryWrapper<FmsCategory> queryWrapper = new QueryWrapper<FmsCategory>()
                .eq("enabled", 1);
        return fmsCategoryService.list(queryWrapper);
    }

    public List<Fms> getAllThreadByCategoryIdAndTypeId(int categoryId, int typeId, boolean sort, boolean asc, boolean byCreateTime) {
        QueryWrapper<Fms> queryWrapper = new QueryWrapper<>();
        queryWrapper
                .eq("categoryId", categoryId)
                .eq("enabled", 1)
                .eq("categoryEnabled", 1)
                .orderBy(sort, asc, byCreateTime ? "createTime" : "modifyTime");
        if (typeId != 0)
            queryWrapper.eq("typeId", typeId);
        return fmsService.list(queryWrapper);
    }

    public int addNewThread(FmsThread fmsThread) {
        fmsThreadService.save(fmsThread);
        return fmsThread.getId();
    }

    public int addNewComment(FmsComment fmsComment) {
        fmsCommentService.save(fmsComment);
        return fmsComment.getId();
    }

    public List<FmsThreadType> getAllThreadTypes() {
        return threadTypeService.list();
    }

    public List<FmsCommentDto> getAllCommentsByThreadId(int threadId, int userId) {
        QueryWrapper<FmsComment> queryWrapper = new QueryWrapper<>();
        queryWrapper
                .eq("threadId", threadId)
                .eq("enabled", 1);
        List<FmsCommentDto> result = new ArrayList<>();
        for (FmsComment fmsComment : fmsCommentService.list(queryWrapper)) {
            FmsCommentDto dto = new FmsCommentDto();
            UmsUserinfo author = accountService.getUserInfoByUserId(fmsComment.getAuthorid());
            dto.setAvatar(author.getAvatar());
            dto.setFmsComment(fmsComment);
            dto.setNickname(author.getNickname());
            dto.setAgreed(-1);
            if (userId > 0) {
                UmsCommentAgreement commentAgreement = commentAgreementService.getOne(
                        new QueryWrapper<UmsCommentAgreement>()
                                .eq("userId", userId)
                                .eq("commentId", fmsComment.getId()));
                if (commentAgreement != null) dto.setAgreed(commentAgreement.getAgreement());
            }
            result.add(dto);
        }
        return result;
    }

    public Fms getThreadById(int threadId) {
        return fmsService.getById(threadId);
    }

    public boolean updateUserCommentAgreement(int userId, int commentId, int agreeInt) {
        QueryWrapper<UmsCommentAgreement> queryWrapper = new QueryWrapper<>();
        queryWrapper
                .eq("userId", userId)
                .eq("commentId", commentId);
        UmsCommentAgreement commentAgreement = commentAgreementService.getOne(queryWrapper);
        FmsComment fmsComment = fmsCommentService.getById(commentId);

        if (commentAgreement == null) {
            commentAgreement = new UmsCommentAgreement();
            commentAgreement.setCommentid(commentId);
            commentAgreement.setUserid(userId);
            commentAgreement.setId(0);
            commentAgreement.setAgreement(agreeInt);
            commentAgreementService.save(commentAgreement);
            if (agreeInt == 1)
                fmsComment.setAgreecount(fmsComment.getAgreecount() + 1);
            else if (agreeInt == 0)
                fmsComment.setDisagreecount(fmsComment.getDisagreecount() + 1);
            fmsCommentService.updateById(fmsComment);
            return true;
        }
        if (commentAgreement.getAgreement() == agreeInt)
            return false;
        if (commentAgreement.getAgreement() == 1) // 用户目前是点赞这个评论的
            // 如果是要点踩或者要取消赞。
            fmsComment.setAgreecount(fmsComment.getAgreecount() - 1); // 将取消赞
        if (agreeInt == 0) // 如果是要点踩
            fmsComment.setDisagreecount(fmsComment.getDisagreecount() + 1);
        if (commentAgreement.getAgreement() == 0) // 用户目前是反对这个评论的
            // 如果是要点赞或者取消踩
            fmsComment.setDisagreecount(fmsComment.getDisagreecount() - 1); // 将取消反对
        if (agreeInt == 1) // 如果是要点赞
            fmsComment.setAgreecount(fmsComment.getAgreecount() + 1);
        fmsCommentService.updateById(fmsComment);
        commentAgreement.setAgreement(agreeInt);
        commentAgreementService.updateById(commentAgreement);
        return true;
    }

    public Ums GetAuthorByThreadId(int threadId) {
        Fms fms = fmsService.getById(threadId);
        return umsService.getById(fms.getAuthorid());
    }

    public Ums GetPersonInfo(int id) {
        return umsService.getById(id);
    }

    public List<Fms> getFmsByKeyword(String keyword) {
        QueryWrapper<Fms> queryWrapper = new QueryWrapper<>();
        List<String> fields = Arrays.asList("content", "categoryTitle", "title", "nickname", "topic");
        Map<String, List<String>> andList = new HashMap<>();
        Map<String, List<String>> orList = new HashMap<>();

        for (String s : keyword.split(" ")) {
            if (s.trim().length() <= 0) continue;
            String[] split = s.split(":");
            if (split.length == 2 && fields.contains(split[0])) {
                andList.computeIfAbsent(split[0], k -> new ArrayList<>());
                andList.get(split[0]).add(split[1]);
                continue;
            }
            for (String field : fields) {
                orList.computeIfAbsent(field, k -> new ArrayList<>());
                orList.get(field).add(s);
            }
        }

        queryWrapper.eq("enabled", 1);
        queryWrapper.orderBy(true, false, "modifyTime");
        if (orList.size() > 0) {
            queryWrapper.and(i ->  // i: queryWrapper
                    orList.forEach((field, kwds) -> // field 字段
                            kwds.forEach(kwd ->  // kwd 关键词
                                    i.or().like(field, kwd))));
        }
        if (andList.size() > 0)
            andList.forEach((key, value) -> value.forEach(kwd -> queryWrapper.like(key, kwd)));
        return fmsService.list(queryWrapper);
    }
}
