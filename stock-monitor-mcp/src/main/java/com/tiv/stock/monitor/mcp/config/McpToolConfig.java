package com.tiv.stock.monitor.mcp.config;

import com.tiv.stock.monitor.mcp.tools.DateTimeTool;
import org.springframework.ai.tool.ToolCallbackProvider;
import org.springframework.ai.tool.method.MethodToolCallbackProvider;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * MCP 工具配置
 */
@Configuration
public class McpToolConfig {

    @Bean
    public ToolCallbackProvider mcpToolCallbackProvider(DateTimeTool dateTimeTool) {
        return MethodToolCallbackProvider.builder()
                .toolObjects(dateTimeTool)
                .build();
    }

}