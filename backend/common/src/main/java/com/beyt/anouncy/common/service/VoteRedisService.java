package com.beyt.anouncy.common.service;

import com.beyt.anouncy.common.entity.neo4j.model.VoteCount;
import com.beyt.anouncy.common.entity.redis.AnnounceVoteDTO;
import com.beyt.anouncy.common.exception.ClientErrorException;
import com.beyt.anouncy.common.repository.AnnounceRepository;
import com.beyt.anouncy.common.repository.Neo4jCustomRepository;
import com.beyt.anouncy.common.repository.VoteRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.BooleanUtils;
import org.redisson.api.RMap;
import org.redisson.api.RScoredSortedSet;
import org.redisson.api.RedissonClient;
import org.redisson.client.protocol.ScoredEntry;

import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Slf4j
@RequiredArgsConstructor
public abstract class VoteRedisService {
    protected final RedissonClient redissonClient;
    protected final AnnounceRepository announceRepository;
    protected final VoteRepository voteRepository;
    protected final Neo4jCustomRepository neo4jCustomRepository;

    public static final Integer PAGE_SIZE = 10;
    public static final String ANNOUNCE_REGION_SORTED_SET_KEY = "ANNOUNCE_REGION_SS_KEY_";
    public static final String ANNOUNCE_LOCK_KEY = "ANNOUNCE_LOCK_KEY_";
    public static final String ANNOUNCE_SINGLE_VOTE_MAP_PREFIX = "ANNOUNCE_SINGLE_VOTE_MAP_";
    public static final String ANNOUNCE_PAGE_CACHE_MAP = "ANNOUNCE_PAGE_CACHE_MAP";

    public void saveVoteYesOrNo(String announceId, String regionId, Boolean yesOrNo) {
        RMap<String, AnnounceVoteDTO> voteMap = redissonClient.getMap(ANNOUNCE_SINGLE_VOTE_MAP_PREFIX + regionId);
        RScoredSortedSet<String> sortedSet = getScoredSortedSet(regionId);
        Integer newOrder = sortedSet.addScoreAndGetRank(announceId, BooleanUtils.isTrue(yesOrNo) ? 1 : -1);
        AnnounceVoteDTO announceVote = voteMap.get(announceId);
        if (Objects.isNull(announceVote)) {
            announceVote = fetchAnnounceVotes(announceId, regionId);
        }
        announceVote.receiveVote(regionId, yesOrNo);
        announceVote.setRegionOrder(newOrder);

        voteMap.fastPut(announceId, announceVote);
    }

    protected AnnounceVoteDTO fetchAnnounceVotes(String announceId, String regionId) {
        Optional<VoteCount> voteCount = neo4jCustomRepository.getVoteCount(announceId, regionId);
        AnnounceVoteDTO voteSingleCache = new AnnounceVoteDTO();
        if (voteCount.isEmpty()) {
            throw new ClientErrorException("announce.not.found");
        }

        VoteCount count = voteCount.get();

        if (count.currentRegionId().equals(regionId)) {
            throw new ClientErrorException("vote.region.not.suitable");
        }

        voteSingleCache.setYes(count.yes());
        voteSingleCache.setNo(count.no());
        voteSingleCache.setCurrentRegionId(count.currentRegionId());
        return voteSingleCache;
    }


    public List<String> getPaginatedAnnounceIdList(String regionId, Integer pageCount) {
        RScoredSortedSet<String> sortedSet = getScoredSortedSet(regionId);

        Collection<ScoredEntry<String>> scoredEntries = sortedSet.entryRange(pageCount * PAGE_SIZE, (pageCount + 1) * PAGE_SIZE);

        return scoredEntries.stream().map(ScoredEntry::getValue).toList();
    }

    private RScoredSortedSet<String> getScoredSortedSet(String regionId) {
        return redissonClient.getScoredSortedSet(ANNOUNCE_REGION_SORTED_SET_KEY + regionId);
    }
}
