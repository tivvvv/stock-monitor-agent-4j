package com.tiv.stock.monitor.web.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.time.LocalDateTime;

/**
 * 股票异动消息
 *
 * @TableName stock_rss_info
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
public class StockMsg {

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
     * 新闻发布时间(北京时间)
     */
    private LocalDateTime publishTimeCn;

    /**
     * 24小时内新闻数量
     */
    private Long countsIn24Hour;

    /**
     * 3天内新闻数量
     */
    private Long countsIn3Day;

    /**
     * 1周内新闻数量
     */
    private Long countsIn1Week;

}