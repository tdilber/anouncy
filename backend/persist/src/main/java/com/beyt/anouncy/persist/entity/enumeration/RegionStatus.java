package com.beyt.anouncy.persist.entity.enumeration;

import com.beyt.anouncy.common.exception.DeveloperMistakeException;
import com.beyt.anouncy.common.persist.RegionStatusPTO;

import java.util.Objects;

public enum RegionStatus {
    ACTIVE, INACTIVE;

    public RegionStatusPTO of() {
        switch (this) {
            case ACTIVE -> {
                return RegionStatusPTO.ACTIVE;
            }
            case INACTIVE -> {
                return RegionStatusPTO.INACTIVE;
            }
            default -> throw new DeveloperMistakeException("UnKnown RegionStatus Enum!");
        }
    }

    public static RegionStatus of(RegionStatusPTO pto) {
        if (Objects.isNull(pto)) {
            return null;
        }

        switch (pto) {
            case ACTIVE -> {
                return ACTIVE;
            }
            case INACTIVE -> {
                return INACTIVE;
            }
            case UNRECOGNIZED -> {
                throw new DeveloperMistakeException("UnKnown RegionStatus Enum!");
            }
            default -> throw new DeveloperMistakeException("UnKnown RegionStatus Enum!");
        }
    }
}
