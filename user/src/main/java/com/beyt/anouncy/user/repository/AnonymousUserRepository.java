package com.beyt.anouncy.user.repository;

import com.beyt.anouncy.user.entity.AnonymousUser;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface AnonymousUserRepository extends CrudRepository<AnonymousUser, UUID> {

}
