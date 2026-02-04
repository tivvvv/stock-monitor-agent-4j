package com.tiv.stock.monitor.mcp.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.tiv.stock.monitor.mcp.entity.StockRssInfo;
import com.tiv.stock.monitor.mcp.mapper.StockRssInfoMapper;
import com.tiv.stock.monitor.mcp.service.StockService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
public class StockServiceImpl implements StockService {

    @Resource
    private StockRssInfoMapper stockRssInfoMapper;

    @Override
    public List<StockRssInfo> getStockNewsByCode(String stockCode, LocalDateTime startDate, LocalDateTime endDate) {
        QueryWrapper<StockRssInfo> queryWrapper = new QueryWrapper<>();

        queryWrapper.eq("stock_code", stockCode);
        queryWrapper.ge(startDate != null, "publish_time_cn", startDate);
        queryWrapper.le(endDate != null, "publish_time_cn", endDate);
        queryWrapper.orderByDesc("publish_time_cn");

        return stockRssInfoMapper.selectList(queryWrapper);
    }

}
