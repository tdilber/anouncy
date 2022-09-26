package com.beyt.anouncy.user.repository;

import com.beyt.anouncy.user.entity.Configuration;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface ConfigurationRepository extends CrudRepository<Configuration, UUID> {

    @Override
    List<Configuration> findAll();
}
