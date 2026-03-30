package com.shirin.jpaspecdynamicsearchengine.book.web.api;

import com.shirin.jpaspecdynamicsearchengine.book.application.search.BookSearchCriteria;
import com.shirin.jpaspecdynamicsearchengine.book.application.search.BookSearchResult;
import com.shirin.jpaspecdynamicsearchengine.book.application.search.BookSearchUseCase;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/books")
public class BookSearchRestController {
  private final BookSearchUseCase service;

  @PostMapping("/search")
  public Page<BookSearchResult> search(
      @Valid @RequestBody BookSearchCriteria criteria,
      @PageableDefault(size = 10, sort = "title") Pageable pageable) {
    return service.search(criteria, pageable);
  }
}
