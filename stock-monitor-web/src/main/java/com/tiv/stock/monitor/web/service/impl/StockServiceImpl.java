package com.tiv.stock.monitor.web.service.impl;

import cn.hutool.core.util.BooleanUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.tiv.stock.monitor.web.common.Constants;
import com.tiv.stock.monitor.web.entity.StockMsg;
import com.tiv.stock.monitor.web.entity.StockRssInfo;
import com.tiv.stock.monitor.web.mapper.StockRssInfoMapper;
import com.tiv.stock.monitor.web.service.StockService;
import jakarta.annotation.Resource;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

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

    @Override
    public String formatStockMsgs(List<StockMsg> stockMsgs) {
        return stockMsgs.stream()
                .map(this::formatStockMsg)
                .collect(Collectors.joining("\n--------------------\n"));
    }

    @Override
    public String formatStockMsg(StockMsg stockMsg) {
        return String.format(
                "📌 代码: %s\n📅 时间: %s\n📰 标题: %s\n📰 中标: %s\n🏷️ 标签: %s\n🔗 链接: %s\n📊 统计: 24小时内异动=%d次; 3天内异动=%d次; 1周内异动=%d次",
                stockMsg.getStockCode(),
                stockMsg.getPublishTimeCn(),
                stockMsg.getTitle(),
                stockMsg.getTitleCn(),
                stockMsg.getTags(),
                stockMsg.getLink(),
                stockMsg.getCountsIn24Hour(),
                stockMsg.getCountsIn3Day(),
                stockMsg.getCountsIn1Week()
        );
    }

}
