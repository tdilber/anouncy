package com.beyt.anouncy.persist.helper;


import com.beyt.anouncy.common.entity.enumeration.RegionStatus;
import com.beyt.anouncy.common.entity.enumeration.RegionType;
import com.beyt.anouncy.common.v1.RegionListPTO;
import com.beyt.anouncy.common.v1.RegionOptionalPTO;
import com.beyt.anouncy.common.v1.RegionPTO;
import com.beyt.anouncy.persist.entity.Region;
import com.beyt.anouncy.persist.helper.base.PtoConverter;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.logging.log4j.util.Strings;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.StreamSupport;


@Component
public class RegionPtoConverter implements PtoConverter<Region, RegionPTO, RegionListPTO, RegionOptionalPTO> {

    @Override
    public Region toEntity(RegionPTO pto) {
        if (Objects.isNull(pto)) {
            return null;
        }

        Region region = new Region();
        if (pto.hasId()) {
            region.setId(pto.getId());
        }
        region.setName(pto.getName());
        region.setOrdinal(pto.getOrdinal());
        region.setLatitude(pto.getLatitude());
        region.setLongitude(pto.getLongitude());
        region.setLocationId(pto.getLocationId());
        region.setType(RegionType.of(pto.getType()));
        region.setStatus(RegionStatus.of(pto.getStatus()));
        if (Strings.isNotBlank(pto.getParentRegionId())) {
            Region parentRegion = new Region();
            parentRegion.setId(pto.getParentRegionId());
            region.setParentRegion(parentRegion);
        }

        return region;
    }

    @Override
    public List<Region> toEntityList(RegionListPTO listPTO) {
        if (Objects.isNull(listPTO) || CollectionUtils.isEmpty(listPTO.getRegionListList())) {
            return new ArrayList<>();
        }

        return listPTO.getRegionListList().stream().map(this::toEntity).toList();
    }

    @Override
    public RegionOptionalPTO toOptionalEntity(Optional<Region> entityOptional) {
        final RegionOptionalPTO.Builder builder = RegionOptionalPTO.newBuilder();
        entityOptional.ifPresent(e -> builder.setRegion(toPto(e)));
        return builder.build();
    }

    @Override
    public RegionPTO toPto(Region region) {
        RegionPTO.Builder builder = RegionPTO.newBuilder()
                .setId(region.getId())
                .setName(region.getName())
                .setOrdinal(region.getOrdinal())
                .setLatitude(region.getLatitude())
                .setLongitude(region.getLongitude())
                .setLocationId(region.getLocationId());
        Optional.ofNullable(region.getParentRegion()).map(Region::getId).ifPresent(builder::setParentRegionId);
        Optional.ofNullable(region.getType()).map(RegionType::of).ifPresent(builder::setType);
        Optional.ofNullable(region.getStatus()).map(RegionStatus::of).ifPresent(builder::setStatus);

        return builder
                .build();
    }

    @Override
    public RegionListPTO toPtoList(Iterable<Region> entityList) {
        return RegionListPTO.newBuilder().addAllRegionList(StreamSupport.stream(entityList.spliterator(), false).map(this::toPto).toList()).build();
    }
}
