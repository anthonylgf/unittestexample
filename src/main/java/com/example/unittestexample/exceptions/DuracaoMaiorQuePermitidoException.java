package com.example.unittestexample.exceptions;

public class DuracaoMaiorQuePermitidoException extends RuntimeException {
  public DuracaoMaiorQuePermitidoException(int duracao) {
    super(String.format("Duração invalida: %d horas, O limite permitido é de 5 horas", duracao));
  }
}
