package com.balancerx.api;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = "com.balancerx")
public class BalancerXApiApplication {
    public static void main(String[] args) {
        SpringApplication.run(BalancerXApiApplication.class, args);
    }
}
