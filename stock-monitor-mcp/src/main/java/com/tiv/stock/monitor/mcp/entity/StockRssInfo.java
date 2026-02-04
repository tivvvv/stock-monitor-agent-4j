package com.tiv.stock.monitor.mcp.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
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
@TableName("stock_rss_info")
public class StockRssInfo {

    /**
     * 主键id
     */
    private String id;

    /**
     * 股票代码
     */
    @TableField("stock_code")
    private String stockCode;

    /**
     * 标题
     */
    private String title;

    /**
     * 中文标题
     */
    @TableField("title_cn")
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
    @TableField("publish_time_gmt")
    private LocalDateTime publishTimeGmt;

    /**
     * 新闻发布时间(北京时间)
     */
    @TableField("publish_time_cn")
    private LocalDateTime publishTimeCn;

    /**
     * 创建时间
     */
    @TableField("create_time")
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    @TableField("update_time")
    private LocalDateTime updateTime;

}