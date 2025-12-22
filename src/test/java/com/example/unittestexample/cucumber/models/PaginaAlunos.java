package com.example.unittestexample.cucumber.models;

import com.example.unittestexample.dtos.AlunoDto;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class PaginaAlunos {

  private List<AlunoDto> content;
  private PageMetadata page;

  @NoArgsConstructor
  @AllArgsConstructor
  @Data
  public static class PageMetadata {
    private int size;
    private int number;
    private long totalElements;
    private int totalPages;
  }
}
