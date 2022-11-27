package com.beyt.anouncy.common.entity.neo4j;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.neo4j.core.schema.*;
import org.springframework.data.neo4j.core.support.UUIDStringGenerator;

import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.Date;

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
}
