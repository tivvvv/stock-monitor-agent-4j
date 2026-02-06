package com.tiv.stock.monitor.mcp.service;

import com.tiv.stock.monitor.mcp.entity.StockAndCount;
import com.tiv.stock.monitor.mcp.entity.StockRssInfo;

import java.time.LocalDateTime;
import java.util.List;

public interface StockService {

    List<StockRssInfo> getStockNewsByCode(String stockCode, LocalDateTime startDate, LocalDateTime endDate);

    List<StockAndCount> getStockAboveTargetCount(Integer targetCount, LocalDateTime startDate, LocalDateTime endDate);

    List<StockRssInfo> getStockNewsByKeywords(List<String> keywords);

}
