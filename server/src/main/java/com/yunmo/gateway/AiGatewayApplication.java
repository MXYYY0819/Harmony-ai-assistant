package com.yunmo.gateway;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.mybatis.spring.annotation.MapperScan;
@SpringBootApplication @MapperScan("com.yunmo.gateway.mapper") public class AiGatewayApplication  {
    public static void main(String[] args) {
        SpringApplication.run(AiGatewayApplication.class,args);
    }
}
