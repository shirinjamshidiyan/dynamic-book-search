package com.shirin.jpaspecdynamicsearchengine.book.application.search;

import com.shirin.jpaspecdynamicsearchengine.book.domain.Book;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.util.Set;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class BookMapper {

    public static BookSearchResult toResultDTO(Book book) {

        if(book==null) {
            return null;
        }
        return new BookSearchResult(
                book.getId(),
                book.getTitle(),
                book.getGenre(),
                book.getPrice(),
                book.getPublishYear(),
                book.getAvailability(),
                book.getPublisherId(),
                book.getPublisher() != null ? book.getPublisher().getName() : null,
                Set.copyOf(book.getAuthors()) //Returns an unmodifiable Set
        );

    }
}
