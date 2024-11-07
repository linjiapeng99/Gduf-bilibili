package com.gduf.bilibili.service.feign;

import javafx.beans.binding.ObjectExpression;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Map;

@FeignClient("gduf-bilibili-ms-api")//要去调用的服务名称
public interface MsDeclareService {
    @GetMapping("/demos")
    public Long msget(@RequestParam Long id);
    @PostMapping("demos")
    public Map<String, Object>mspost(@RequestBody Map<String,Object>params);
}
