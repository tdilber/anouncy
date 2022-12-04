package com.beyt.anouncy.common.repository;

import com.beyt.anouncy.common.entity.neo4j.Announce;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface AnnounceRepository extends Neo4jRepository<Announce, String> {

    Page<Announce> findAllByAnonymousUserId(UUID anonymousUserId, Pageable pageable);

    Optional<Announce> findByIdAndAnonymousUserId(String announceId, UUID anonymousUserId);
}
