package com.beyt.anouncy.location.service.provider;

import com.beyt.anouncy.location.entity.Location;
import com.beyt.anouncy.location.enumeration.LocationType;
import com.beyt.anouncy.location.repository.LocationRepository;
import lombok.RequiredArgsConstructor;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;

@Service
@RequiredArgsConstructor
public class MongoLocationProvider implements LocationProvider {
    private final LocationRepository locationRepository;

    @Override
    public Integer priority() {
        return 1;
    }

    @Override
    public List<Location> findCoordinate(Double latitude, Double longitude) {
        List<Location> locations = locationRepository.findAllByPointIntersect(latitude, longitude);
        locations = validate(locations);
        return locations;
    }

    private List<Location> validate(List<Location> locations) {
        List<Location> countyList = locations.stream().filter(l -> l.getType().equals(LocationType.COUNTY)).toList();
        if (countyList.size() != 1) {
            return Collections.EMPTY_LIST;
        }

//        long count = locations.stream().filter(l -> locations.stream().noneMatch(l2 -> l.getId().equals(l.getParentLocationId()))).count();
        if (validateTree(locations, countyList.get(0))) {
            return locations;
        }

        List<Long> parentLocationIdList = countyList.get(0).getParentLocationIdList();
        if (CollectionUtils.isNotEmpty(parentLocationIdList)) {
            return locationRepository.findAllByIdIsIn(parentLocationIdList);
        }

        return Collections.EMPTY_LIST;
    }

    private boolean validateTree(List<Location> locations, Location child) {
        if (locations.size() <= 1) {
            return true;
        }

        var parentOpt = locations.stream().filter(l -> l.getId().equals(child.getParentLocationId())).findFirst();
        if (parentOpt.isEmpty()) {
            return false;
        }
        return validateTree(locations, parentOpt.get());
    }
}
