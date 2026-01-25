package com.tiv.stock.monitor.web.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.tiv.stock.monitor.web.entity.StockRssInfo;
import com.tiv.stock.monitor.web.mapper.StockRssInfoMapper;
import com.tiv.stock.monitor.web.service.StockService;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class StockServiceImpl implements StockService {

    @Resource
    private StockRssInfoMapper stockRssInfoMapper;

    @Override
    public Boolean isStockNewsExist(String stockCode, String link) {
        QueryWrapper<StockRssInfo> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("stock_code", stockCode);
        queryWrapper.eq("link", link);

        return stockRssInfoMapper.selectCount(queryWrapper) > 0;
    }

    @Override
    public void saveStockNews(List<StockRssInfo> stockRssInfos) {
        stockRssInfoMapper.insert(stockRssInfos);
    }

}
