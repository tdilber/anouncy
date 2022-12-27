package com.beyt.anouncy.region.util;

import com.beyt.anouncy.common.v1.LocationPTO;
import com.beyt.anouncy.common.v1.RegionPTO;
import com.beyt.anouncy.common.v1.RegionStatusPTO;

public final class RegionProtoUtil {

    private RegionProtoUtil() {
    }


    public static RegionPTO to(LocationPTO c) {
        return RegionPTO.newBuilder()
                .setLongitude(c.getLongitude())
                .setLatitude(c.getLatitude())
                .setName(c.getName())
                .setLocationId(c.getId())
                .setStatus(RegionStatusPTO.ACTIVE)
                .setType(c.getType())
                .setOrdinal(c.getOrdinal())
                .build();
    }
}
