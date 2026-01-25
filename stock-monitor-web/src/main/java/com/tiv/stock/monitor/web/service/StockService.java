package com.tiv.stock.monitor.web.service;

import com.tiv.stock.monitor.web.entity.StockRssInfo;

import java.util.List;

public interface StockService {

    Boolean isStockNewsExist(String stockCode, String link);

    void saveStockNews(List<StockRssInfo> stockRssInfos);

}
