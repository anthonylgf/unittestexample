package com.example.unittestexample.fixtures;

import com.example.unittestexample.models.Turma;
import java.time.LocalTime;
import java.util.UUID;
import java.util.function.Consumer;

public class TurmaFixture {

  public static Turma build() {
    return build(turma -> {});
  }

  public static Turma build(Consumer<Turma> consumer) {
    var turma = new Turma();
    turma.setId(1L);
    turma.setNome(gerarNomeAleatorio());
    turma.setHorarioInicio(LocalTime.of(8, 0));
    turma.setHorarioFim(LocalTime.of(12, 0));
    turma.setDuracao(240);
    turma.setLimiteTurma(30);

    consumer.accept(turma);
    return turma;
  }

  private static String gerarNomeAleatorio() {
    return UUID.randomUUID().toString().replace("-", "").replaceAll("[0-9]", "a").toUpperCase();
  }
}
