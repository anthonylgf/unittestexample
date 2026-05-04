package com.example.unittestexample.exceptions;

public class TurmaPossuiAlunosException extends RuntimeException {
  public TurmaPossuiAlunosException(String nomeTurma, int quantidadeAlunos) {
    super(
        String.format(
            "Não é permetido pois a turma '%s' possui %d aluno(s) matriculado(s).",
            nomeTurma, quantidadeAlunos));
  }
}
