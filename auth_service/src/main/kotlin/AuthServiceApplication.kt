package com.example

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.context.annotation.ComponentScan
import org.springframework.data.jpa.repository.config.EnableJpaRepositories
import org.springframework.kafka.annotation.EnableKafka

@EnableKafka
@SpringBootApplication
@ComponentScan(basePackages = ["com.example", "service", "config", "kafka"])
@EnableJpaRepositories(basePackages = ["com.example.repos"])
class AuthServiceApplication

fun main(args: Array<String>) {
    runApplication<AuthServiceApplication>(*args)
}
