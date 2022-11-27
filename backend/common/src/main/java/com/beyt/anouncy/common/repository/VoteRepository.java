package com.beyt.anouncy.common.repository;

import com.beyt.anouncy.common.entity.neo4j.Vote;
import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.data.neo4j.repository.query.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface VoteRepository extends Neo4jRepository<Vote, String> {

//    List<Vote> findAllByAnonymousUserId(UUID anonymousUserId, Pageable pageable);
//
//    Optional<Vote> findByAnonymousUserIdAndAnnounceId(UUID anonymousUserId, String announceId);


    @Query("MATCH (announce:Announce) WHERE announce.id = $announceId RETURN announce.id as announceId, COUNT { MATCH (announce)-[:USER_VOTE]->(vote:Vote) WHERE vote.value = true } as yes, COUNT { MATCH (announce)-[:USER_VOTE]->(vote:Vote) WHERE vote.value = true } as no ")
    List<VoteSummary> findByAnonymousUserIdAndAnnounceIdIsIn(UUID anonymousUserId, List<String> announceIdList);

    interface VoteSummary {
        String getId();

        Boolean getValue();

        String getAnnounceId();
    }
}
