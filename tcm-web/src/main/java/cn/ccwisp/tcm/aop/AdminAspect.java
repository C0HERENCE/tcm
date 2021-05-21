package cn.ccwisp.tcm.aop;

import cn.ccwisp.tcm.bo.TcmUserDetails;
import cn.ccwisp.tcm.common.api.CommonResult;
import cn.ccwisp.tcm.generated.domain.UmsAdminLogs;
import cn.ccwisp.tcm.generated.service.impl.UmsAdminLogsServiceImpl;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.PostMapping;

import java.lang.annotation.Annotation;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

@Component
@Aspect
public class AdminAspect {

    private final UmsAdminLogsServiceImpl adminLogsService;

    public AdminAspect(UmsAdminLogsServiceImpl adminLogsService) {
        this.adminLogsService = adminLogsService;
    }

    @Pointcut("execution(public * cn.ccwisp.tcm.controller.AdminController.*(..))")
    public void adminLog() {
    }

    @Around("adminLog()")
    public Object doAdminLog(ProceedingJoinPoint proceedingJoinPoint) throws Throwable {
        MethodSignature signature = (MethodSignature) proceedingJoinPoint.getSignature(); // 获得方法签名
        boolean isPost = signature.getMethod().isAnnotationPresent(PostMapping.class); // 是否为POST请求
        CommonResult proceed = (CommonResult) proceedingJoinPoint.proceed(); // 方法执行，并得到返回值
        if (!isPost) return proceed;
        String methodName = signature.getName();// 得到方法名
        StringBuilder sb = new StringBuilder();
        String[] parameterNames = signature.getParameterNames();
        Object[] args = proceedingJoinPoint.getArgs(); // 得到所有参数
        for (int i = 0; i < args.length; i++) {
            sb.append(parameterNames[i]).append(": ").append(args[i]).append(",");
        }
        long status = proceed.getStatus(); // 得到执行结果 ， 200 = OK
        TcmUserDetails tcmUserDetails = new TcmUserDetails();
        try {
            tcmUserDetails = (TcmUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        } catch (Exception e) {
            return proceed;
        }
        String username = tcmUserDetails.getUsername(); // 得到操作用户名
        int userId = tcmUserDetails.getUserId();// 得到操作用户ID
        UmsAdminLogs entity = new UmsAdminLogs();

        entity.setId(0);
        entity.setCreatetime(new Date());
        entity.setAdminid(userId);
        entity.setAdminusername(username);
        entity.setArgs(sb.length() > 31?sb.substring(0, 30):sb.toString());
        entity.setMethodname(methodName);
        entity.setSucceed(status == 200 ? 1 : 0);
        adminLogsService.save(entity);
        return proceed;
    }
}
