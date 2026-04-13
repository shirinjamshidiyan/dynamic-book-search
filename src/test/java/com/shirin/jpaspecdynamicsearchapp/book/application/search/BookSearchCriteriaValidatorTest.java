package com.shirin.jpaspecdynamicsearchapp.book.application.search;

import static org.assertj.core.api.Assertions.assertThat;

import java.math.BigDecimal;
import org.junit.jupiter.api.Test;
import org.springframework.validation.BeanPropertyBindingResult;

public class BookSearchCriteriaValidatorTest {

  @Test
  void doNothingWhenCriteriaIsNull() {
    BeanPropertyBindingResult errors = new BeanPropertyBindingResult(new Object(), "criteria");
    BookSearchCriteriaValidator.validate(null, errors);
    assertThat(errors.hasErrors()).isFalse();
  }

  @Test
  void rejectsWhenMinPriceExceedsMaxPrice() {
    BookSearchCriteria criteria =
        new BookSearchCriteria(
            null,
            null,
            new BigDecimal("50.00"),
            new BigDecimal("20.00"),
            null,
            null,
            null,
            null,
            null,
            SearchMode.AND);

    BeanPropertyBindingResult errors = new BeanPropertyBindingResult(criteria, "criteria");
    BookSearchCriteriaValidator.validate(criteria, errors);

    assertThat(errors.hasFieldErrors("minPrice")).isTrue();
    assertThat(errors.getFieldError("minPrice").getCode())
        .isEqualTo("bookSearch.minPrice.greaterThanMaxPrice");
  }

  @Test
  void doesNotRejectWhenMinPriceEqualsToMaxPrice() {
    BookSearchCriteria criteria =
        new BookSearchCriteria(
            null,
            null,
            new BigDecimal("20.00"),
            new BigDecimal("20.00"),
            null,
            null,
            null,
            null,
            null,
            SearchMode.AND);

    BeanPropertyBindingResult errors = new BeanPropertyBindingResult(criteria, "criteria");
    BookSearchCriteriaValidator.validate(criteria, errors);
    assertThat(errors.hasFieldErrors("minPrice")).isFalse();
  }

  @Test
  void doesNotRejectWhenOnlyMinPriceIsProvided() {
    BookSearchCriteria criteria =
        new BookSearchCriteria(
            null,
            null,
            new BigDecimal("20.00"),
            null,
            null,
            null,
            null,
            null,
            null,
            SearchMode.AND);

    BeanPropertyBindingResult errors = new BeanPropertyBindingResult(criteria, "criteria");
    BookSearchCriteriaValidator.validate(criteria, errors);
    assertThat(errors.hasFieldErrors("minPrice")).isFalse();
  }

  @Test
  void rejectsWhenPublishYearFromExceedsPublishYearTo() {
    BookSearchCriteria criteria =
        new BookSearchCriteria(
            null, null, null, null, 2025, 2020, null, null, null, SearchMode.AND);

    BeanPropertyBindingResult errors = new BeanPropertyBindingResult(criteria, "criteria");
    BookSearchCriteriaValidator.validate(criteria, errors);

    assertThat(errors.hasFieldErrors("publishYearFrom")).isTrue();
    assertThat(errors.getFieldError("publishYearFrom").getCode())
        .isEqualTo("bookSearch.publishYearFrom.greaterThanPublishYearTo");
  }

  @Test
  void doesNotRejectWhenPublishYearFromIsLessThanPublishYearTo() {
    BookSearchCriteria criteria =
        new BookSearchCriteria(
            null, null, null, null, 2015, 2020, null, null, null, SearchMode.AND);

    BeanPropertyBindingResult errors = new BeanPropertyBindingResult(criteria, "criteria");
    BookSearchCriteriaValidator.validate(criteria, errors);

    assertThat(errors.hasFieldErrors("publishYearFrom")).isFalse();
  }

  @Test
  void doesNotRejectWhenOnlyPublishYearToIsProvided() {
    BookSearchCriteria criteria =
        new BookSearchCriteria(
            null, null, null, null, null, 2020, null, null, null, SearchMode.AND);

    BeanPropertyBindingResult errors = new BeanPropertyBindingResult(criteria, "criteria");
    BookSearchCriteriaValidator.validate(criteria, errors);

    assertThat(errors.hasErrors()).isFalse();
  }
}
