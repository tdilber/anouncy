package com.beyt.anouncy.persist.repository;

import com.beyt.anouncy.persist.entity.Announce;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AnnounceRepository extends Neo4jRepository<Announce, String> {

    Page<Announce> findAllByAnonymousUserId(String anonymousUserId, Pageable pageable);

    Optional<Announce> findByIdAndAnonymousUserId(String announceId, String anonymousUserId);
}
