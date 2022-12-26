package com.beyt.anouncy.common.entity.enumeration;

import com.beyt.anouncy.common.exception.DeveloperMistakeException;
import com.beyt.anouncy.common.v1.RegionTypePTO;

import java.util.Objects;

public enum RegionType {
    COUNTRY, REGION, CITY, COUNTY;

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

    public RegionTypePTO of() {
        switch (this) {
            case COUNTRY -> {
                return RegionTypePTO.COUNTRY;
            }
            case CITY -> {
                return RegionTypePTO.CITY;
            }
            case COUNTY -> {
                return RegionTypePTO.COUNTY;
            }
            default -> throw new DeveloperMistakeException("UnKnown RegionType Enum!");
        }
    }

    public static RegionType of(RegionTypePTO pto) {
        if (Objects.isNull(pto)) {
            return null;
        }

        switch (pto) {
            case COUNTRY -> {
                return COUNTRY;
            }
            case CITY -> {
                return CITY;
            }
            case COUNTY -> {
                return COUNTY;
            }
            case UNRECOGNIZED -> {
                throw new DeveloperMistakeException("UnKnown RegionType Enum!");
            }
            default -> throw new DeveloperMistakeException("UnKnown RegionType Enum!");
        }
    }
}
