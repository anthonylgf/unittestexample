package com.example.unittestexample.publisher;

import com.example.unittestexample.models.Aluno;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@Slf4j
@Component("alunoPublisher")
@Profile("no-kafka-test")
public class AlunoNoPublisher {
    public void sendAluno(Aluno aluno) {
        log.info("ANULADO: O envio de Kafka para o Aluno ID {} foi interceptado.", aluno.getId());
    }
}
