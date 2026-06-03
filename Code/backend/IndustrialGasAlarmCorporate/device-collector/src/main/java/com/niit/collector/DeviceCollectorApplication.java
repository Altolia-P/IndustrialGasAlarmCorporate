package com.niit.collector;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableDiscoveryClient
@EnableScheduling
@MapperScan("com.niit.collector.infrastructure.mapper")
public class DeviceCollectorApplication {

    public static void main(String[] args) {
        SpringApplication.run(DeviceCollectorApplication.class, args);
    }
}
