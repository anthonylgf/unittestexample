package com.example.unittestexample.exceptions;

public class TurmaLotadaException extends RuntimeException {
  public TurmaLotadaException(String nome, Integer limite) {
    super(String.format("A turma '%s' já atingiu o limite máximo de %d alunos.", nome, limite));
  }
}
