package com.gduf.bilibili.domain.annotation;

import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RestController;

import java.lang.annotation.*;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
@Documented
@Component
public @interface ApiLiitedRole {
    String[]limitedRoleCodeList()default {};
}
