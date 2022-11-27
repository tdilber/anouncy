package com.beyt.anouncy.common.entity.neo4j.model;

public record VoteCount(String announceId, Long yes, Long no, String currentRegionId) {

}
