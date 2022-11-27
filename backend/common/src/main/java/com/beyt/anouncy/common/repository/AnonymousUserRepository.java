package com.beyt.anouncy.common.repository;

import com.beyt.anouncy.common.entity.neo4j.AnonymousUser;
import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface AnonymousUserRepository extends Neo4jRepository<AnonymousUser, UUID> {

}
