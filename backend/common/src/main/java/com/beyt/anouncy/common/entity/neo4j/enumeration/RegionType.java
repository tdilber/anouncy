package com.beyt.anouncy.common.entity.neo4j.enumeration;

import com.beyt.anouncy.common.entity.enumeration.LocationType;

public enum RegionType {
    COUNTRY, CITY, COUNTY;

    public LocationType convert() {
        switch (this) {
            case COUNTRY -> {
                return LocationType.COUNTRY;
            }
            case CITY -> {
                return LocationType.CITY;
            }
            case COUNTY -> {
                return LocationType.COUNTY;
            }
            default -> throw new IllegalStateException();
        }
    }
}
