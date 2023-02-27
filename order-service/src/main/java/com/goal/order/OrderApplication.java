package com.goal.order;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

/**
 * @author Mr.Peng
 */
@EnableFeignClients
@MapperScan("com.goal.order.mapper")
@SpringBootApplication(scanBasePackages = {"com.goal.order", "com.goal.common.*"})
public class OrderApplication {
    public static void main(String[] args) {
        SpringApplication.run(OrderApplication.class, args);
    }
}
