package com.tiv.stock.monitor.web;

import io.github.cdimascio.dotenv.Dotenv;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

@SpringBootApplication
@EnableAspectJAutoProxy
public class WebApplication {

    public static void main(String[] args) {
        // 加载.env文件
        Dotenv dotenv = Dotenv.configure().ignoreIfMissing().load();
        // 把.env文件中的变量参数设置到当前项目中
        dotenv.entries().forEach(entry -> System.setProperty(entry.getKey(), entry.getValue()));

        SpringApplication.run(WebApplication.class, args);
    }

}
