package com.shirin.jpaspecdynamicsearchengine.book.domain;

import com.shirin.jpaspecdynamicsearchengine.publisher.domain.Publisher;
import jakarta.persistence.*;
import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Set;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "books")
@NoArgsConstructor(access = AccessLevel.PROTECTED) // JPA
@Getter
public class Book {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(name = "publisher_id", nullable = false)
  private Long publisherId;

  @Column(nullable = false)
  private String title;

  @Column(name = "genre")
  private String genre;

  @Column(nullable = false, precision = 12, scale = 2)
  private BigDecimal price;

  @Column(name = "publish_year")
  private Integer publishYear;

  @Column(nullable = false)
  private Boolean availability = true;

  @ElementCollection
  @CollectionTable(name = "book_authors", joinColumns = @JoinColumn(name = "book_id"))
  @Column(name = "author", nullable = false)
  @org.hibernate.annotations.BatchSize(size = 50) // to avoid N+1 queries problem
  private Set<String> authors = new HashSet<>();

  // Read-only association for query convenience. publisherId remains the source of truth.
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "publisher_id", insertable = false, updatable = false)
  private Publisher publisher;
}
