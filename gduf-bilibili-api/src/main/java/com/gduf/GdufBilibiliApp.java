package com.gduf;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;

@SpringBootApplication
public class GdufBilibiliApp {
    public static void main(String[] args) {
        ApplicationContext run = SpringApplication.run(GdufBilibiliApp.class, args);
    }
}
