package com.example.unittestexample.cucumber.configuration;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.test.context.EmbeddedKafka;

@Configuration
@Profile("kafka-integration")
@EmbeddedKafka(
    partitions = 1,
    topics = {"unittestexample.aluno"},
    // Propriedades para garantir que o produtor e consumidor usem o broker embutido
    brokerProperties = {"listeners=PLAINTEXT://localhost:9092", "port=9092"})
@EnableKafka
public class KafkaTestConfig {}
