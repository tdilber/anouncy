package com.beyt.anouncy.location.repository;

import com.beyt.anouncy.location.entity.Location;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Spring Data MongoDB repository for the Location entity.
 */
@Repository
@SuppressWarnings("unused")
public interface LocationRepository extends MongoRepository<Location, Long> {

    @Query("{\"boundary.geometry\": {$geoIntersects: {$geometry: {\"type\":\"Point\",\"coordinates\": [?1, ?0] } }}}")
    List<Location> findAllByPointIntersect(Double latitude, Double longitude);

    List<Location> findAllByIdIsIn(List<Long> idList);

    List<Location> findAllByParentLocationIdIsIn(List<Long> parentIdList);

    List<Location> findAllByParentLocationIdIsInAndId(List<Long> parentIdList, Long locationId);
}
