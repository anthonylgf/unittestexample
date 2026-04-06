package com.example.unittestexample.exceptions;

public class TurmaPossuiAlunosException extends RuntimeException {
  public TurmaPossuiAlunosException(String nomeTurma, int quantidadeAlunos) {
    super(
        String.format(
            "Não é permitido excluir a turma '%s' pois ela possui %d aluno(s) matriculado(s).",
            nomeTurma, quantidadeAlunos));
  }
}
