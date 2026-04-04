package com.shirin.jpaspecdynamicsearchengine.book.domain;
import com.shirin.jpaspecdynamicsearchengine.publisher.domain.Publisher;
import com.shirin.jpaspecdynamicsearchengine.book.application.search.BookSearchCriteria;
import com.shirin.jpaspecdynamicsearchengine.book.application.search.SearchMode;
import com.shirin.jpaspecdynamicsearchengine.book.infrastructure.BookRepository;
import com.shirin.jpaspecdynamicsearchengine.book.infrastructure.BookSpecificationBuilder;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.util.ReflectionTestUtils;

import java.lang.reflect.Constructor;
import java.math.BigDecimal;
import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DataJpaTest(properties={"spring.sql.init.mode=never"})
class BookSpecificationBuilderDataJpaTest {

    @Autowired
    private BookRepository bookRepository;
    @Autowired
    private EntityManager entityManager;

    @BeforeEach
    void createDatabaseForTest() {
        Publisher prenticeHall = persistPublisher("Prentice Hall", 1913);
        Publisher addisonWesley = persistPublisher("Addison-Wesley", 1942);
        Publisher oreilly = persistPublisher("O'Reilly", 1978);

        persistBook( prenticeHall.getId(), "Clean Code","Programming",
                new BigDecimal("40.00"),2008,true, Set.of("Robert C. Martin")
        );

        persistBook(addisonWesley.getId(),"Effective Java","Programming",
                new BigDecimal("55.00"),2018,true, Set.of("Joshua Bloch")
        );

        persistBook(oreilly.getId(), "Designing Data Intensive Applications","Software Architecture",
                new BigDecimal("65.00"),2017,false, Set.of("Martin Kleppmann")
        );

        persistBook( prenticeHall.getId(),"Clean Architecture","Programming",
                new BigDecimal("45.00"), 2017, true, Set.of("Robert C. Martin")
        );

        entityManager.flush();
        entityManager.clear();
    }

    @Test
    void throwsExceptionWhenCriteriaIsNull() {
        assertThatThrownBy(() -> BookSpecificationBuilder.buildSpecification(null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("criteria must not be null");
    }
    @Test
    void returnsAllBooksWhenCriteriaIsEmpty() {
        BookSearchCriteria criteria = new BookSearchCriteria(
                null, null, null, null, null, null, null, null, null, SearchMode.AND);

        List<Book> result = bookRepository.findAll(BookSpecificationBuilder.buildSpecification(criteria));
        assertThat(result).hasSize(4);
    }
    @Test
    void filtersByTitleContainsIgnoringCase() {
        BookSearchCriteria criteria = new BookSearchCriteria(
                "clean", null, null, null, null, null, null, null, null, SearchMode.AND);
        List<Book> result = bookRepository.findAll(BookSpecificationBuilder.buildSpecification(criteria));

        assertThat(result.stream().map(Book::getTitle).toList())
                .containsExactlyInAnyOrder("Clean Code", "Clean Architecture");
    }
    @Test
    void filtersByGenreIgnoringCase() {
        BookSearchCriteria criteria = new BookSearchCriteria(
                null, "programming", null, null, null, null, null, null, null, SearchMode.AND);

        List<Book> result = bookRepository.findAll(BookSpecificationBuilder.buildSpecification(criteria));
        assertThat(result.stream().map(Book::getTitle).toList())
                .containsExactlyInAnyOrder("Clean Code", "Effective Java", "Clean Architecture");
    }
    @Test
    void filtersByPriceRange() {
        BookSearchCriteria criteria = new BookSearchCriteria(
                null, null, new BigDecimal("41.00"), new BigDecimal("60.00"),
                null, null, null, null, null, SearchMode.AND);

        List<Book> result = bookRepository.findAll(BookSpecificationBuilder.buildSpecification(criteria));
        assertThat(result.stream().map(Book::getTitle).toList())
                .containsExactlyInAnyOrder("Effective Java", "Clean Architecture");
    }
    @Test
    void filtersByAuthorContains() {
        BookSearchCriteria criteria = new BookSearchCriteria(
                null, null, null, null, null, null, null, "martin", null, SearchMode.AND);

        List<Book> result = bookRepository.findAll(BookSpecificationBuilder.buildSpecification(criteria));
        assertThat(result.stream().map(Book::getTitle).toList())
                .containsExactlyInAnyOrder("Clean Code", "Clean Architecture","Designing Data Intensive Applications");
    }
    @Test
    void filtersByPublisherNameContains() {
        BookSearchCriteria criteria = new BookSearchCriteria(
                null, null, null, null, null, null, null, null, "prentice", SearchMode.AND);

        List<Book> result = bookRepository.findAll(BookSpecificationBuilder.buildSpecification(criteria));
        assertThat(result.stream().map(Book::getTitle).toList())
                .containsExactlyInAnyOrder("Clean Code", "Clean Architecture");
    }
    @Test
    void combinesFiltersWithAnd() {
        BookSearchCriteria criteria = new BookSearchCriteria(
                "clean",
                "programming",
                new BigDecimal("41.00"),
                new BigDecimal("60.00"),
                null,
                null,
                true,
                null,
                null,
                SearchMode.AND
        );

        List<Book> result = bookRepository.findAll(BookSpecificationBuilder.buildSpecification(criteria));
        assertThat(result.stream().map(Book::getTitle).toList()).containsExactly("Clean Architecture");
    }
    @Test
    void combinesFiltersWithOr() {
        BookSearchCriteria criteria = new BookSearchCriteria(
                "clean",
                "software architecture",
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                SearchMode.OR
        );

        List<Book> result = bookRepository.findAll(BookSpecificationBuilder.buildSpecification(criteria));
        assertThat(result.stream().map(Book::getTitle).toList())
                .containsExactlyInAnyOrder("Clean Code", "Clean Architecture", "Designing Data Intensive Applications");
    }

    private Publisher persistPublisher(String name, Integer foundedYear) {
        Publisher publisher = newPublisher(name, foundedYear);
        entityManager.persist(publisher);
        return publisher;
    }

    private Publisher newPublisher(String name, Integer foundedYear) {
        try {
            Constructor<Publisher> ctor = Publisher.class.getDeclaredConstructor();
            ctor.setAccessible(true);
            Publisher publisher = ctor.newInstance();
            ReflectionTestUtils.setField(publisher, "name", name);
            ReflectionTestUtils.setField(publisher, "foundedYear", foundedYear);
            return publisher;
        } catch (Exception e) {
            throw new RuntimeException("Failed to create test publisher", e);
        }
    }

    private Book persistBook(
            Long publisherId,
            String title,
            String genre,
            BigDecimal price,
            Integer publishYear,
            Boolean availability,
            Set<String> authors
    ) {
        Book book = new Book();
        ReflectionTestUtils.setField(book, "publisherId", publisherId);
        ReflectionTestUtils.setField(book, "title", title);
        ReflectionTestUtils.setField(book, "genre", genre);
        ReflectionTestUtils.setField(book, "price", price);
        ReflectionTestUtils.setField(book, "publishYear", publishYear);
        ReflectionTestUtils.setField(book, "availability", availability);
        ReflectionTestUtils.setField(book, "authors", authors);
        entityManager.persist(book);
        return book;
    }
}