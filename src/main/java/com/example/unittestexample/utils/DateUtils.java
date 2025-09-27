package com.example.unittestexample.utils;

import java.time.LocalDate;
import java.time.ZoneId;
import org.springframework.stereotype.Component;

@Component
public class DateUtils {

  LocalDate dataAtual() {
    return LocalDate.now(ZoneId.of("America/Sao_Paulo"));
  }

  public int diferencaEmAnosDataAtual(LocalDate data) {
    return data.until(dataAtual()).getYears();
  }

  public LocalDate recuperarDataEmAnos(int anos) {
    return dataAtual().minusYears(anos);
  }
}
