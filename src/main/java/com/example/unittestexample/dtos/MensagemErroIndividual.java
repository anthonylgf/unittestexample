package com.example.unittestexample.dtos;

import lombok.*;

@Setter
@Getter
@Builder
public class MensagemErroIndividual {

  private String codigoErro;

  private String mensagem;
}
