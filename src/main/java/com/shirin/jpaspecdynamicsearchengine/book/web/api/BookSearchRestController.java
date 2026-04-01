package com.shirin.jpaspecdynamicsearchengine.book.web.api;

import com.shirin.jpaspecdynamicsearchengine.book.application.search.*;
import com.shirin.jpaspecdynamicsearchengine.book.web.api.error.ValidationErrorMapper;
import com.shirin.jpaspecdynamicsearchengine.book.web.api.error.ValidationException;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Valid;
import jakarta.validation.Validator;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.properties.bind.validation.ValidationErrors;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import java.util.Set;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/books")
public class BookSearchRestController {
  private final BookSearchUseCase bookSearchUseCase;
  private final Validator validator;

  @PostMapping("/search")
  public PageResponse<BookSearchResult> search(
      @RequestBody BookSearchCriteria criteria,
      @PageableDefault(size = 10, sort = "title") Pageable pageable) {

    BeanPropertyBindingResult errors = new BeanPropertyBindingResult(criteria, "criteria");

    applyBeanValidation(criteria, errors);
    BookSearchCriteriaValidator.validate(criteria, errors);

    if (errors.hasErrors()) {
      throw new ValidationException(ValidationErrorMapper.toFieldErrors(errors));
    }

    return bookSearchUseCase.searchForApi(criteria, pageable);
  }

  private void applyBeanValidation(BookSearchCriteria criteria, BeanPropertyBindingResult errors) {
    Set<ConstraintViolation<BookSearchCriteria>> violations = validator.validate(criteria);

    for (ConstraintViolation<BookSearchCriteria> violation : violations) {
      String field = violation.getPropertyPath().toString();
      String message = violation.getMessage();
      errors.rejectValue(field, field, message);
    }
  }
}
