package com.beyt.anouncy.persist.repository;

import com.beyt.anouncy.persist.entity.AnonymousUser;
import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AnonymousUserRepository extends Neo4jRepository<AnonymousUser, String> {

}
