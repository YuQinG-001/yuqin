package com.yuqin.meinian.api.config.xss;

import com.yuqin.meinian.api.async.InitializeWorkAsync;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
public class AsyncConfig {
    private final InitializeWorkAsync initializeWorkAsync;

    @PostConstruct
    public void init() {
        initializeWorkAsync.initializeWork();
    }
}
