package com.example.unittestexample.cucumber.subscriber;

import com.example.unittestexample.mappers.AlunoMapper;
import com.example.unittestexample.models.Aluno;
import com.example.unittestexample.services.AlunoService;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.ArrayList;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@Getter
@RequiredArgsConstructor
public class AlunoSubscriber {

  private final ObjectMapper mapper;
  private final AlunoService service;
  private final AlunoMapper alunoMapper;

  private final BlockingQueue<String> mensagensEnviadas = new LinkedBlockingQueue<>();

  private final java.util.List<String> mensagensRecebidas = new ArrayList<>();

  @KafkaListener(
      groupId = "${spring.kafka.consumer.group-id}",
      topics = "${unittestexample.config.kafka.topics.unittestexample-aluno}",
      containerFactory = "kafkaListenerContainerFactory")
  public void listen(ConsumerRecord<String, Aluno> json) {
    try {
      Aluno aluno = json.value();
      log.info("Recebido aluno: {}", mapper.writeValueAsString(aluno));
      log.info("Chave mensagem: {}", json.key());
      mensagensRecebidas.add(json.key());
    } catch (Exception e) {
      log.error("Erro: {}", e.getMessage());
    }
  }
}
