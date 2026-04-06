package com.example.unittestexample.exceptions;

public class TurmaJaExistenteException extends RuntimeException {

  public TurmaJaExistenteException(String nome) {
    super(String.format("Já possui uma turma com o nome: %s !", nome));
  }
}
