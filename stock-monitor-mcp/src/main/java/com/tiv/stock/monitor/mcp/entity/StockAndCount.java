package com.tiv.stock.monitor.mcp.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class StockAndCount {

    /**
     * 股票代码
     */
    private String stockCode;

    /**
     * 异动次数
     */
    private String occurCount;

}
