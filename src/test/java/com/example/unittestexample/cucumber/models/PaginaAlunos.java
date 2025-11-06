package com.example.unittestexample.cucumber.models;

import com.example.unittestexample.dtos.AlunoDto;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.web.PagedModel;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class PaginaAlunos {

  private List<AlunoDto> content;
  private PagedModel.PageMetadata page;

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
