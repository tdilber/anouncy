package com.beyt.anouncy.persist.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.neo4j.core.schema.GeneratedValue;
import org.springframework.data.neo4j.core.schema.Id;
import org.springframework.data.neo4j.core.schema.Node;
import org.springframework.data.neo4j.core.support.UUIDStringGenerator;

import java.io.Serializable;

@Node
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AnonymousUser implements Serializable {
    @Id
    @GeneratedValue(UUIDStringGenerator.class)
    private String id;
}
