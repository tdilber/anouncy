package com.beyt.anouncy.location.service.provider;

import com.beyt.anouncy.common.exception.ClientErrorException;
import com.beyt.anouncy.location.entity.Location;
import lombok.RequiredArgsConstructor;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;

@Service
@RequiredArgsConstructor
public class LocationProviderManager {
    private final List<LocationProvider> locationProviderList;

    public List<Location> findCoordinate(Double longitude, Double latitude) {
        List<LocationProvider> locationProviders = locationProviderList.stream().sorted(Comparator.comparing(LocationProvider::priority)).toList();
        for (LocationProvider locationProvider : locationProviders) {
            var locs = locationProvider.findCoordinate(latitude, longitude);
            if (CollectionUtils.isNotEmpty(locs)) {
                return locs;
            }
        }

        throw new ClientErrorException("location.not.found");
    }
}
