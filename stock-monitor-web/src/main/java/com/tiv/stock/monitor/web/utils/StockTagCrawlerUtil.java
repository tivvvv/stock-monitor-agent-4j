package com.tiv.stock.monitor.web.utils;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 股票标签爬虫工具类
 */
public class StockTagCrawlerUtil {

    private static final String URL = "https://www.stocktitan.net/news/live.html";

    public static Map<String, List<String>> getTags(List<String> titles) throws Exception {
        Document doc = Jsoup.connect(URL)
                .userAgent("Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/120.0.0.0 Safari/537.36")
                .header("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/avif,image/webp,image/apng,*/*;q=0.8")
                .header("Accept-Language", "en-US,en;q=0.9")
                .header("Accept-Encoding", "gzip, deflate, br")
                .header("Cache-Control", "no-cache")
                .header("Pragma", "no-cache")
                .header("Sec-Ch-Ua", "\"Not_A Brand\";v=\"8\", \"Chromium\";v=\"120\", \"Google Chrome\";v=\"120\"")
                .header("Sec-Ch-Ua-Mobile", "?0")
                .header("Sec-Ch-Ua-Platform", "\"macOS\"")
                .header("Sec-Fetch-Dest", "document")
                .header("Sec-Fetch-Mode", "navigate")
                .header("Sec-Fetch-Site", "none")
                .header("Sec-Fetch-User", "?1")
                .header("Upgrade-Insecure-Requests", "1")
                .header("X-Requested-With", "XMLHttpRequest")
                .timeout(20_000)
                .followRedirects(true)
                .ignoreHttpErrors(true)
                .get();
        return extractTagsByTitle(doc, titles);
    }

    /**
     * 根据新闻标题提取标签
     */
    private static Map<String, List<String>> extractTagsByTitle(Document doc, List<String> titles) {
        Map<String, List<String>> title2TagsMap = new HashMap<>();
        for (String title : titles) {
            List<String> tags = new ArrayList<>();
            // 1. 找到所有新闻标题节点
            Elements titleElements = doc.select("a.feed-link");
            for (Element titleEl : titleElements) {
                if (title.equals(titleEl.text())) {
                    // 2. 向上找到整个news-row
                    Element newsRow = titleEl.closest("div.news-row");
                    if (newsRow == null) {
                        continue;
                    }

                    // 3. 在该news-row中找tags
                    Elements tagElements = newsRow.select("div[name=tags] span.badge");
                    for (Element tag : tagElements) {
                        tags.add(tag.text().trim());
                    }
                    break;
                }
            }
            title2TagsMap.put(title, tags);
        }

        return title2TagsMap;
    }

}
