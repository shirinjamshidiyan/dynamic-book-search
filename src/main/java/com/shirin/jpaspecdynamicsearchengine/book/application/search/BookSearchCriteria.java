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
    @PositiveOrZero(message = "minPrice must be zero or positive") BigDecimal minPrice,
    @PositiveOrZero(message = "maxPrice must be zero or positive") BigDecimal maxPrice,
    @Min(value = 1000, message = "publishYearFrom must be at least 1000")
        @Max(value = 2030, message = "publishYearFrom must be at most 2030")
        Integer publishYearFrom,
    @Min(value = 1000, message = "publishYearTo must be at least 1000")
        @Max(value = 2030, message = "publishYearTo must be at most 2030")
        Integer publishYearTo,
    Boolean availability,
    @Size(max = 100, message = "Author name must be at most 100 characters")
        String author, // partial match
    @Size(max = 100, message = "PublisherName must be at most 100 characters") String publisherName,
    SearchMode searchMode) {

  public BookSearchCriteria {
    if (minPrice != null && maxPrice != null && minPrice.compareTo(maxPrice) > 0) {
      throw new IllegalArgumentException("minPrice cannot be greater than maxPrice");
    }

    if (publishYearFrom != null && publishYearTo != null && publishYearFrom > publishYearTo) {
      throw new IllegalArgumentException("publishYearFrom cannot be greater than publishYearTo");
    }

    if (searchMode == null) {
      searchMode = SearchMode.AND;
    }
  }
}
