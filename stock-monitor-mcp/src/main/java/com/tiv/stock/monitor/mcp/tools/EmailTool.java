package com.tiv.stock.monitor.mcp.tools;

import com.tiv.stock.monitor.mcp.entity.EmailInfo;
import com.vladsch.flexmark.html.HtmlRenderer;
import com.vladsch.flexmark.parser.Parser;
import com.vladsch.flexmark.util.data.MutableDataSet;
import jakarta.annotation.Resource;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;

import java.util.Date;

/**
 * MCP 邮件工具类
 */
@Slf4j
@Component
public class EmailTool {

    @Value("${spring.mail.username}")
    private String sender;

    @Resource
    private JavaMailSender javaMailSender;

    private static final String TYPE_TEXT = "Text";

    private static final String TYPE_HTML = "HTML";

    private static final String TYPE_MARK_DOWN = "Markdown";

    @Tool(description = "获取邮件工具的发件人")
    public String getSender() {
        return sender;
    }

    @Tool(description = "发送邮件")
    public void sendEmail(EmailInfo emailInfo) {
        try {
            MimeMessage mimeMessage = javaMailSender.createMimeMessage();
            MimeMessageHelper messageHelper = new MimeMessageHelper(mimeMessage, true);

            messageHelper.setFrom(sender);
            messageHelper.setTo(emailInfo.getReceiver());
            messageHelper.setSubject(emailInfo.getSubject());
            messageHelper.setSentDate(new Date());

            String contentType = emailInfo.getContentType();
            if (TYPE_HTML.equalsIgnoreCase(contentType)) {
                messageHelper.setText(emailInfo.getContent(), true);
            } else if (TYPE_MARK_DOWN.equalsIgnoreCase(contentType)) {
                messageHelper.setText(markDownToHtml(emailInfo.getContent()), true);
            } else {
                messageHelper.setText(emailInfo.getContent());
            }

            javaMailSender.send(mimeMessage);
        } catch (MessagingException e) {
            log.error("sendEmail--邮件发送失败, emailInfo: {}", emailInfo, e);
        }
    }

    /**
     * Markdown转HTML
     *
     * @param markdown
     * @return
     */
    private String markDownToHtml(String markdown) {
        MutableDataSet dataset = new MutableDataSet();
        Parser parser = Parser.builder(dataset).build();
        HtmlRenderer htmlRenderer = HtmlRenderer.builder(dataset).build();

        return htmlRenderer.render(parser.parse(markdown));
    }

}
