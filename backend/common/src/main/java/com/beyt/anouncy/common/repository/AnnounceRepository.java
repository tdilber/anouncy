package com.beyt.anouncy.common.repository;

import com.beyt.anouncy.common.entity.neo4j.Announce;
import org.springframework.data.domain.Pageable;
import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface AnnounceRepository extends Neo4jRepository<Announce, String> {

    List<Announce> findAllByAnonymousUserId(UUID anonymousUserId, Pageable pageable);
}
