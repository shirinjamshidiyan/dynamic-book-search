package com.shirin.jpaspecdynamicsearchengine.book.application.search;

import com.shirin.jpaspecdynamicsearchengine.book.domain.Book;
import com.shirin.jpaspecdynamicsearchengine.book.infrastructure.BookRepository;
import com.shirin.jpaspecdynamicsearchengine.book.infrastructure.BookSpecificationBuilder;
import com.shirin.jpaspecdynamicsearchengine.publisher.domain.Publisher;
import com.shirin.jpaspecdynamicsearchengine.publisher.repository.PublisherRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class BookSearchUseCase {

  private final BookRepository bookRepository;

  public Page<BookSearchResult> search(BookSearchCriteria criteria, Pageable pageable) {
    Specification<Book> bookSpecification = BookSpecificationBuilder.buildSpecification(criteria);
    return bookRepository.findAll(bookSpecification, pageable).map(BookMapper::toResultDTO);
  }

  public List<String> getAllGenres()
  {
    return bookRepository.findAllGenres();
  }

}
