package cn.ccwisp.tcm.service;


import cn.ccwisp.tcm.bo.TcmUserDetails;
import cn.ccwisp.tcm.common.api.CommonResult;
import cn.ccwisp.tcm.common.exception.Asserts;
import cn.ccwisp.tcm.dto.FmsCommentDto;
import cn.ccwisp.tcm.generated.domain.*;
import cn.ccwisp.tcm.generated.service.impl.*;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.sun.mail.imap.protocol.ID;
import org.elasticsearch.action.update.UpdateRequest;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.*;

@Service
public class AdminService {

    private final UmsUserRoleServiceImpl userRoleService;
    private final UmsRolesServiceImpl rolesService;
    private final UmsRoleServiceImpl umsRoleService;
    private final UmsUserServiceImpl umsUserService;
    private final UmsServiceImpl umsService;
    private final RestHighLevelClient restHighLevelClient;
    private final FmsThreadServiceImpl threadService;
    private final FmsThreadTypeServiceImpl threadTypeService;
    private final FmsCommentServiceImpl commentService;
    private final FmsCategoryServiceImpl categoryService;
    private final KmsFeedbackServiceImpl kmsFeedbackService;

    public HashMap<String, Integer> RoleMap;
    public HashMap<AdminOp, String> LogMap;

    public List<KmsFeedback> getAllFeedback() {
        return kmsFeedbackService.list(new QueryWrapper<KmsFeedback>().orderByDesc("createtime"));
    }

    public void processFeedback(int id, String note) {
        TcmUserDetails user = (TcmUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        KmsFeedback byId = kmsFeedbackService.getById(id);
        byId.setNote(note);
        byId.setProcessed(1);
       kmsFeedbackService.updateById(byId);
    }

    public List<Ums> getAllAdmins() {
        return umsService.list(new QueryWrapper<Ums>().eq("isAdmin", 1));
    }

    public List<UmsRoles> getUserRolesById(int userId) {
        return rolesService.list(new QueryWrapper<UmsRoles>().eq("userId", userId));
    }
    private final FmsCommentServiceImpl fmsCommentService;
    private final AccountService accountService;
    public List<FmsCommentDto> getAllCommentsByThreadId(Integer threadId) {
        QueryWrapper<FmsComment> queryWrapper = new QueryWrapper<>();
        queryWrapper
                .eq("threadId", threadId);
        List<FmsCommentDto> result = new ArrayList<>();
        for (FmsComment fmsComment : fmsCommentService.list(queryWrapper)) {
            FmsCommentDto dto = new FmsCommentDto();
            UmsUserinfo author = accountService.getUserInfoByUserId(fmsComment.getAuthorid());
            dto.setAvatar(author.getAvatar());
            dto.setFmsComment(fmsComment);
            dto.setNickname(author.getNickname());
            dto.setAgreed(-1);
            dto.setEnabled(fmsComment.getEnabled());
            result.add(dto);
        }
        return result;
    }


    public enum AdminOp {
        Es,
        UserEnable,
        ThreadDelete,
        CommentDelete,
        Feedback,
    }

    public AdminService(UmsUserRoleServiceImpl userRoleService, UmsUserServiceImpl umsUserService, RestHighLevelClient restHighLevelClient, UmsAdminLogsServiceImpl adminLogsService, FmsCategoryServiceImpl fmsCategoryService, FmsThreadServiceImpl fmsThreadService, FmsThreadTypeServiceImpl threadTypeService, FmsCommentServiceImpl commentService, FmsServiceImpl fmsService, KmsFeedbackServiceImpl kmsFeedbackService, UmsRolesServiceImpl rolesService, UmsServiceImpl umsService, UmsRoleServiceImpl umsRoleService, FmsCommentServiceImpl fmsCommentService, AccountService accountService) {
        RoleMap = new HashMap<>();
        RoleMap.put("comment", 1);
        RoleMap.put("post", 2);
        RoleMap.put("action", 3);
        RoleMap.put("userAdmin", 4);
        RoleMap.put("knowledgeAdmin", 5);
        RoleMap.put("forumAdmin", 6);
        this.userRoleService = userRoleService;
        this.umsUserService = umsUserService;
        this.restHighLevelClient = restHighLevelClient;
        LogMap = new HashMap<>();
        LogMap.put(AdminOp.Es, "1");
        LogMap.put(AdminOp.CommentDelete, "2");
        LogMap.put(AdminOp.Feedback, "3");
        LogMap.put(AdminOp.ThreadDelete, "4");
        LogMap.put(AdminOp.UserEnable, "5");
        this.categoryService = fmsCategoryService;
        this.threadService = fmsThreadService;
        this.threadTypeService = threadTypeService;
        this.commentService = commentService;
        this.fmsService = fmsService;
        this.kmsFeedbackService = kmsFeedbackService;
        this.rolesService = rolesService;
        this.umsService = umsService;
        this.umsRoleService = umsRoleService;
        this.fmsCommentService = fmsCommentService;
        this.accountService = accountService;
    }

    public List<UmsUserRole> getUserRoles(int userId) {
        QueryWrapper<UmsUserRole> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("userId", userId);
        return userRoleService.list(queryWrapper);
    }

    public void setRole(int userId, List<String> roles) {
        userRoleService.remove(new QueryWrapper<UmsUserRole>().eq("userId", userId));
        for (String role : roles) {
            UmsRole roleModel = umsRoleService.getOne(new QueryWrapper<UmsRole>().eq("name", role));
            UmsUserRole umsUserRole = new UmsUserRole();
            umsUserRole.setCreatetime(new Date());
            umsUserRole.setRoleid(roleModel.getId());
            umsUserRole.setUserid(userId);
            userRoleService.save(umsUserRole);
        }
    }

    // 授予权限（用户id， 权限id）
    public void addRole(int userId, String roleName) {
        UmsUserRole one = userRoleService.getOne(new QueryWrapper<UmsUserRole>()
                .eq("roleId", roleName)
                .eq("userId", userId));
        if (one == null) {
            UmsUserRole umsUserRole = new UmsUserRole();
            umsUserRole.setCreatetime(new Date());
            umsUserRole.setRoleid(Integer.valueOf(roleName));
            umsUserRole.setUserid(userId);
            userRoleService.save(umsUserRole);
        }
    }

    // 移除权限（用户id， 权限id）
    public void removeRole(int userId, String roleName) {
        QueryWrapper<UmsUserRole> eq = new QueryWrapper<UmsUserRole>()
                .eq("roleId", roleName)
                .eq("userId", userId);
        UmsUserRole one = userRoleService.getOne(eq);
        if (one != null) {
            userRoleService.remove(eq);
        }
    }

    // 禁用用户， 启用用户
    public void setUserEnable(int userId, int enabled) {
        UmsUser byId = umsUserService.getById(userId);
        if (byId.getEnabled() == enabled)
            return;
        byId.setEnabled(enabled);
        umsUserService.updateById(byId);
    }

    public int updateEs(String index, String id, String field, String text) throws IOException {
        Map<String, Object> m = new HashMap<>();
        m.put(field, text);
        restHighLevelClient.update(new UpdateRequest(index, id).doc(m), RequestOptions.DEFAULT);
        return 1;
    }

    // 修改分类标题
    public void updateCategory(int CategoryId, String title, String english, String intro) {
        FmsCategory byId = categoryService.getById(CategoryId);
        if (byId == null)
            Asserts.fail("该分类不存在");
        byId.setTitle(title);
        byId.setIntro(intro);
        byId.setEnglish(english);
        categoryService.updateById(byId);
    }

    // 添加一个分类
    public void AddCategory(String title, String english, String intro) {
        FmsCategory fmsCategory = new FmsCategory();
        fmsCategory.setIntro(intro);
        fmsCategory.setTitle(title);
        fmsCategory.setId(0);
        fmsCategory.setEnglish(english);
        fmsCategory.setEnabled(0);
        categoryService.save(fmsCategory);
    }

    // 给文章标签
    public void SetThreadType(int threadId, int TypeId) {
        FmsThread byId = threadService.getById(threadId);
        FmsThreadType byId1 = threadTypeService.getById(TypeId);
        if (byId1 == null)
            Asserts.fail("该类型不存在");
        if (byId == null)
            Asserts.fail("该分类不存在");
        byId.setTypeid(TypeId);
        threadService.save(byId);
    }

    // 文章删除
    public void DeleteThread(int id, int enabled) {
        FmsThread byId = threadService.getById(id);
        byId.setEnabled(enabled);
        threadService.updateById(byId);
    }

    // 评论删除
    public void DeleteComment(int id, int enabled) {
        FmsComment byId = commentService.getById(id);
        byId.setEnabled(enabled);
        commentService.updateById(byId);
    }

    public List<FmsCategory> GetAllCategories() {
        return categoryService.list();
    }


    public void enableCategory(int categoryId, int enabled) {
        FmsCategory byId = categoryService.getById(categoryId);
        byId.setEnabled(enabled);
        categoryService.updateById(byId);
    }

    private final FmsServiceImpl fmsService;

    public List<Fms> getThreads(String keyword, int categoryId, int TypeId, int authorId) {
        QueryWrapper<Fms> queryWrapper = new QueryWrapper<Fms>();
        queryWrapper.like("title", keyword);
        if (categoryId != 0) {
            queryWrapper.eq("categoryId", categoryId);
        }
        if (TypeId != 0) {
            queryWrapper.eq("typeId", TypeId);
        }
        if (authorId != 0) {
            queryWrapper.eq("authorId", authorId);
        }
        return fmsService.list(queryWrapper);
    }

    public Map<String, Object> GetForumStatistics(){
        Calendar calendar = Calendar.getInstance();

        calendar.setTime(new Date());
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        Date zero = calendar.getTime();

        calendar.add(Calendar.DATE, -1);
        Date time = calendar.getTime();
        Map<String, Object> map = new HashMap<>();
        map.put("用户总数", umsUserService.count());
        map.put("帖子总数", threadService.count());
        map.put("今日发布", threadService.count(new QueryWrapper<FmsThread>()
                .gt("createTime", zero)));
        map.put("昨日发布", threadService.count(new QueryWrapper<FmsThread>()
                .between("createTime", time, zero)
        ));
        return map;
    }
}
