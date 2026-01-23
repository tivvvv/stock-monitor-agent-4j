package com.tiv.stock.monitor.web.service;

import com.rometools.rome.feed.synd.SyndEntry;

import java.util.List;

public interface RssService {

    List<SyndEntry> fetchRssFeed(String rssUrl);

    void displayRss();

}
