package com.tiv.stock.monitor.mcp.tools;

import com.tiv.stock.monitor.mcp.entity.StockRssInfo;
import com.tiv.stock.monitor.mcp.service.StockService;
import jakarta.annotation.Resource;
import jakarta.validation.constraints.NotBlank;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

/**
 * MCP 股票工具类
 */
@Slf4j
@Component
public class StockTool {

    @Resource
    private StockService stockService;

    @Tool(description = "根据股票代码查询指定时间内的股票异动信息")
    public List<StockRssInfo> getStockNewsByCode(@NotBlank(message = "股票代码不能为空") String stockCode,
                                                 LocalDateTime startDate,
                                                 LocalDateTime endDate) {
        return stockService.getStockNewsByCode(stockCode, startDate, endDate);
    }

}
