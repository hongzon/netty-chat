package com.nettychat.server.base.aspect;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.util.Arrays;

/**
 * HttpAspect 用于拦截 Http 请求和响应
 *
 */
@Aspect
@Component
public class HttpAspect {

    private final static Logger logger = LoggerFactory.getLogger(HttpAspect.class);

    @Pointcut(value = "execution(public * com.nettychat.server.web.controller.*.*(..))")
    public void pointcut() {
    }

    @Before("pointcut()")
    public void doBefore(JoinPoint joinPoint) {
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        HttpServletRequest request = attributes.getRequest();
        logger.info("<-------- Request --------");
        logger.info("<--      HTTP: {} {}", request.getMethod(), request.getRequestURL());
        logger.info("<--        IP: {}", request.getRemoteAddr());
        logger.info("<-- SessionID: {}", request.getSession().getId());
        logger.info("<--    Method: {}", joinPoint.getSignature().getDeclaringTypeName() + "." + joinPoint.getSignature().getName());
        logger.info("<--      Args: {}", Arrays.toString(joinPoint.getArgs()));
        logger.info("<-------------------------");
    }

    @AfterReturning(returning = "response", pointcut = "pointcut()")
    public void doAfterReturning(Object response) {
        logger.info("-------- Response -------->");
        logger.info("-->    Result: {}", response);
        logger.info("-------------------------->");
    }

    @AfterThrowing(throwing = "exception", pointcut = "pointcut()")
    public void doAfterThrowing(Exception exception) {
        logger.info("-------- Response -------->");
        logger.info("--> Exception: {}", exception.getMessage());
        logger.info("-------------------------->");
    }
}
