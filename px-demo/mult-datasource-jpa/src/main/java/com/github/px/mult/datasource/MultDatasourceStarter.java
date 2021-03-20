package com.github.px.mult.datasource;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication(scanBasePackages = "com.github.px")
@EnableJpaAuditing
public class MultDatasourceStarter {
    public static void main(String[] args) {

        SpringApplication.run(MultDatasourceStarter.class ,args);
    }
}