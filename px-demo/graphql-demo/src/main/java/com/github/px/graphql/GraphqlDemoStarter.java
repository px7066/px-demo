package com.github.px.graphql;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing
public class GraphqlDemoStarter {
    public static void main(String[] args) {
        SpringApplication.run(GraphqlDemoStarter.class, args);
    }
}
