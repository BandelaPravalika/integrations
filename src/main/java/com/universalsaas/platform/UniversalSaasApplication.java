package com.universalsaas.platform;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class UniversalSaasApplication {

    public static void main(String[] args) {
        SpringApplication.run(UniversalSaasApplication.class, args);
    }
}
