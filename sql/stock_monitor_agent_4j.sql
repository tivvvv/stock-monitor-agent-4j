DROP TABLE IF EXISTS `stock_rss_info`;
CREATE TABLE `stock_rss_info`
(
    `id`               VARCHAR(64)  NOT NULL COMMENT '主键id',
    `stock_code`       VARCHAR(64)  NOT NULL COMMENT '股票代码',
    `title`            VARCHAR(512) NOT NULL COMMENT '标题',
    `title_cn`         VARCHAR(512)          DEFAULT NULL COMMENT '中文标题',
    `link`             VARCHAR(512) NOT NULL COMMENT '链接',
    `tags`             VARCHAR(255)          DEFAULT NULL COMMENT '标签',
    `publish_time_gmt` DATETIME     NOT NULL COMMENT '新闻发布时间(GMT)',
    `publish_time_cn`  DATETIME     NOT NULL COMMENT '新闻发布时间(北京时间)',
    `create_time`      timestamp    NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time`      timestamp    NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`)
) COMMENT '股票RSS信息表';