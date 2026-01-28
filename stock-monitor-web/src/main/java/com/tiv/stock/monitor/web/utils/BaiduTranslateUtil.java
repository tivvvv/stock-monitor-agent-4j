package com.tiv.stock.monitor.web.utils;

import cn.hutool.core.annotation.Alias;
import cn.hutool.core.map.MapUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.crypto.digest.DigestUtil;
import cn.hutool.json.JSONUtil;
import com.tiv.stock.monitor.web.common.Constants;
import jakarta.annotation.Resource;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.net.URI;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.stream.Collectors;

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
    public Map<String, String> translateEnToCnBatch(List<String> texts) {
        return getTransResultBatch(texts, "en", "zh");
    }

    /**
     * 英译中
     */
    public String translateEnToCn(String text) {
        return getTransResult(text, "en", "zh").get(text);
    }

    /**
     * 批量翻译多个文本
     */
    public Map<String, String> getTransResultBatch(List<String> texts, String from, String to) {
        String combinedText = String.join("\n", texts);
        // 检查是否超过字符限制
        if (combinedText.length() <= Constants.TRANSLATE_MAX_LENGTH) {
            // 未超过限制,直接翻译
            return getTransResult(combinedText, from, to);
        } else {
            // 超过限制,分批处理
            Map<String, String> result = new HashMap<>();
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
                        result.putAll(getTransResult(String.join("\n", currentBatch), from, to));
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
                result.putAll(getTransResult(String.join("\n", currentBatch), from, to));
            }

            return result;
        }
    }

    /**
     * 翻译单个文本
     */
    public Map<String, String> getTransResult(String text, String from, String to) {
        Map<String, String> params = buildParams(text, from, to);

        String sendUrl = getUrlWithQueryStringEncoded(url, params);
        URI uri = URI.create(sendUrl);
        String result = restTemplate.getForObject(uri, String.class);

        if (StrUtil.isBlank(result)) {
            log.error("getTransResult--翻译结果为空, params:[{}]", params);
            return Collections.emptyMap();
        }
        if (result.contains("error_code")) {
            BaiduTranslateError error = JSONUtil.toBean(result, BaiduTranslateError.class);
            log.error("getTransResult--翻译失败, errorCode:[{}], errorMsg:[{}], params:[{}]",
                    error.getErrorCode(), error.getErrorMsg(), params);
            return Collections.emptyMap();
        }
        BaiduTranslateResult translateResult = JSONUtil.toBean(result, BaiduTranslateResult.class);
        return translateResult.getTransResults().stream()
                .collect(Collectors.toMap(
                        BaiduTranslateResult.TransResult::getSrc,
                        BaiduTranslateResult.TransResult::getDst
                ));
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

    private String getUrlWithQueryStringEncoded(String url, Map<String, String> params) {
        if (MapUtil.isEmpty(params)) {
            return url;
        }

        StringBuilder builder = new StringBuilder(url);
        if (url.contains("?")) {
            builder.append("&");
        } else {
            builder.append("?");
        }

        int i = 0;
        for (String key : params.keySet()) {
            String value = params.get(key);
            if (value == null) {
                continue;
            }

            if (i != 0) {
                builder.append('&');
            }

            builder.append(key);
            builder.append('=');
            builder.append(URLEncoder.encode(value, StandardCharsets.UTF_8));

            i++;
        }
        return builder.toString();
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    private static class BaiduTranslateResult {

        private String from;

        private String to;

        @Alias("trans_result")
        private List<TransResult> transResults;

        @Data
        @NoArgsConstructor
        @AllArgsConstructor
        public static class TransResult {
            private String src;
            private String dst;
        }

    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    private static class BaiduTranslateError {

        @Alias("error_code")
        private String errorCode;

        @Alias("error_msg")
        private String errorMsg;

    }


}