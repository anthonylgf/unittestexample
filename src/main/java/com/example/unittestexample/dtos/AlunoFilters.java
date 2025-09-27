package com.example.unittestexample.dtos;

import com.example.unittestexample.enums.Genero;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.Data;

@Data
public class AlunoFilters {
  private String nomeCompleto;
  @PositiveOrZero private Integer idadeMinima;
  @PositiveOrZero private Integer idadeMaxima;
  private Genero genero;
}
