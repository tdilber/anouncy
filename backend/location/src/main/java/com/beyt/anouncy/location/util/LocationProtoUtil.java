package com.beyt.anouncy.location.util;

import com.beyt.anouncy.common.entity.enumeration.LocationType;
import com.beyt.anouncy.common.v1.LocationListPTO;
import com.beyt.anouncy.common.v1.LocationPTO;
import com.beyt.anouncy.common.v1.RegionTypePTO;
import com.beyt.anouncy.location.entity.Location;

import java.util.List;
import java.util.Objects;

public final class LocationProtoUtil {
    private LocationProtoUtil() {
    }

    public static LocationListPTO getLocationListPTO(List<Location> parents) {
        return LocationListPTO.newBuilder().addAllLocationList(parents.stream().map(LocationProtoUtil::to).toList()).build();
    }

    public static LocationPTO to(Location l) {
        return LocationPTO.newBuilder()
                .setId(l.getId())
                .setName(l.getName())
                .setOrdinal(l.getOrdinal())
                .setLatitude(l.getLatitude())
                .setLongitude(l.getLongitude())
                .setType(convertType(l.getType()))
                .setParentLocationId(l.getParentLocationId())
                .build();
    }

    public static RegionTypePTO convertType(LocationType type) {
        if (Objects.isNull(type)) {
            return RegionTypePTO.UNRECOGNIZED;
        }

        switch (type) {

            case COUNTRY -> {
                return RegionTypePTO.COUNTRY;
            }
            case CITY -> {
                return RegionTypePTO.CITY;
            }
            case COUNTY -> {
                return RegionTypePTO.COUNTY;
            }
            case REGION, STREET, VILLAGE, TOWN, NONE -> {
                return RegionTypePTO.UNRECOGNIZED;
            }
            default -> {
                return RegionTypePTO.UNRECOGNIZED;
            }
        }
    }
}
