package com.beyt.anouncy.persist.repository;

import com.beyt.anouncy.persist.entity.Vote;
import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface VoteRepository extends Neo4jRepository<Vote, String> {

}
