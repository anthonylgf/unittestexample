package com.example.unittestexample.exceptions;

public class AlunoExisteMesmoNomeException extends RuntimeException {

  public AlunoExisteMesmoNomeException(String nomeCompleto) {
    super(String.format("Ja existe aluno com o mesmo nome: %s.", nomeCompleto));
  }
}
