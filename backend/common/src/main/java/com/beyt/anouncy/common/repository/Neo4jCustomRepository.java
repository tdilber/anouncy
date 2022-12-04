package com.beyt.anouncy.common.repository;

import com.beyt.anouncy.common.entity.neo4j.model.VoteCount;
import com.beyt.anouncy.common.entity.neo4j.model.VoteSummary;
import lombok.RequiredArgsConstructor;
import org.springframework.data.neo4j.core.Neo4jClient;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.Optional;
import java.util.UUID;

@Repository
@RequiredArgsConstructor
public class Neo4jCustomRepository {

    private final Neo4jClient neo4jClient;

    public Optional<VoteCount> getVoteCount(String regionId, String announceId) {
        return this.neo4jClient
                .query("""
                        MATCH (announce:Announce)
                        WHERE announce.id = $announceId
                        RETURN announce.id as announceId,
                        COUNT { MATCH (announce)-[:USER_VOTE]->(vote:Vote)<-[:VOTED_REGION]-(region:Region) WHERE vote.value = true AND region.id = $regionId } as yes,
                        COUNT { MATCH (announce)-[:USER_VOTE]->(vote:Vote)<-[:VOTED_REGION]-(region:Region) WHERE vote.value = false AND region.id = $regionId } as no,
                        announce.currentRegion.id as currentRegionId
                        """
                )
                .bind(announceId).to("announceId")
                .bind(regionId).to("regionId")
                .fetchAs(VoteCount.class)
                .mappedBy((typeSystem, record) -> new VoteCount(record.get("announceId").asString(),
                        record.get("yes").asLong(), record.get("no").asLong(), record.get("currentRegionId").asString()))
                .first();
    }

    public Collection<VoteCount> getAllVoteCounts(String regionId, Collection<String> announceIdList) {
        return this.neo4jClient
                .query("""
                        MATCH (announce:Announce)
                        WHERE announce.id IN ($announceIdList)
                        RETURN announce.id as announceId,
                        COUNT { MATCH (announce)-[:USER_VOTE]->(vote:Vote)<-[:VOTED_REGION]-(region:Region) WHERE vote.value = true AND region.id = $regionId } as yes,
                        COUNT { MATCH (announce)-[:USER_VOTE]->(vote:Vote)<-[:VOTED_REGION]-(region:Region) WHERE vote.value = false AND region.id = $regionId } as no,
                        announce.currentRegion.id as currentRegionId
                        """
                )
                .bind(announceIdList).to("announceIdList")
                .bind(regionId).to("regionId")
                .fetchAs(VoteCount.class)
                .mappedBy((typeSystem, record) -> new VoteCount(record.get("announceId").asString(),
                        record.get("yes").asLong(), record.get("no").asLong(), record.get("currentRegionId").asString()))
                .all();
    }

    public Collection<VoteSummary> getVoteSummaries(UUID anonymousUserId, String regionId, Collection<String> announceIdList) {
        return this.neo4jClient
                .query("""
                        MATCH (user:AnonymousUser)-[:OWN_USER]->(announce:Announce)-[:USER_VOTE]->(vote:Vote)<-[:VOTED_REGION]-(region:Region)
                        WHERE announce.id IN ($announceIdList) AND user.id=$anonymousUserId AND region.id=$regionId
                        RETURN vote.id as id, vote.value as value, announce.id as announceId
                        """
                )
                .bind(announceIdList).to("announceIdList")
                .bind(anonymousUserId.toString()).to("anonymousUserId")
                .bind(regionId).to("regionId")
                .fetchAs(VoteSummary.class)
                .mappedBy((typeSystem, record) -> new VoteSummary(record.get("id").asString(),
                        record.get("value").asBoolean(), record.get("announceId").asString()))
                .all();
    }
}
