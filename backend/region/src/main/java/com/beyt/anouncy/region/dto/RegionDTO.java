package com.beyt.anouncy.region.dto;

import com.beyt.anouncy.common.entity.enumeration.LocationType;
import com.beyt.anouncy.common.entity.neo4j.Region;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.commons.collections4.CollectionUtils;

import java.io.Serializable;
import java.util.List;
import java.util.Optional;

@Data
@NoArgsConstructor
public class RegionDTO implements Serializable {
    private List<RegionItemDTO> regions = null;

    public RegionDTO(List<Region> regionList) {
        if (CollectionUtils.isNotEmpty(regionList)) {
            this.regions = regionList.stream().map(RegionItemDTO::new).toList();
        }
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class RegionItemDTO {
        private String regionId;
        private String parentLocationId;
        private String name;
        private LocationType type;

        public RegionItemDTO(Region region) {
            this.regionId = region.getId();
            this.name = region.getName();
            this.type = region.getType().convert();
            this.parentLocationId = Optional.ofNullable(region.getParentRegion()).map(Region::getId).orElse(null);
        }
    }
}
