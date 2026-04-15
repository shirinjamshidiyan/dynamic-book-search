package com.shirin.jpaspecdynamicsearchapp.book.application.search;

import java.util.List;

public record PageResponse<T>(
    List<T> content, int page, int size, long totalElements, int totalPages) {
  public PageResponse {
    content = content == null ? List.of() : List.copyOf(content); // related to SpotBugs
  }
}
