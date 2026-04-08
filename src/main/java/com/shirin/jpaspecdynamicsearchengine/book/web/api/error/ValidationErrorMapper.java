package com.shirin.jpaspecdynamicsearchengine.book.web.api.error;

import java.util.List;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.springframework.validation.BindingResult;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ValidationErrorMapper {
  public static List<FieldErrorDTO> toFieldErrors(BindingResult bindingResult) {
    return bindingResult.getFieldErrors().stream()
        .map(error -> new FieldErrorDTO(error.getField(), error.getDefaultMessage()))
        .toList();
  }
}
