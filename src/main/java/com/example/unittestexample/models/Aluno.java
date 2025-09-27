package com.example.unittestexample.models;

import com.example.unittestexample.enums.Genero;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.LocalDate;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "ALUNO")
public class Aluno {

  @Id @GeneratedValue private Long id;

  @Column(name = "nome_completo", length = 400, unique = true, nullable = false)
  private String nomeCompleto;

  @Enumerated(EnumType.STRING)
  @Column(name = "genero", nullable = false)
  private Genero genero;

  @Column(name = "data_nascimento", nullable = false)
  private LocalDate dataNascimento;
}
