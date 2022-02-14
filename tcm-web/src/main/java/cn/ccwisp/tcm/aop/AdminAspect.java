package cn.ccwisp.tcm.aop;

import cn.ccwisp.tcm.bo.TcmUserDetails;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component
@Aspect
public class AdminAspect {

    @Pointcut("execution(public * cn.ccwisp.tcm.controller.AdminController.*(..))")
    public void adminLog() {

    }

    @Around("adminLog()")
    public Object doAdminLog(ProceedingJoinPoint proceedingJoinPoint) throws Throwable {
        System.out.println("AOP 方法名" + proceedingJoinPoint.getSignature().getName());
        Object[] args = proceedingJoinPoint.getArgs();
        Object proceed = proceedingJoinPoint.proceed();
        TcmUserDetails tcmUserDetails = (TcmUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String username = tcmUserDetails.getUsername();
        System.out.println("操作用户是" + username);
//        proceed.
        return proceed;
    }
}
