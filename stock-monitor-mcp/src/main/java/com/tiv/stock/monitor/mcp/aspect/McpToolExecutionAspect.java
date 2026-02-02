package com.tiv.stock.monitor.mcp.aspect;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

/**
 * MCP 工具执行切面
 */
@Slf4j
@Aspect
@Component
public class McpToolExecutionAspect {

    @Around("@annotation(org.springframework.ai.tool.annotation.Tool)")
    public Object logToolExecution(ProceedingJoinPoint joinPoint) throws Throwable {
        String className = joinPoint.getTarget().getClass().getSimpleName();
        String methodName = joinPoint.getSignature().getName();

        log.info("========== 调用MCP工具: {}.{}() ==========", className, methodName);
        try {
            return joinPoint.proceed();
        } catch (Throwable throwable) {
            log.error("MCP工具调用异常: {}.{}()", className, methodName, throwable);
            throw throwable;
        }
    }

}