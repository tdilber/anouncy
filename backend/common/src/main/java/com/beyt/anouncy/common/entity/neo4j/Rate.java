package com.beyt.anouncy.common.entity.neo4j;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.neo4j.core.schema.*;
import org.springframework.data.neo4j.core.support.UUIDStringGenerator;

import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.UUID;

@Node
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Rate implements Serializable {
    @Id
    @GeneratedValue(UUIDStringGenerator.class)
    private String id;

    @NotNull
    @Property("anonymous_user_id")
    private UUID anonymousUserId;

    @NotNull
    @Property("value")
    private Boolean value;

    @Relationship(value = "HAS_ANNOUNCE", direction = Relationship.Direction.INCOMING)
    @JsonIgnoreProperties(value = {"announce"}, allowSetters = true)
    private Announce announce;
}
