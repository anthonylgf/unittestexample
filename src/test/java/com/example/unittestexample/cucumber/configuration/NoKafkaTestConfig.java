package com.example.unittestexample.cucumber.configuration;

import com.example.unittestexample.configs.KafkaConfig;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.kafka.KafkaAutoConfiguration;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

@Configuration
@Profile("no-kafka-test")
@EnableAutoConfiguration(
        exclude = {KafkaAutoConfiguration.class}
)
public class NoKafkaTestConfig {
}
