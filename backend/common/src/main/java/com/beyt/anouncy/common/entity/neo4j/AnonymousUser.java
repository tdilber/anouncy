package com.beyt.anouncy.common.entity.neo4j;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.neo4j.core.schema.Id;
import org.springframework.data.neo4j.core.schema.Node;

import java.io.Serializable;
import java.util.UUID;

@Node
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AnonymousUser implements Serializable {
    @Id
    @NotNull
    private UUID id;
}
