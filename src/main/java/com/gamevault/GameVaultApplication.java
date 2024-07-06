package com.gamevault;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class GameVaultApplication {

    public static void main(String[] args) {
        SpringApplication.run(GameVaultApplication.class, args);
    }

}
