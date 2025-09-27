package com.example.unittestexample.exceptions;

public class AlunoNaoEncontradoException extends RuntimeException {
  public AlunoNaoEncontradoException(Long id) {
    super(String.format("Aluno com id %d nao encontrado.", id));
  }
}
