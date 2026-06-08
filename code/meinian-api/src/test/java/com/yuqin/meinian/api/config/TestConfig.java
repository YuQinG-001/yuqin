package com.yuqin.meinian.api.config;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;

@TestConfiguration
@ComponentScan(excludeFilters = {
        @ComponentScan.Filter(type = FilterType.REGEX, pattern = "com.yuqin.meinian.api.config.WebSocketConfig")
})
public class TestConfig {
}