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
import com.tiv.stock.monitor.web.mapper.StockRssInfoMapper;
import com.tiv.stock.monitor.web.service.RssService;
import com.tiv.stock.monitor.web.utils.GMTDateConvertUtil;
import com.tiv.stock.monitor.web.utils.StockTagCrawlerUtil;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Slf4j
@Service
public class RssServiceImpl implements RssService {

    @Resource
    private StockRssInfoMapper stockRssInfoMapper;

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
        List<SyndEntry> rssList = this.fetchRssFeed(STOCK_RSS_URL);
        if (CollUtil.isEmpty(rssList)) {
            return;
        }
        List<StockRssInfo> stockRssInfos = new ArrayList<>();
        for (SyndEntry rss : rssList) {
            // EquipmentShare Prices Initial Public Offering | EQPT Stock News
            String rssTitle = rss.getTitle();
            String stockTitle = getStockTitle(rssTitle);
            StockRssInfo stockRssInfo = StockRssInfo.builder()
                    .stockCode(getStockCode(rssTitle))
                    .title(stockTitle)
                    .link(rss.getLink())
                    .publishTimeGmt(GMTDateConvertUtil.convertGmt(rss.getPublishedDate()))
                    .publishTimeCn(GMTDateConvertUtil.convertGmtToBeijing(rss.getPublishedDate()))
                    .build();
            try {
                List<String> tags = StockTagCrawlerUtil.getTags(stockTitle);
                stockRssInfo.setTags(getTagsCn(tags));
            } catch (Exception e) {
                log.error("displayRss--获取股票标签失败,rssTitle:{}", rssTitle, e);
                stockRssInfo.setTags(JSONUtil.toJsonStr(Collections.emptyList()));
            }
            stockRssInfos.add(stockRssInfo);
        }
        stockRssInfoMapper.insert(stockRssInfos);
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
