package com.example.unittestexample.exceptions;

public class TurmaNaoEncontradaException extends RuntimeException {
  public TurmaNaoEncontradaException(Long id) {
    super(String.format("Turma com id: %d não encontrado", id));
  }
}
