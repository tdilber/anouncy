package com.beyt.anouncy.location.entity;

import com.beyt.anouncy.common.entity.enumeration.LocationStatus;
import com.beyt.anouncy.common.entity.enumeration.LocationType;
import com.beyt.anouncy.location.model.GeoJsonItem;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.apache.logging.log4j.util.Strings;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.io.Serializable;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * Created by tdilber at 25-Aug-19
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
//@ToString(callSuper = true)
@Document(collection = "locations")
public class Location implements Serializable {
    @Id
    @Field("id")
    private Long id;

    @NotNull
    @Field("name")
    private String name;

    @Field("ordinal")
    private Integer ordinal;

    @Field("user_usage")
    private Integer userUsage;

    @NotNull
    @Field("latitude")
    private Double latitude;

    @NotNull
    @Field("longitude")
    private Double longitude;

    @NotNull
    @Field("type")
    private LocationType type;

    @NotNull
    @Field("status")
    private LocationStatus status;

    @Field("parent_location_id")
    private Long parentLocationId;

    @Field("google_map_api_info")
    private Object googleMapApiInfo;

    @Field("path")
    private String path;

    @Field("boundary")
    private GeoJsonItem boundary;

    public List<Long> getParentLocationIdList() {
        if (Strings.isBlank(path)) {
            return Collections.EMPTY_LIST;
        }

        return Arrays.stream(path.split("/|")).filter(Strings::isNotBlank).map(Long::parseLong).toList();
    }
}
