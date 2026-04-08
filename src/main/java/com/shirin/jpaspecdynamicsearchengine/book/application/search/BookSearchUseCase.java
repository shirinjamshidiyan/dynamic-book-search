package com.shirin.jpaspecdynamicsearchengine.book.application.search;

import com.shirin.jpaspecdynamicsearchengine.book.domain.Book;
import com.shirin.jpaspecdynamicsearchengine.book.infrastructure.BookRepository;
import com.shirin.jpaspecdynamicsearchengine.book.infrastructure.BookSpecificationBuilder;
import com.shirin.jpaspecdynamicsearchengine.publisher.infrastructure.PublisherRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class BookSearchUseCase {

  private final BookRepository bookRepository;
  private final PublisherRepository publisherRepository;

  public PageResponse<BookSearchResult> searchForApi(
      BookSearchCriteria criteria, Pageable pageable) {
    Page<BookSearchResult> page = searchPage(criteria, pageable);
    return new PageResponse<>(
        page.getContent(),
        page.getNumber(),
        page.getSize(),
        page.getTotalElements(),
        page.getTotalPages());
  }

  public Page<BookSearchResult> searchPage(BookSearchCriteria criteria, Pageable pageable) {
    Specification<Book> bookSpecification = BookSpecificationBuilder.buildSpecification(criteria);
    return bookRepository.findAll(bookSpecification, pageable).map(BookMapper::toResultDTO);
  }

  public List<String> getAllGenres() {
    return bookRepository.findAllGenres();
  }

  public List<String> getAllPublisherNames() {
    return publisherRepository.findAllNames();
  }
}
