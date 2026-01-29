package com.tiv.stock.monitor.web.service;

import com.tiv.stock.monitor.web.entity.StockMsg;
import com.tiv.stock.monitor.web.entity.StockRssInfo;

import java.time.LocalDateTime;
import java.util.List;

public interface StockService {

    Boolean isStockNewsExist(String stockCode, String link);

    void saveStockNews(List<StockRssInfo> stockRssInfos);

    Long getStockNewsCount(String stockCode, LocalDateTime startTimeGmt, LocalDateTime endTimeGmt);

    String formatStockMsgs(List<StockMsg> stockMsgs);

    String formatStockMsg(StockMsg stockMsg);

}
