package com.tiv.stock.monitor.mcp.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.tiv.stock.monitor.mcp.entity.StockAndCount;
import com.tiv.stock.monitor.mcp.entity.StockRssInfo;
import org.apache.ibatis.annotations.Mapper;

import java.time.LocalDateTime;
import java.util.List;

@Mapper
public interface StockRssInfoMapper extends BaseMapper<StockRssInfo> {

    List<StockAndCount> getStockAboveTargetCount(Integer targetCount, LocalDateTime startDate, LocalDateTime endDate);

}
