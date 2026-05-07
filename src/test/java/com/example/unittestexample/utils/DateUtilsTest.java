package com.example.unittestexample.utils;

import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDate;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

class DateUtilsTest {

  DateUtils dateUtils = Mockito.spy(new DateUtils());
  static final LocalDate DATA_ATUAL = LocalDate.of(2025, 10, 21);

  @Test
  void diferencaEmAnosDataAtual_AniversarioJaPassou() {
    LocalDate datePassou = LocalDate.of(2006, 6, 18);
    Mockito.when(dateUtils.dataAtual()).thenReturn(DATA_ATUAL);
    int idade = dateUtils.diferencaEmAnosDataAtual(datePassou);
    assertEquals(19, idade);
  }

  @Test
  void diferencaEmAnosDataAtual_AniversarioProximo() {
    LocalDate dateProxima = LocalDate.of(2006, 10, 22);
    Mockito.when(dateUtils.dataAtual()).thenReturn(DATA_ATUAL);
    int idade = dateUtils.diferencaEmAnosDataAtual(dateProxima);
    assertEquals(18, idade);
  }

  @Test
  void diferencaEmAnosDataAtual_AniversarioNoDia() {
    LocalDate dateNoDia = LocalDate.of(2006, 10, 21);
    Mockito.when(dateUtils.dataAtual()).thenReturn(DATA_ATUAL);
    int idade = dateUtils.diferencaEmAnosDataAtual(dateNoDia);
    assertEquals(19, idade);
  }

  @Test
  void diferencaEmAnosDataAtual_AniversarioFuturo() {
    LocalDate dataFutura = LocalDate.of(2026, 10, 22);
    Mockito.when(dateUtils.dataAtual()).thenReturn(DATA_ATUAL);
    int idade = dateUtils.diferencaEmAnosDataAtual(dataFutura);
    assertEquals(-1, idade);
  }

  @Test
  void recuperarDataEmAnos_idadeNormal() {
    Mockito.when(dateUtils.dataAtual()).thenReturn(DATA_ATUAL);
    int idade = 19;
    LocalDate data = dateUtils.recuperarDataEmAnos(idade);
    int ano = data.getYear();
    assertEquals(2006, ano);
  }

  @Test
  void recuperarDataEmAnos_IdadeZerada() {
    Mockito.when(dateUtils.dataAtual()).thenReturn(DATA_ATUAL);
    int idade = 0;
    LocalDate data = dateUtils.recuperarDataEmAnos(idade);
    int ano = data.getYear();
    assertEquals(2025, ano);
  }

  @Test
  void recuperarDataEmAnos_IdadeNegativa() {
    Mockito.when(dateUtils.dataAtual()).thenReturn(DATA_ATUAL);
    int idade = -3;
    LocalDate data = dateUtils.recuperarDataEmAnos(idade);
    int ano = data.getYear();
    assertEquals(2028, ano);
  }
}
