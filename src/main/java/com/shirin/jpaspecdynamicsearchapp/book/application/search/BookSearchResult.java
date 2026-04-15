package com.shirin.jpaspecdynamicsearchapp.book.application.search;

import java.math.BigDecimal;
import java.util.Set;

public record BookSearchResult(
    Long id,
    String title,
    String genre,
    BigDecimal price,
    Integer publishYear,
    Boolean availability,
    Long publisherId,
    String publisherName,
    Set<String> authors) {
  public BookSearchResult {
    authors = authors == null ? Set.of() : Set.copyOf(authors); // related to SpotBugs
  }
}
