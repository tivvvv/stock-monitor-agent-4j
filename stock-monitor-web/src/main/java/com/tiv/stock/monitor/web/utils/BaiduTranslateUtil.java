package com.tiv.stock.monitor.web.utils;

import cn.hutool.crypto.digest.DigestUtil;
import com.tiv.stock.monitor.web.common.Constants;
import jakarta.annotation.Resource;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 百度翻译工具类
 */
@Slf4j
@Component
@NoArgsConstructor
public class BaiduTranslateUtil {

    @Value("${translate.url}")
    private String url;

    @Value("${translate.appId}")
    private String appId;

    @Value("${translate.secretKey}")
    private String secretKey;

    @Resource
    private RestTemplate restTemplate;

    /**
     * 批量英译中
     */
    public String translateEnToCnBatch(List<String> texts) {
        return getTransResultBatch(texts, "en", "zh");
    }

    /**
     * 英译中
     */
    public String translateEnToCn(String text) {
        return getTransResult(text, "en", "zh");
    }

    /**
     * 批量翻译多个文本
     */
    public String getTransResultBatch(List<String> texts, String from, String to) {
        String combinedText = String.join("\n", texts);
        // 检查是否超过字符限制
        if (combinedText.length() <= Constants.TRANSLATE_MAX_LENGTH) {
            // 未超过限制,直接翻译
            return getTransResult(combinedText, from, to);
        } else {
            // 超过限制,分批处理
            StringBuilder sb = new StringBuilder();
            List<String> currentBatch = new ArrayList<>();
            int currentLength = 0;

            for (String text : texts) {
                // 当前批次长度 + 换行符长度 + 新文本长度
                int newLength = currentLength + (currentBatch.isEmpty() ? 0 : 1) + text.length();
                if (newLength <= Constants.TRANSLATE_MAX_LENGTH) {
                    // 添加到当前批次
                    currentBatch.add(text);
                    currentLength = newLength;
                } else {
                    // 当前批次已满,处理当前批次
                    if (!currentBatch.isEmpty()) {
                        String batchResult = getTransResult(String.join("\n", currentBatch), from, to);
                        sb.append(batchResult);

                        // 将当前文本加入新批次
                        currentBatch.clear();
                        currentBatch.add(text);
                        currentLength = text.length();
                    } else {
                        // 当前批次为空,单个文本就超过限制
                        log.warn("getTransResultBatch--单个文本超过翻译长度限制: " + text);
                        continue;
                    }
                }
            }

            // 处理最后一个批次
            if (!currentBatch.isEmpty()) {
                String batchResult = getTransResult(String.join("\n", currentBatch), from, to);
                sb.append(batchResult);
            }

            return sb.toString();
        }
    }

    /**
     * 翻译单个文本
     */
    public String getTransResult(String text, String from, String to) {
        Map<String, String> params = buildParams(text, from, to);
        MultiValueMap<String, String> requestParams = new LinkedMultiValueMap<>();
        requestParams.setAll(params);
        UriComponentsBuilder builder = UriComponentsBuilder.fromUriString(url);
        URI uri = builder.queryParams(requestParams).build().encode().toUri();
        return restTemplate.getForObject(uri, String.class);
    }

    private Map<String, String> buildParams(String query, String from, String to) {
        Map<String, String> params = new HashMap<>();
        params.put("q", query);
        params.put("from", from);
        params.put("to", to);

        params.put("appid", appId);

        // 随机数
        String salt = String.valueOf(System.currentTimeMillis());
        params.put("salt", salt);

        // 签名
        String src = appId + query + salt + secretKey;
        params.put("sign", DigestUtil.md5Hex(src));

        return params;
    }

}