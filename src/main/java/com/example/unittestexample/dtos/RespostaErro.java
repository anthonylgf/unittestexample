package com.example.unittestexample.dtos;

import java.util.List;
import lombok.Data;

@Data
public class RespostaErro {

  private List<MensagemErroIndividual> erros;
}
