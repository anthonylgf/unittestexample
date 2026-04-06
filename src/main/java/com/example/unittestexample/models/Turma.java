package com.example.unittestexample.models;

import com.example.unittestexample.exceptions.DuracaoMaiorQuePermitidoException;
import com.example.unittestexample.exceptions.FimAntesDoInicioException;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import java.time.Duration;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "TURMA")
public class Turma {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(name = "nome", length = 400, unique = true, nullable = false)
  private String nome;

  @Column(name = "horario_inicio", nullable = false)
  private LocalTime horarioInicio;

  @Column(name = "horario_fim", nullable = false)
  private LocalTime horarioFim;

  @Column(name = "duracao")
  private Integer duracao;

  @Column(name = "limite_turma", nullable = false)
  private Integer limiteTurma;

  @OneToMany(mappedBy = "turma", fetch = FetchType.LAZY)
  @JsonManagedReference
  private List<Aluno> alunos = new ArrayList<>();

  @PrePersist
  @PreUpdate
  private void calcularDuracao() {
    if (horarioInicio != null && horarioFim != null) {
      if (horarioFim.isBefore(horarioInicio)) {
        throw new FimAntesDoInicioException(horarioFim, horarioInicio);
      }

      long horas = Duration.between(horarioInicio, horarioFim).toHours();

      if (horas > 5) {
        throw new DuracaoMaiorQuePermitidoException((int) horas);
      }

      this.duracao = (int) horas;
    }
  }
}
