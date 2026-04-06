package com.example.unittestexample.dtos;

import java.time.LocalTime;
import java.util.List;

public record TurmaDetalhesDto(
    Long id,
    String nome,
    LocalTime horarioInicio,
    LocalTime horarioFim,
    Integer duracao,
    Integer limiteTurma,
    List<AlunoDto> alunos) {}
