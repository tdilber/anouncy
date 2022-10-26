package com.beyt.anouncy.location.service.provider;

import com.beyt.anouncy.location.entity.Location;

import java.util.List;

public interface LocationProvider {
    // Small First
    Integer priority();

    List<Location> findCoordinate(Double latitude, Double longitude);
}
