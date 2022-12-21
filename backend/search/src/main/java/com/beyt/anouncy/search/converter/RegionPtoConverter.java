package com.beyt.anouncy.search.converter;


import com.beyt.anouncy.common.entity.enumeration.RegionStatus;
import com.beyt.anouncy.common.entity.enumeration.RegionType;
import com.beyt.anouncy.common.v1.RegionPTO;
import com.beyt.anouncy.search.entity.AnnounceSearchItem;
import org.springframework.stereotype.Component;

import java.util.Objects;
import java.util.Optional;


@Component
public class RegionPtoConverter {

    public AnnounceSearchItem.RegionSearchItem toEntity(RegionPTO pto) {
        if (Objects.isNull(pto)) {
            return null;
        }

        AnnounceSearchItem.RegionSearchItem region = new AnnounceSearchItem.RegionSearchItem();
        region.setId(pto.getId());
        region.setName(pto.getName());
        region.setOrdinal(pto.getOrdinal());
        region.setLatitude(pto.getLatitude());
        region.setLongitude(pto.getLongitude());
        region.setLocationId(pto.getLocationId());
        region.setType(RegionType.of(pto.getType()));
        region.setStatus(RegionStatus.of(pto.getStatus()));

        return region;
    }

    public RegionPTO toPto(AnnounceSearchItem.RegionSearchItem region) {
        RegionPTO.Builder builder = RegionPTO.newBuilder()
                .setId(region.getId())
                .setName(region.getName())
                .setOrdinal(region.getOrdinal())
                .setLatitude(region.getLatitude())
                .setLongitude(region.getLongitude())
                .setLocationId(region.getLocationId());
        Optional.ofNullable(region.getType()).map(RegionType::of).ifPresent(builder::setType);
        Optional.ofNullable(region.getStatus()).map(RegionStatus::of).ifPresent(builder::setStatus);

        return builder
                .build();
    }
}
