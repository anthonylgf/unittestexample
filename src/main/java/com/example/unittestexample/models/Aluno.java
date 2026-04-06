package com.example.unittestexample.models;

import com.example.unittestexample.enums.Genero;
import jakarta.persistence.*;
import com.fasterxml.jackson.annotation.JsonBackReference;
import java.time.LocalDate;
import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
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

  @ManyToOne
  @JoinColumn(name = "turma_id")
  @JsonBackReference
  private Turma turma;
}
