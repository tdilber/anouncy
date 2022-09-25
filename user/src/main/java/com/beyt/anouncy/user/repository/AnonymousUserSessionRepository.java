package com.beyt.anouncy.user.repository;

import com.beyt.anouncy.user.entity.AnonymousUser;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AnonymousUserSessionRepository extends CrudRepository<AnonymousUser, String> {

}
