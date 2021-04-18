package cn.ccwisp.tcm.controller;

import cn.ccwisp.tcm.bo.TcmUserDetails;
import cn.ccwisp.tcm.common.api.CommonResult;
import cn.ccwisp.tcm.dto.FmsDetailResponse;
import cn.ccwisp.tcm.dto.FmsResponse;
import cn.ccwisp.tcm.dto.NewReplyRequest;
import cn.ccwisp.tcm.dto.NewThreadRequest;
import cn.ccwisp.tcm.generated.domain.*;
import cn.ccwisp.tcm.service.ForumService;
import cn.ccwisp.tcm.service.RedisService;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@RestController
@RequestMapping("forum")
public class ForumController {
    private final ForumService forumService;
    private final RedisService redisService;

    public ForumController(ForumService forumService, RedisService redisService) {
        this.forumService = forumService;
        this.redisService = redisService;
    }

    private CommonResult<List<FmsResponse>> getFmsResponseListCommonResult(List<Fms> fmsList) {
        List<FmsResponse> fmsResponseList = new ArrayList<>();
        for (Fms fms : fmsList) {
            FmsResponse fmsResponse = new FmsResponse();
            fmsResponse.setThread(fms);
            fmsResponse.setFav(redisService.GetFavOfThread(fms.getId()));
            fmsResponse.setViews(redisService.GetViewsOfThread(fms.getId()));
            fmsResponse.setLike(redisService.GetLikeOfThread(fms.getId()));
            fmsResponse.setHaveFav(false);
            fmsResponse.setHaveLike(false);
            if (SecurityContextHolder.getContext().getAuthentication().getPrincipal().getClass().getName().equals(TcmUserDetails.class.getName())) {
                TcmUserDetails principal = (TcmUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
                if (principal != null) {
                    fmsResponse.setHaveFav(redisService.GetUserFavThreadId(principal.getUserId()).contains(fms.getId()));
                    fmsResponse.setHaveLike(redisService.GetUserLikedThreadId(principal.getUserId()).contains(fms.getId()));
                }
            }
            fmsResponseList.add(fmsResponse);
        }
        return CommonResult.success(fmsResponseList);
    }

    // 获取所有分类
    @GetMapping("/category")
    public CommonResult<List<FmsCategory>> getAllCategories() {
        return CommonResult.success(forumService.getAllCategories());
    }

    // 获取所有类型
    @GetMapping("/type")
    public CommonResult<List<FmsThreadType>> getAllThreadTypes() {
        return CommonResult.success(forumService.getAllThreadTypes());
    }

    // 获取分类下的帖子
    @GetMapping("/thread")
    public CommonResult<List<FmsResponse>> getAllThreadOfCategoryIdAndTypeId(
            @RequestParam(value = "categoryId", required = false, defaultValue = "-1") int CategoryId,
            @RequestParam(value = "typeId", required = false, defaultValue = "0") int typeId,
            @RequestParam(value = "sort", required = false, defaultValue = "false") boolean sort,
            @RequestParam(value = "asc", required = false, defaultValue = "false") boolean asc,
            @RequestParam(value = "byCreateTime", required = false, defaultValue = "false") boolean byCreateTime) {
        if (CategoryId == -1)
            return CommonResult.badRequest();
        List<Fms> fmsList = forumService.getAllThreadByCategoryIdAndTypeId(CategoryId, typeId, sort, asc, byCreateTime);
        return getFmsResponseListCommonResult(fmsList);
    }

    // 获取一个帖子
    @GetMapping("/thread/{threadId}")
    public CommonResult<FmsDetailResponse> getThreadDetailById(@PathVariable("threadId") int threadId) {
        FmsDetailResponse fmsDetailResponse = new FmsDetailResponse();
        Fms fms = forumService.getThreadById(threadId);
        if (fms == null)
            return CommonResult.badRequest("帖子不存在");
        fmsDetailResponse.setThread(fms);
        fmsDetailResponse.setFav(redisService.GetFavOfThread(fms.getId()));
        fmsDetailResponse.setViews(redisService.GetViewsOfThread(fms.getId()));
        fmsDetailResponse.setLiked(redisService.GetLikeOfThread(fms.getId()));
        fmsDetailResponse.setFmsComments(forumService.getAllCommentsByThreadId(threadId));
        fmsDetailResponse.setFmsThreadKmsKnowledgeList(forumService.getRelatedKnowledge(threadId));
        return CommonResult.success(fmsDetailResponse);
    }

    // 发布一个帖子
    @PostMapping("/thread/post")
    public CommonResult<Object> postThread(@RequestBody NewThreadRequest newThreadRequest) {
        TcmUserDetails principal = (TcmUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        FmsThread fmsThread = new FmsThread();
        fmsThread.setId(0);
        fmsThread.setAuthorid(principal.getUserId());
        fmsThread.setTitle(newThreadRequest.getTitle());
        fmsThread.setCreatetime(new Date());
        fmsThread.setModifytime(new Date());
        fmsThread.setContent(newThreadRequest.getContent());
        fmsThread.setCategoryid(newThreadRequest.getCategoryId());
        fmsThread.setTypeid(1); // TODO
        fmsThread.setEnabled(1);
        int threadId = forumService.addNewThread(fmsThread);
        forumService.addRelatedKnowledge(threadId, newThreadRequest.getRelatedKnowledge());
        return CommonResult.success("发布成功");
    }

    // 发布一个评论
    @PostMapping("/thread/reply")
    public CommonResult<Object> postReply(@RequestBody NewReplyRequest newReplyRequest) {
        TcmUserDetails principal = (TcmUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        FmsComment fmsComment = new FmsComment();
        fmsComment.setId(0);
        fmsComment.setThreadid(newReplyRequest.getThreadId());
        fmsComment.setAgreecount(0);
        fmsComment.setDisagreecount(0);
        fmsComment.setAuthorid(principal.getUserId());
        fmsComment.setCreatetime(new Date());
        fmsComment.setContent(newReplyRequest.getContent());
        fmsComment.setEnabled(1);
        return forumService.addNewComment(fmsComment) > 0 ? CommonResult.success("回复成功") : CommonResult.badRequest("回复失败");
    }

    // 浏览过一个帖子
    @PostMapping("/thread/view/{threadId}")
    public CommonResult<Object> viewThread(@PathVariable("threadId") int threadId) {
        TcmUserDetails principal = (TcmUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        redisService.addUserHistory(principal.getUserId(), threadId);
        redisService.IncrementViewsCount(threadId);
        return CommonResult.success("ok");
    }

    // 点赞一个帖子
    @PostMapping("/thread/like/{threadId}")
    public CommonResult<Object> likeThread(@PathVariable("threadId") int threadId) {
        TcmUserDetails principal = (TcmUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (redisService.AddUserLikedThread(principal.getUserId(), threadId)) {
            return CommonResult.success("操作成功");
        }
        return CommonResult.badRequest("操作失败");
    }

    // 收藏一个帖子
    @PostMapping("/thread/fav/{threadId}")
    public CommonResult<Object> favThread(@PathVariable("threadId") int threadId) {
        TcmUserDetails principal = (TcmUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (redisService.AddUserFavThread(principal.getUserId(), threadId)) {
            return CommonResult.success("操作成功");
        }
        return CommonResult.badRequest("操作失败");
    }

    // 取消点赞一个帖子
    @PostMapping("/thread/dislike/{threadId}")
    public CommonResult<Object> disLikeThread(@PathVariable("threadId") int threadId) {
        TcmUserDetails principal = (TcmUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (redisService.RemoveUserLikedThread(principal.getUserId(), threadId)) {
            return CommonResult.success("操作成功");
        }
        return CommonResult.badRequest("操作失败");
    }

    // 取消收藏一个帖子
    @PostMapping("/thread/disfav/{threadId}")
    public CommonResult<Object> disFavThread(@PathVariable("threadId") int threadId) {
        TcmUserDetails principal = (TcmUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (redisService.RemoveUserFavThread(principal.getUserId(), threadId)) {
            return CommonResult.success("操作成功");
        }
        return CommonResult.badRequest("操作失败");
    }
}
