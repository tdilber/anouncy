package com.beyt.anouncy.persist.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.neo4j.core.schema.*;
import org.springframework.data.neo4j.core.support.UUIDStringGenerator;

import java.io.Serializable;
import java.util.Collection;
import java.util.Date;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Node
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Announce implements Serializable {
    @Id
    @GeneratedValue(UUIDStringGenerator.class)
    private String id;

    @NotNull
    @Property("body")
    private String body;

    @Relationship(value = "OWN_USER", direction = Relationship.Direction.INCOMING)
    @JsonIgnoreProperties(value = {"user"}, allowSetters = true)
    private AnonymousUser anonymousUser;

    @Relationship(value = "HAS_BEGIN_REGION", direction = Relationship.Direction.INCOMING)
    private Region beginRegion;

    @Relationship(value = "HAS_CURRENT_REGION", direction = Relationship.Direction.INCOMING)
    private Region currentRegion;

    @NotNull
    @Property("create_date")
    private Date createDate;

    public static Map<String, Set<String>> getRegionAnnounceIdSetMap(Collection<Announce> announceList) {
        return announceList.stream().collect(Collectors.groupingBy(a -> a.getCurrentRegion().getId(), Collectors.mapping(Announce::getId, Collectors.toSet())));
    }
}
