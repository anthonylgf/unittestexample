package com.example.unittestexample.dtos;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import java.time.LocalTime;
import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TurmaDto {

  private Long id;

  @Pattern(
      regexp = "^[A-Z0-9-]+$",
      message = "O nome deve conter apenas letras maiúsculas, números e o caractere '-'")
  @NotBlank(message = "O nome deve ser preenchido")
  private String nome;

  @NotNull(message = "Horário de início obrigatório")
  @JsonFormat(pattern = "HH:mm")
  private LocalTime horarioInicio;

  @NotNull(message = "Horário de fim obrigatório")
  @JsonFormat(pattern = "HH:mm")
  private LocalTime horarioFim;

  private Integer limiteTurma;
}
