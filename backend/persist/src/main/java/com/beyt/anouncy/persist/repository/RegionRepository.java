package com.beyt.anouncy.persist.repository;

import com.beyt.anouncy.persist.entity.Region;
import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RegionRepository extends Neo4jRepository<Region, String> {

    List<Region> findAllByParentRegionIdIsIn(List<String> parentRegionIdList);

    List<Region> findAllByLocationIdIsIn(List<Long> locationIdList);

    Optional<Region> findByLocationId(Long locationId);

}
