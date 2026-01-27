package com.tiv.stock.monitor.web.aspect;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

/**
 * 服务执行时间切面
 */
@Slf4j
@Aspect
@Component
public class ServiceExecutionTimeAspect {

    @Around("execution(* com.tiv.stock.monitor.web.service.impl..*.*(..))")
    public Object recordTimeLog(ProceedingJoinPoint joinPoint) throws Throwable {

        long beginTime = System.currentTimeMillis();

        Object proceed = joinPoint.proceed();
        String pointCut = joinPoint.getTarget().getClass().getName() + "." + joinPoint.getSignature().getName();

        long endTime = System.currentTimeMillis();

        long executeTime = endTime - beginTime;

        if (executeTime > 5000) {
            log.error("SLOW: {} took {}ms, args: {}", pointCut, executeTime, joinPoint.getArgs());
        } else if (executeTime > 2000) {
            log.warn("MODERATE: {} took {}ms, args: {}", pointCut, executeTime, joinPoint.getArgs());
        } else {
            log.debug("FAST: {} took {}ms", pointCut, executeTime);
        }
        return proceed;
    }

}
