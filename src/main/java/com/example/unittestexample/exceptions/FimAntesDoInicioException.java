package com.example.unittestexample.exceptions;

import java.time.LocalTime;

public class FimAntesDoInicioException extends RuntimeException {
  public FimAntesDoInicioException(LocalTime fim, LocalTime inicio) {
    super(
        String.format(
            "Horário inválido: o fim (%s) não pode ser anterior ao início (%s).", fim, inicio));
  }
}
