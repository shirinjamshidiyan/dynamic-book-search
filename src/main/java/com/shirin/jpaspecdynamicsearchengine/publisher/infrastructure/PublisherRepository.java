package com.shirin.jpaspecdynamicsearchengine.publisher.infrastructure;

import com.shirin.jpaspecdynamicsearchengine.publisher.domain.Publisher;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface PublisherRepository extends JpaRepository<Publisher, Long> {

  @Query("""
select p.name
from Publisher p
order by p.name
""")
  List<String> findAllNames();
}
