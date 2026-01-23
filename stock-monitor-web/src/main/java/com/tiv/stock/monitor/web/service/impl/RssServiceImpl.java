package com.tiv.stock.monitor.web.service.impl;

import com.rometools.rome.feed.synd.SyndEntry;
import com.rometools.rome.feed.synd.SyndFeed;
import com.rometools.rome.io.SyndFeedInput;
import com.rometools.rome.io.XmlReader;
import com.tiv.stock.monitor.web.service.RssService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.net.URL;
import java.util.Collections;
import java.util.List;

@Slf4j
@Service
public class RssServiceImpl implements RssService {

    private static final String STOCK_RSS_URL = "https://www.stocktitan.net/rss";

    @Override
    public List<SyndEntry> fetchRssFeed(String rssUrl) {
        try {
            URL url = new URL(rssUrl);
            SyndFeedInput input = new SyndFeedInput();
            SyndFeed feed = input.build(new XmlReader(url));
            return feed.getEntries();
        } catch (Exception e) {
            log.error("fetchRssFeed--失败,rssUrl:{}", rssUrl, e);
        }
        return Collections.emptyList();
    }

    @Override
    public void displayRss() {
        List<SyndEntry> rssList = this.fetchRssFeed(STOCK_RSS_URL);
        System.out.println(rssList);
    }

}
