package com.beyt.anouncy.user.repository;

import com.beyt.anouncy.user.entity.AnonymousUserSession;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AnonymousUserSessionRepository extends CrudRepository<AnonymousUserSession, String> {

}
