package com.shirin.jpaspecdynamicsearchengine.book.application.search;

import com.shirin.jpaspecdynamicsearchengine.book.domain.Book;
import com.shirin.jpaspecdynamicsearchengine.book.infrastructure.BookRepository;
import com.shirin.jpaspecdynamicsearchengine.book.infrastructure.BookSpecificationBuilder;
import lombok.AllArgsConstructor;
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

    public Page<BookSearchResult> search(BookSearchCriteria criteria, Pageable pageable)
    {
        Specification<Book> bookSpecification = BookSpecificationBuilder.buildSpecification(criteria);
        return bookRepository
                .findAll(bookSpecification, pageable)
                .map(BookMapper::toResultDTO);
    }


    /*
    public List<String> getAllPublishers()
    {
        return bookPublisherRepository.findAll()
                .stream()
                .map(BookPublisher::getName)
                .distinct()
                .sorted()
                .collect(Collectors.toList());
    }
    public List<String> getAllGenres()
    {
       return bookRepository.findAll()
                .stream()
                .map(Book::getGenre)
                .distinct()
                .sorted()
                .collect(Collectors.toList());
    }
     */
}
