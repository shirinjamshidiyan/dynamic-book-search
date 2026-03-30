package com.shirin.jpaspecdynamicsearchengine.book.infrastructure;

import com.shirin.jpaspecdynamicsearchengine.book.application.search.BookSearchCriteria;
import com.shirin.jpaspecdynamicsearchengine.book.application.search.SearchMode;
import com.shirin.jpaspecdynamicsearchengine.book.domain.Book;
import com.shirin.jpaspecdynamicsearchengine.publisher.domain.Publisher;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.springframework.data.jpa.domain.Specification;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class BookSpecificationBuilder {

  // (partial, case-insensitive)
  private static Specification<Book> titleContains(String title) {
    return (root, query, cb) ->
        cb.like(cb.lower(root.get("title")), "%" + title.toLowerCase() + "%");
  }

  // (exact, case-insensitive)
  private static Specification<Book> genreEquals(String genre) {
    return (root, query, cb) -> cb.equal(cb.lower(root.get("genre")), genre.toLowerCase());
  }

  private static Specification<Book> availabilityEquals(Boolean available) {
    return (root, query, cb) -> cb.equal(root.get("availability"), available);
  }

  private static Specification<Book> priceInRange(BigDecimal minPrice, BigDecimal maxPrice) {
    return (root, query, cb) -> {
      if (minPrice != null && maxPrice != null) {
        return cb.between(root.get("price"), minPrice, maxPrice);
      } else if (maxPrice != null) {
        return cb.lessThanOrEqualTo(root.get("price"), maxPrice);
      }
      return cb.greaterThanOrEqualTo(root.get("price"), minPrice);
    };
  }

  private static Specification<Book> publishYearInRange(Integer yearFrom, Integer yearTo) {
    return (root, query, cb) -> {
      if (yearFrom != null && yearTo != null) {
        return cb.between(root.get("publishYear"), yearFrom, yearTo);
      } else if (yearTo != null) {
        return cb.lessThanOrEqualTo(root.get("publishYear"), yearTo);
      }
      return cb.greaterThanOrEqualTo(root.get("publishYear"), yearFrom);
    };
  }

  private static Specification<Book> authorContains(String author) {
    return (root, query, cb) -> {
      Join<Book, String> authorsJoin = root.joinSet("authors", JoinType.LEFT);
      query.distinct(true);
      return cb.like(cb.lower(authorsJoin), "%" + author.toLowerCase() + "%");
    };
  }

  private static Specification<Book> publisherNameContains(String publisherName) {
    return (root, query, cb) -> {
      Join<Book, Publisher> publisherJoin = root.join("publisher", JoinType.LEFT);
      query.distinct(true);
      return cb.like(cb.lower(publisherJoin.get("name")), "%" + publisherName.toLowerCase() + "%");
    };
  }

  public static Specification<Book> buildSpecification(BookSearchCriteria criteria) {
    if (criteria == null) {
      throw new IllegalArgumentException("criteria must not be null");
    }
    List<Specification<Book>> specs = new ArrayList<>();

    if (criteria.title() != null && !criteria.title().isBlank()) {
      specs.add(titleContains(criteria.title()));
    }
    if (criteria.genre() != null && !criteria.genre().isBlank()) {
      specs.add(genreEquals(criteria.genre()));
    }
    if (criteria.availability() != null) {
      specs.add(availabilityEquals(criteria.availability()));
    }
    if (criteria.minPrice() != null || criteria.maxPrice() != null) {
      specs.add(priceInRange(criteria.minPrice(), criteria.maxPrice()));
    }
    if (criteria.publishYearFrom() != null || criteria.publishYearTo() != null) {
      specs.add(publishYearInRange(criteria.publishYearFrom(), criteria.publishYearTo()));
    }
    if (criteria.author() != null && !criteria.author().isBlank()) {
      specs.add(authorContains(criteria.author()));
    }
    if (criteria.publisherName() != null && !criteria.publisherName().isBlank()) {
      specs.add(publisherNameContains(criteria.publisherName()));
    }

    Specification<Book> result = null;

    for (Specification<Book> sp : specs) {
      if (result == null) result = sp;
      else result = SearchMode.OR.equals(criteria.searchMode()) ? result.or(sp) : result.and(sp);
    }
    return result == null ? Specification.unrestricted() : result;
  }
}
