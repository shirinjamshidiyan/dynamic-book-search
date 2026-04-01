package com.shirin.jpaspecdynamicsearchengine.book.application.search;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.springframework.validation.Errors;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class BookSearchCriteriaValidator {

    public static void validate(BookSearchCriteria criteria, Errors errors){
        if (criteria == null) {
            return;
        }
        if (criteria.minPrice() != null && criteria.maxPrice() != null &&
                criteria.minPrice().compareTo(criteria.maxPrice()) > 0) {
            errors.rejectValue(
                    "minPrice",
                    "bookSearch.minPrice.greaterThanMaxPrice",
                    "Minimum price cannot be greater than maximum price.");
        }
        if (criteria.publishYearFrom() != null && criteria.publishYearTo() != null
                && criteria.publishYearFrom() > criteria.publishYearTo()) {
            errors.rejectValue(
                    "publishYearFrom",
                    "bookSearch.publishYearFrom.greaterThanPublishYearTo",
                    "Minimum price cannot be greater than maximum price.");
        }
    }
}