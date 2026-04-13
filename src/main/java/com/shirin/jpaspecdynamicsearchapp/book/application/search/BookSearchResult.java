package com.shirin.jpaspecdynamicsearchapp.book.application.search;

import java.math.BigDecimal;
import java.util.Set;

// return a result DTO instead of returning entities

public record BookSearchResult(
    Long id,
    String title,
    String genre,
    BigDecimal price,
    Integer publishYear,
    Boolean availability,
    Long publisherId,
    String publisherName,
    Set<String> authors) {}
