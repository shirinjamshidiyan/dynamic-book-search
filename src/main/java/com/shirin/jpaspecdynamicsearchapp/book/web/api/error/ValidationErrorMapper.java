package com.shirin.jpaspecdynamicsearchapp.book.web.api.error;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.validation.BindingResult;

@Component
@RequiredArgsConstructor
public class ValidationErrorMapper {

  private final MessageSource messageSource;

  public List<FieldErrorDTO> toFieldErrors(BindingResult bindingResult) {
    return bindingResult.getFieldErrors().stream()
        .map(
            error ->
                new FieldErrorDTO(
                    error.getField(),
                    messageSource.getMessage(error, LocaleContextHolder.getLocale())))
        .toList();
  }
}
