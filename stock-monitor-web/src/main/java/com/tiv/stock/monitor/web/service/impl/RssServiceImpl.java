package com.tiv.stock.monitor.web.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.json.JSONUtil;
import com.rometools.rome.feed.synd.SyndEntry;
import com.rometools.rome.feed.synd.SyndFeed;
import com.rometools.rome.io.SyndFeedInput;
import com.rometools.rome.io.XmlReader;
import com.tiv.stock.monitor.web.common.Constants;
import com.tiv.stock.monitor.web.common.StockTagEnum;
import com.tiv.stock.monitor.web.entity.StockRssInfo;
import com.tiv.stock.monitor.web.service.RssService;
import com.tiv.stock.monitor.web.service.StockService;
import com.tiv.stock.monitor.web.utils.GMTDateConvertUtil;
import com.tiv.stock.monitor.web.utils.StockTagCrawlerUtil;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
public class RssServiceImpl implements RssService {

    @Resource
    private StockService stockService;

    private static final String STOCK_RSS_URL = "https://www.stocktitan.net/rss";

    @Override
    public List<SyndEntry> fetchRssFeed(String rssUrl) {
        try {
            URL url = new URL(rssUrl);
            SyndFeedInput input = new SyndFeedInput();
            SyndFeed feed = input.build(new XmlReader(url));
            log.info("fetchRssFeed--成功,抓取RSS[{}]条", feed.getEntries().size());
            return feed.getEntries();
        } catch (Exception e) {
            log.error("fetchRssFeed--失败,rssUrl:{}", rssUrl, e);
        }
        return Collections.emptyList();
    }

    @Override
    public void displayRss() {
        // 1. 获取股票新闻
        List<SyndEntry> rssList = this.fetchRssFeed(STOCK_RSS_URL);
        if (CollUtil.isEmpty(rssList)) {
            return;
        }
        List<String> stockTitles = new ArrayList<>();
        List<StockRssInfo> stockRssInfos = new ArrayList<>();

        for (SyndEntry rss : rssList) {
            // EquipmentShare Prices Initial Public Offering | EQPT Stock News
            String rssTitle = rss.getTitle();
            String stockTitle = getStockTitle(rssTitle);
            stockTitles.add(stockTitle);

            String stockCode = getStockCode(rssTitle);
            String link = rss.getLink();

            StockRssInfo stockRssInfo = StockRssInfo.builder()
                    .stockCode(stockCode)
                    .title(stockTitle)
                    .link(link)
                    .publishTimeGmt(GMTDateConvertUtil.convertGmt(rss.getPublishedDate()))
                    .publishTimeCn(GMTDateConvertUtil.convertGmtToBeijing(rss.getPublishedDate()))
                    .tags(JSONUtil.toJsonStr(Collections.emptyList()))
                    .build();
            // 根据股票代码和新闻链接判重
            if (stockService.isStockNewsExist(stockCode, link)) {
                log.info("displayRss--股票新闻已存在,stockCode:{},link:{}", stockCode, link);
                continue;
            }
            stockRssInfos.add(stockRssInfo);
        }
        // 2. 批量获取股票信息标签
        try {
            Map<String, List<String>> title2TagsMap = StockTagCrawlerUtil.getTags(stockTitles);
            for (StockRssInfo stockRssInfo : stockRssInfos) {
                String stockTitle = stockRssInfo.getTitle();
                if (title2TagsMap.containsKey(stockTitle)) {
                    stockRssInfo.setTags(getTagsCn(title2TagsMap.get(stockTitle)));
                }
            }
        } catch (Exception e) {
            log.error("displayRss--批量获取股票信息标签,stockTitles:{}", JSONUtil.toJsonStr(stockTitles), e);
        }
        // 3. 保存股票信息
        stockService.saveStockNews(stockRssInfos);
    }

    private String getStockTitle(String rssTitle) {
        // EquipmentShare Prices Initial Public Offering | EQPT Stock News
        String[] titleArr = rssTitle.split("\\|");
        // EquipmentShare Prices Initial Public Offering
        return titleArr[0].trim();
    }

    private String getStockCode(String rssTitle) {
        // EquipmentShare Prices Initial Public Offering | EQPT Stock News
        String[] titleArr = rssTitle.split("\\|");
        //  EQPT Stock News
        String stockStr = titleArr[titleArr.length - 1];
        int stockNewsIndex = stockStr.indexOf(Constants.STOCK_NEWS_SUFFIX);
        // EQPT
        return stockStr.substring(0, stockNewsIndex).trim();
    }

    private String getTagsCn(List<String> tags) {
        List<String> tagsCn = tags.stream().map(tag -> {
            String desc = StockTagEnum.getDescByKey(tag);
            return desc != null ? desc : tag;
        }).toList();
        return JSONUtil.toJsonStr(tagsCn);
    }

}
