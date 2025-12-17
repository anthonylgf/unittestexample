package com.example.unittestexample.subscriber;

import com.example.unittestexample.mappers.AlunoMapper;
import com.example.unittestexample.models.Aluno;
import com.example.unittestexample.services.AlunoService;
import com.example.unittestexample.subscriber.representation.AlunoRepresentation;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class AlunoSubscriber {

  private final ObjectMapper mapper;
  private final AlunoService service;
  private final AlunoMapper alunoMapper;

  @KafkaListener(
      groupId = "${spring.kafka.consumer.group-id}",
      topics = "${unittestexample.config.kafka.topics.unittestexample-aluno}")
  public void listen(String json) {
    try {
      var representatio = mapper.readValue(json, AlunoRepresentation.class);
      Aluno aluno = alunoMapper.toAluno(representatio);

    } catch (Exception e) {
      log.error("Erro: {}", e.getMessage());
    }
    return;
  }
}
