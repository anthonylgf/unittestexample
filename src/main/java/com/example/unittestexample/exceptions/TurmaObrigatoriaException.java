package com.example.unittestexample.exceptions;

public class TurmaObrigatoriaException extends RuntimeException {
  public TurmaObrigatoriaException() {
    super("A vinculação de uma turma é obrigatória para cadastrar ou alterar um aluno.");
  }
}
