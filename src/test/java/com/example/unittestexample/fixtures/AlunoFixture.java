package com.example.unittestexample.fixtures;

import com.example.unittestexample.dtos.AlunoDto;
import com.example.unittestexample.enums.Genero;
import com.example.unittestexample.models.Aluno;
import java.time.LocalDate;
import java.util.UUID;
import java.util.function.Consumer;

public class AlunoFixture {

  public static Aluno build(Consumer<Aluno> consumer) {
    var aluno = new Aluno();
    aluno.setId(1L);
    aluno.setNomeCompleto(String.format("%s %s", gerarNomeAleatorio(), gerarNomeAleatorio()));
    aluno.setGenero(Genero.MASCULINO);
    aluno.setDataNascimento(LocalDate.of(2000, 1, 15));

    consumer.accept(aluno);
    return aluno;
  }

  public static AlunoDto buildDto(Consumer<AlunoDto> consumer) {
    var aluno = new AlunoDto();
    aluno.setId(1L);
    aluno.setNome(gerarNomeAleatorio());
    aluno.setSobrenome(gerarNomeAleatorio());
    aluno.setGenero(Genero.MASCULINO);
    aluno.setDataNascimento(LocalDate.of(2000, 1, 15));

    consumer.accept(aluno);
    return aluno;
  }

  private static String gerarNomeAleatorio() {
    return UUID.randomUUID().toString().replace("-", "").replaceAll("[0-9]", "a").toUpperCase();
  }
}
