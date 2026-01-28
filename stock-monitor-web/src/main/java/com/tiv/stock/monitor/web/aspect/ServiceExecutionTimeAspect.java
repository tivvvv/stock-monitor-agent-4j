package com.tiv.stock.monitor.web.aspect;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;
import org.springframework.util.StopWatch;

/**
 * 服务执行时间切面
 */
@Slf4j
@Aspect
@Component
public class ServiceExecutionTimeAspect {

    @Around("execution(* com.tiv.stock.monitor.web.service.impl..*.*(..))")
    public Object recordTimeLog(ProceedingJoinPoint joinPoint) throws Throwable {

        StopWatch stopWatch = new StopWatch();
        stopWatch.start();

        Object proceed = joinPoint.proceed();
        String pointCut = joinPoint.getTarget().getClass().getName() + "." + joinPoint.getSignature().getName();

        stopWatch.stop();
        long executeTime = stopWatch.getTotalTimeMillis();

        if (executeTime > 10_000) {
            log.error("[缓慢方法]: {} took {} ms, args: {}", pointCut, executeTime, joinPoint.getArgs());
        } else if (executeTime > 5_000) {
            log.warn("[较慢方法]: {} took {} ms, args: {}", pointCut, executeTime, joinPoint.getArgs());
        } else {
            log.debug("[正常方法]: {} took {} ms, args: {}", pointCut, executeTime, joinPoint.getArgs());
        }
        return proceed;
    }

}
