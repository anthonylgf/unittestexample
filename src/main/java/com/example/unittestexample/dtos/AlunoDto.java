package com.example.unittestexample.dtos;

import com.example.unittestexample.annotations.Create;
import com.example.unittestexample.annotations.Update;
import com.example.unittestexample.enums.Genero;
import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.*;
import java.time.LocalDate;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AlunoDto {

  @Null(message = "O campo de id deve ser nulo nas requisicoes")
  private Long id;

  @Pattern(
      regexp = "[A-Z]+",
      message = "O nome deve conter apenas caracteres sem acentos e espaços")
  @NotBlank(message = "O nome deve ser preenchido")
  private String nome;

  @Pattern(
      regexp = "[A-Z]+",
      message = "O sobrenome deve conter apenas caracteres sem acentos e espaços")
  @NotBlank(message = "O sobrenome deve ser preenchido")
  private String sobrenome;

  @NotNull(groups = Create.class, message = "O genero deve ser preenchido")
  @Null(groups = Update.class, message = "O genero nao deve ser modificado")
  private Genero genero;

  @JsonFormat(pattern = "dd-MM-yyyy")
  @PastOrPresent(message = "A data de nascimento nao pode ser futura")
  @NotNull(groups = Create.class, message = "A data de nascimento deve ser preenchida")
  @Null(groups = Update.class, message = "A data de nascimento nao deve ser modificada")
  private LocalDate dataNascimento;
}
