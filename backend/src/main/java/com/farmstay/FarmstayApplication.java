package com.farmstay;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("com.farmstay.mapper")
public class FarmstayApplication {

    public static void main(String[] args) {
        SpringApplication.run(FarmstayApplication.class, args);
    }
}
