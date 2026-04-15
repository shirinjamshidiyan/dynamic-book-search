package com.shirin.jpaspecdynamicsearchapp.book.web.api.error;

import java.time.LocalDateTime;
import java.util.List;

public record ApiError(
    LocalDateTime timestamp,
    int status,
    String error,
    String message,
    String path,
    List<FieldErrorDTO> fieldErrors) {
  public ApiError {
    fieldErrors = fieldErrors == null ? List.of() : List.copyOf(fieldErrors); // related to SpotBugs
  }
}
