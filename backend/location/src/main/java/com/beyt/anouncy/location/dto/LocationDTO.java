package com.beyt.anouncy.location.dto;

import com.beyt.anouncy.location.entity.Location;
import com.beyt.anouncy.location.enumeration.LocationType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.commons.collections4.CollectionUtils;

import java.util.List;

@Data
@NoArgsConstructor
public class LocationDTO {
    private List<LocationItemDTO> locations = null;

    public LocationDTO(List<Location> locationList) {
        if (CollectionUtils.isNotEmpty(locationList)) {
            this.locations = locationList.stream().map(LocationItemDTO::new).toList();
        }
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class LocationItemDTO {
        private Long locationId;
        private Long parentLocationId;
        private String name;
        private LocationType type;

        public LocationItemDTO(Location location) {
            this.locationId = location.getId();
            this.name = location.getName();
            this.type = location.getType();
            this.parentLocationId = location.getParentLocationId();
        }
    }
}
