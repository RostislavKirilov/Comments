package com.tinqinacademy.comments.rest;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;


@SpringBootApplication
@EnableJpaRepositories(basePackages = "com.tinqinacademy.comments.persistence.repository")
@EntityScan(basePackages = "com.tinqinacademy.comments.persistence.entities")
@ComponentScan(basePackages = "com.tinqinacademy.comments")
public class CommentsApplication {

    public static void main ( String[] args ) {
        SpringApplication.run(CommentsApplication.class, args);
    }

}
