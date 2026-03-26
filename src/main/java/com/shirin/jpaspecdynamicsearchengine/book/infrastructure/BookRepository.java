package com.shirin.jpaspecdynamicsearchengine.book.infrastructure;

import com.shirin.jpaspecdynamicsearchengine.book.domain.Book;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface BookRepository extends JpaRepository<Book,Long>, JpaSpecificationExecutor<Book> {
}
