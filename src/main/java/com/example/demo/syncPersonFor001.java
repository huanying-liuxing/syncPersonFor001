package com.example.demo;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.transaction.annotation.EnableTransactionManagement;

//@SpringBootApplication
@MapperScan(basePackages = "com.example.demo.mapper")
@SpringBootApplication(exclude = {org.springframework.boot.autoconfigure.validation.ValidationAutoConfiguration.class})
@EnableTransactionManagement
public class syncPersonFor001 {

    public static void main(String[] args) {
        SpringApplication.run(syncPersonFor001.class, args);
    }

}
