package com.shirin.jpaspecdynamicsearchengine.book.infrastructure;

import com.shirin.jpaspecdynamicsearchengine.book.domain.Book;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface BookRepository extends JpaRepository<Book, Long>, JpaSpecificationExecutor<Book> {
  @Override
  @EntityGraph(attributePaths = "publisher")
  Page<Book> findAll(Specification<Book> spec, Pageable pageable);

  @Query("""
select distinct b.genre
from Book b
order by b.genre
""")
  List<String> findAllGenres();
}
