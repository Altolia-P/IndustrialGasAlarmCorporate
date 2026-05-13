package com.niit.industrialgasalarmcorporate;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("com.niit.industrialgasalarmcorporate.infrastructure.repository.mapper")
public class IndustrialGasAlarmCorporateApplication {

    public static void main(String[] args) {
        SpringApplication.run(IndustrialGasAlarmCorporateApplication.class, args);
    }

}
