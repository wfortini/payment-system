package com.wfortini.pspemulator;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@EnableAsync
@SpringBootApplication
public class PspEmulatorApplication {

    public static void main(String[] args) {
        SpringApplication.run(PspEmulatorApplication.class, args);
    }
}
