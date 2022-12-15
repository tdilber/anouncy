package com.beyt.anouncy.persist.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.neo4j.core.schema.*;
import org.springframework.data.neo4j.core.support.UUIDStringGenerator;

import java.io.Serializable;
import java.util.Date;

@Node
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Vote implements Serializable {
    @Id
    @GeneratedValue(UUIDStringGenerator.class)
    private String id;

    @NotNull
    @Property("value")
    private Boolean value;

    @Relationship(value = "USER_VOTE", direction = Relationship.Direction.INCOMING)
    @JsonIgnoreProperties(value = {"announce"}, allowSetters = true)
    private Announce announce;

    @Relationship(value = "OWN_USER", direction = Relationship.Direction.INCOMING)
    @JsonIgnoreProperties(value = {"user"}, allowSetters = true)
    private AnonymousUser anonymousUser;

    @Relationship(value = "VOTED_REGION", direction = Relationship.Direction.INCOMING)
    private Region region;

    @NotNull
    @Property("create_date")
    private Date createDate;
}
