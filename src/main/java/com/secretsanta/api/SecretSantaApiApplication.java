package com.secretsanta.api;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan(basePackages = "com.secretsanta.api")
public class SecretSantaApiApplication {

    public static void main(String[] args) {
        SpringApplication.run(SecretSantaApiApplication.class, args);
    }

}
