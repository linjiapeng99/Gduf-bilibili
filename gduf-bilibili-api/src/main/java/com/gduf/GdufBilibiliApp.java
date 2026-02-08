package com.gduf;

import com.gduf.bilibili.service.websocket.WebSocketService;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.hystrix.EnableHystrix;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.ApplicationContext;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.transaction.annotation.EnableTransactionManagement;
@SpringBootApplication
@EnableAsync
@EnableTransactionManagement
@EnableScheduling
//@EnableFeignClients(basePackages = "com.gduf.bilibili.service.feign")//feign要扫的包
@EnableHystrix
public class GdufBilibiliApp {
    public static void main(String[] args) {
        ApplicationContext run = SpringApplication.run(GdufBilibiliApp.class, args);
        WebSocketService.setApplicationContext(run);
    }
}
