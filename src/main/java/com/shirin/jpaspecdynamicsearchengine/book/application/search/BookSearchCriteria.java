package com.shirin.jpaspecdynamicsearchengine.book.application.search;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;
import java.math.BigDecimal;

public record BookSearchCriteria(
    @Size(max = 100, message = "{validation.title.size}") String title, // partial match
    @Size(max = 50, message = "{validation.genre.size}") String genre, // exact match
    @PositiveOrZero(message = "{validation.price.min.positiveOrZero}") BigDecimal minPrice,
    @PositiveOrZero(message = "{validation.price.max.positiveOrZero}") BigDecimal maxPrice,
    @Min(value = 1000, message = "{validation.publishYearFrom.min}")
        @Max(value = 2100, message = "{validation.publishYearFrom.max}")
        Integer publishYearFrom,
    @Min(value = 1000, message = "{validation.publishYearTo.min}")
        @Max(value = 2100, message = "{validation.publishYearTo.max}")
        Integer publishYearTo,
    Boolean availability,
    @Size(max = 100, message = "{validation.author.size}") String author, // partial match
    @Size(max = 100, message = "{validation.publisherName.size}")
        String publisherName, // partial match
    SearchMode searchMode) {

  public BookSearchCriteria {

    if (searchMode == null) {
      searchMode = SearchMode.AND;
    }
  }
}
