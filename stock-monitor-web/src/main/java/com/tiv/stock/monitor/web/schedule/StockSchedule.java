package com.tiv.stock.monitor.web.schedule;

import com.tiv.stock.monitor.web.service.RssService;
import jakarta.annotation.Resource;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@EnableScheduling
public class StockSchedule {

    @Resource
    private RssService rssService;

    @Scheduled(cron = "*/10 * * * * ?")
    public void fetchStockInfo() {
        rssService.displayRss();
    }

}
