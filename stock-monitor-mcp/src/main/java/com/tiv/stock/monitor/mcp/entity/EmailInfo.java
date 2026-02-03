package com.tiv.stock.monitor.mcp.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.ai.tool.annotation.ToolParam;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EmailInfo {

    @ToolParam(description = "收件人")
    private String receiver;

    @ToolParam(description = "邮件主题")
    private String subject;

    @ToolParam(description = "邮件内容")
    private String content;

    @ToolParam(description = "邮件内容类型Text/HTML/Markdown")
    private String contentType;

}
