package com.yuqin.meinian.api;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.ServletComponentScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.scheduling.annotation.EnableAsync;
@ComponentScan("com.yuqin.*")
@EnableAsync
@ServletComponentScan
@MapperScan("com.yuqin.meinian.api.db.mapper")
@SpringBootApplication
public class MeinianApiApplication {

    public static void main(String[] args) {
        SpringApplication.run(MeinianApiApplication.class, args);
    }

}
