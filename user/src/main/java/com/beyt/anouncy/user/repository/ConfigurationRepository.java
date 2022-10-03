package com.beyt.anouncy.user.repository;

import com.beyt.anouncy.user.entity.Configuration;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface ConfigurationRepository extends JpaRepository<Configuration, UUID> {

    @Override
    List<Configuration> findAll();
}
