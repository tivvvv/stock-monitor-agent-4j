package com.tiv.stock.monitor.web.utils;

import com.dingtalk.api.DefaultDingTalkClient;
import com.dingtalk.api.DingTalkClient;
import com.dingtalk.api.request.OapiRobotSendRequest;
import com.dingtalk.api.response.OapiRobotSendResponse;
import com.taobao.api.ApiException;
import com.tiv.stock.monitor.web.common.Constants;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.binary.Base64;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.List;

/**
 * 钉钉工具类
 */
@Slf4j
@Component
@NoArgsConstructor
public class DingTalkUtil {

    @Value("${dingTalk.url}")
    private String url;

    @Value("${dingTalk.robotToken}")
    private String robotToken;

    @Value("${dingTalk.secretKey}")
    private String secretKey;

    public void sendDingTalkMessage(String textContent, List<String> userIds) {
        try {
            Long timestamp = System.currentTimeMillis();
            String signStr = String.format("%s\n%s", timestamp, secretKey);
            Mac mac = Mac.getInstance(Constants.HMAC_SHA256);
            mac.init(new SecretKeySpec(secretKey.getBytes(StandardCharsets.UTF_8), Constants.HMAC_SHA256));
            byte[] signData = mac.doFinal(signStr.getBytes(StandardCharsets.UTF_8));
            String sign = URLEncoder.encode(new String(Base64.encodeBase64(signData)), StandardCharsets.UTF_8);

            // 拼接请求
            String sendUrl = String.format("%s?sign=%s&timestamp=%s", url, sign, timestamp);
            log.info("sendDingTalkMessage--发送消息开始,url:{}", sendUrl);

            DingTalkClient client = new DefaultDingTalkClient(sendUrl);
            OapiRobotSendRequest req = new OapiRobotSendRequest();

            // 定义文本内容
            OapiRobotSendRequest.Text text = new OapiRobotSendRequest.Text();
            text.setContent(textContent);

            // 定义 @ 对象
            OapiRobotSendRequest.At at = new OapiRobotSendRequest.At();
            at.setAtUserIds(userIds);

            // 设置消息类型
            req.setMsgtype("text");
            req.setText(text);
            req.setAt(at);
            OapiRobotSendResponse rsp = client.execute(req, robotToken);
            log.info("sendDingTalkMessage--发送消息结果: {}", rsp.getBody());
        } catch (ApiException | NoSuchAlgorithmException | InvalidKeyException e) {
            log.error("sendDingTalkMessage--发送消息失败", e);
            throw new RuntimeException(e);
        }
    }

}
