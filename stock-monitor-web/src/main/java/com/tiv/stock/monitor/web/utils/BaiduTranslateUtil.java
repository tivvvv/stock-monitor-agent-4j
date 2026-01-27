package com.tiv.stock.monitor.web.utils;

import cn.hutool.crypto.digest.DigestUtil;
import jakarta.annotation.Resource;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.HashMap;
import java.util.Map;

/**
 * 百度翻译工具类
 */
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

    public String translateEnToCn(String text) {
        return getTransResult(text, "en", "zh");
    }

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

        String salt = String.valueOf(System.currentTimeMillis());
        params.put("salt", salt);

        String src = appId + query + salt + secretKey;
        params.put("sign", DigestUtil.md5Hex(src));

        return params;
    }

}