package com.example.unittestexample.exceptions;

public class IdadeInvalidaException extends RuntimeException {

  public IdadeInvalidaException(int idadeAluno, int minimoIdade, int maximoIdade) {
    super(
        String.format(
            "Aluno possui idade invalida: %d. As idades permitidas são entre %d e %d.",
            idadeAluno, minimoIdade, maximoIdade));
  }
}
