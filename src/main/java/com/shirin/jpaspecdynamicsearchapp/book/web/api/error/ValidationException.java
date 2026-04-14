package com.shirin.jpaspecdynamicsearchapp.book.web.api.error;

import java.util.List;

public class ValidationException extends RuntimeException {
  private final List<FieldErrorDTO> fieldErrors;

  public ValidationException(List<FieldErrorDTO> fieldErrors) {
    super("Validation failed");
    this.fieldErrors = fieldErrors == null ? List.of() : List.copyOf(fieldErrors);
  }

  public List<FieldErrorDTO> getFieldErrors() {
    return fieldErrors;
  }
}
