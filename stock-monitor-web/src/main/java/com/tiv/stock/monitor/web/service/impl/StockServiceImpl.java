package com.tiv.stock.monitor.web.service.impl;

import cn.hutool.core.util.BooleanUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.tiv.stock.monitor.web.common.Constants;
import com.tiv.stock.monitor.web.entity.StockRssInfo;
import com.tiv.stock.monitor.web.mapper.StockRssInfoMapper;
import com.tiv.stock.monitor.web.service.StockService;
import jakarta.annotation.Resource;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class StockServiceImpl implements StockService {

    @Resource
    private StockRssInfoMapper stockRssInfoMapper;

    @Resource
    private RedisTemplate<String, Object> redisTemplate;

    @Override
    public Boolean isStockNewsExist(String stockCode, String link) {
        String key = Constants.REDIS_STOCK_NEWS + stockCode;
        return BooleanUtil.isTrue(redisTemplate.opsForHash().hasKey(key, link));
    }

    @Override
    public void saveStockNews(List<StockRssInfo> stockRssInfos) {
        // 1. 批量保存数据库
        stockRssInfoMapper.insert(stockRssInfos);
        // 2. 更新Redis缓存
        for (StockRssInfo stockRssInfo : stockRssInfos) {
            String key = Constants.REDIS_STOCK_NEWS + stockRssInfo.getStockCode();
            String link = stockRssInfo.getLink();
            redisTemplate.opsForHash().put(key, link, System.currentTimeMillis());
            // 设置7天过期时间
            redisTemplate.expire(key, Duration.ofDays(7));
        }
    }

    @Override
    public Long getStockNewsCount(String stockCode, LocalDateTime startTimeGmt, LocalDateTime endTimeGmt) {
        QueryWrapper<StockRssInfo> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("stock_code", stockCode)
                .ge("publish_time_gmt", startTimeGmt)
                .le("publish_time_gmt", endTimeGmt);

        return stockRssInfoMapper.selectCount(queryWrapper);
    }

}
