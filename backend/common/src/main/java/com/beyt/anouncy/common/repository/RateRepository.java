package com.beyt.anouncy.common.repository;

import com.beyt.anouncy.common.entity.neo4j.Rate;
import org.springframework.data.domain.Pageable;
import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface RateRepository extends Neo4jRepository<Rate, String> {

    List<Rate> findAllByAnonymousUserId(UUID anonymousUserId, Pageable pageable);

    Optional<Rate> findByAnonymousUserIdAndAnnounceId(UUID anonymousUserId, String announceId);

}
