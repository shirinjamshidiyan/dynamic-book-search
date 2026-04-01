package com.shirin.jpaspecdynamicsearchengine.book.application.search;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;
import java.math.BigDecimal;

public record BookSearchCriteria(
    @Size(max = 100, message = "Title must be at most 100 characters")
        String title, // partial match
    @Size(max = 50, message = "Genre must be at most 50 characters") String genre, // exact match
    @PositiveOrZero(message = "Minimum price must be zero or positive") BigDecimal minPrice,
    @PositiveOrZero(message = "Maximum price must be zero or positive") BigDecimal maxPrice,
    @Min(value = 1000, message = "publishYearFrom must be at least 1000")
        @Max(value = 2100, message = "publishYearFrom must be at most 2100")
        Integer publishYearFrom,
    @Min(value = 1000, message = "publishYearTo must be at least 1000")
        @Max(value = 2100, message = "publishYearTo must be at most 2100")
        Integer publishYearTo,
    Boolean availability,
    @Size(max = 100, message = "Author name must be at most 100 characters")
        String author, // partial match
    @Size(max = 100, message = "Publisher name must be at most 100 characters") String publisherName,  // partial match
    SearchMode searchMode) {

  public BookSearchCriteria {

    if (searchMode == null) {
      searchMode = SearchMode.AND;
    }
  }
}
