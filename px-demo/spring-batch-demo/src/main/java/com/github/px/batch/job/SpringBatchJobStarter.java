package com.github.px.batch.job;

import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@EnableBatchProcessing
@EnableJpaRepositories
@EnableJpaAuditing
public class SpringBatchJobStarter {
    public static void main(String[] args) {
        SpringApplication.run(SpringBatchJobStarter.class);
    }

}
