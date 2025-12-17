package com.example.unittestexample.publisher;

import com.example.unittestexample.models.Aluno;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
@Profile("!no-kafka-test")
public class AlunoPublisher {

  private final KafkaTemplate<String, Object> kafkaTemplate;

  @Value("${unittestexample.config.kafka.topics.unittestexample-aluno}")
  private String topicAluno;

  public void sendAluno(Aluno aluno) {
    log.info("Enviando Aluno para o Kafka. ID: {}, Tópico: {}", aluno.getId(), topicAluno);
    try {
      kafkaTemplate.send(topicAluno, aluno.getId().toString(), aluno);
      log.info("Aluno enviado com sucesso");
    } catch (Exception e){
      log.error("Erro ao enviar o aluno", e.getMessage());
    }
  }
}
