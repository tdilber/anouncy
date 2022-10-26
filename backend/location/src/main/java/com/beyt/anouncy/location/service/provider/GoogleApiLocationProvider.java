package com.beyt.anouncy.location.service.provider;

import com.beyt.anouncy.common.exception.ClientErrorException;
import com.beyt.anouncy.location.entity.Location;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class GoogleApiLocationProvider implements LocationProvider {
    @Override
    public Integer priority() {
        return 2;
    }

    @Override
    public List<Location> findCoordinate(Double latitude, Double longitude) {
        //TODO implement it
        throw new ClientErrorException("location.not.found");
    }
}
