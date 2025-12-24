package com.example.unittestexample.cucumber.configuration;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.annotation.KafkaListener;

@TestConfiguration
@EnableKafka
public class KafkaListenerConfig {

  public BlockingQueue<String> mensagensKafka() {
    return mensagens;
  }

  public static final BlockingQueue<String> mensagens = new LinkedBlockingQueue<>();

  @KafkaListener(topics = "unittestexample.aluno", groupId = "unittestexample-aluno")
  public void ouvir(String mensagem) {
    mensagens.add(mensagem);
  }
}
