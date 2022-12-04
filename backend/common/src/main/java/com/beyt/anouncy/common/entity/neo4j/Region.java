package com.beyt.anouncy.common.entity.neo4j;

import com.beyt.anouncy.common.entity.neo4j.enumeration.RegionStatus;
import com.beyt.anouncy.common.entity.neo4j.enumeration.RegionType;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.neo4j.core.schema.*;
import org.springframework.data.neo4j.core.support.UUIDStringGenerator;

import java.io.Serializable;

@Node
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Region implements Serializable {
    @Id
    @GeneratedValue(UUIDStringGenerator.class)
    private String id;

    @NotNull
    @Property("name")
    private String name;

    @Property("ordinal")
    private Integer ordinal;

    @NotNull
    @Property("latitude")
    private Double latitude;

    @NotNull
    @Property("longitude")
    private Double longitude;

    @NotNull
    @Property("location_id")
    private Long locationId;

    @NotNull
    @Property("type")
    private RegionType type;

    @NotNull
    @Property("status")
    private RegionStatus status;

    @Relationship(value = "PARENT_REGION", direction = Relationship.Direction.INCOMING)
    @JsonIgnoreProperties(value = {"parentRegion"}, allowSetters = true)
    private Region parentRegion;
}
