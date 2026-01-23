package com.tiv.stock.monitor.web.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 股票RSS信息
 *
 * @TableName stock_rss_info
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StockRssInfo {

    /**
     * 主键id
     */
    private String id;

    /**
     * 股票代码
     */
    private String stockCode;

    /**
     * 标题
     */
    private String title;

    /**
     * 中文标题
     */
    private String titleCn;

    /**
     * 链接
     */
    private String link;

    /**
     * 标签
     */
    private String tags;

    /**
     * 新闻发布时间(GMT)
     */
    private LocalDateTime publishTimeGmt;

    /**
     * 新闻发布时间(北京时间)
     */
    private LocalDateTime publishTimeCn;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    private LocalDateTime updateTime;

}