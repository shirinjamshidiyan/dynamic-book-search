package com.shirin.jpaspecdynamicsearchengine.publisher.repository;

import com.shirin.jpaspecdynamicsearchengine.publisher.domain.Publisher;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface PublisherRepository extends JpaRepository<Publisher, Long> {

    @Query("""
select p.name
from Publisher p
order by p.name
""")
    List<String> findAllNames();
}
