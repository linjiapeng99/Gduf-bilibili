package com.gduf;

import com.gduf.bilibili.service.websocket.WebSocketService;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@SpringBootApplication
@EnableAsync
@EnableTransactionManagement
@EnableScheduling
public class GdufBilibiliApp {
    public static void main(String[] args) {
        ApplicationContext run = SpringApplication.run(GdufBilibiliApp.class, args);
        WebSocketService.setApplicationContext(run);
    }
}
