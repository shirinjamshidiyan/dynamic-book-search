package com.shirin.jpaspecdynamicsearchapp.publisher.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

// No back-reference collection (@OneToMany). Publisher remains a separate aggregate.

@Entity
@Table(name = "publishers")
@NoArgsConstructor(access = AccessLevel.PROTECTED) // JPA
@Getter
public class Publisher {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(nullable = false, unique = true)
  private String name;

  @Column(name = "founded_year")
  private Integer foundedYear;
}
