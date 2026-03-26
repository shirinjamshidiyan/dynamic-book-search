package com.shirin.jpaspecdynamicsearchengine.book.application.search;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.math.BigDecimal;


 class BookSearchCriteriaTest {

    @Test
    void defaultSearchModeMustBeAndWhenNull() {
        BookSearchCriteria criteria = new BookSearchCriteria(
                "Java",
                "Programming",
                BigDecimal.ZERO,
                BigDecimal.TEN,
                2000,
                2025,
                true,
                "XXX",
                "YYY",
                null
        );
        assertEquals(SearchMode.AND, criteria.searchMode());
    }
    @Test
    void minPriceGreaterThanMaxPriceMustThrowException() {
        assertThrows(
                IllegalArgumentException.class,
                () -> new BookSearchCriteria(
                        "Java",
                        "Programming",
                        BigDecimal.valueOf(100),
                        BigDecimal.valueOf(50),
                        2000,
                        2025,
                        true,
                        "XXX",
                        "YYY",
                        SearchMode.AND
                )
        );
    }

    @Test
    void publishYearFromGreaterThanPublishYearToMustThrowException() {
         assertThrows(
                IllegalArgumentException.class,
                () -> new BookSearchCriteria(
                        "Java",
                        "Programming",
                        BigDecimal.ZERO,
                        BigDecimal.TEN,
                        2025,
                        2020,
                        true,
                        "XXX",
                        "YYY",
                        SearchMode.AND
                )
        );
    }
}
