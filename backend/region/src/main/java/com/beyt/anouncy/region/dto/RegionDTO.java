package com.beyt.anouncy.region.dto;

import com.beyt.anouncy.common.entity.enumeration.LocationType;
import com.beyt.anouncy.common.v1.RegionPTO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.commons.collections4.CollectionUtils;

import java.io.Serializable;
import java.util.List;

@Data
@NoArgsConstructor
public class RegionDTO implements Serializable {
    private List<RegionItemDTO> regions = null;

    public RegionDTO(List<RegionPTO> regionList) {
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

        public RegionItemDTO(RegionPTO region) {
            this.regionId = region.getId();
            this.name = region.getName();
            this.type = LocationType.convert(region.getType());
            this.parentLocationId = region.getParentRegionId();
        }
    }
}
