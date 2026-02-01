package com.tiv.stock.monitor.mcp.tools;

import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * MCP 时间工具类
 */
@Slf4j
@Component
public class DateTimeTool {

    @Tool(description = "获取当前时间")
    public String getCurrentTime() {
        log.info("========== 调用MCP工具: 获取当前时间 getCurrentTime() ==========");
        String currentTime = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        return String.format("当前时间是: %s", currentTime);
    }

}
