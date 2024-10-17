package com.gduf;

import com.gduf.bilibili.service.websocket.WebSocketService;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
@SpringBootApplication
public class GdufBilibiliApp {
    public static void main(String[] args) {
        ApplicationContext run = SpringApplication.run(GdufBilibiliApp.class, args);
        WebSocketService.setApplicationContext(run);
    }
}
