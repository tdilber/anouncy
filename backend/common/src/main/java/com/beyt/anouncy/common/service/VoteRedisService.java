package com.beyt.anouncy.common.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RScoredSortedSet;
import org.redisson.api.RedissonClient;
import org.redisson.client.protocol.ScoredEntry;

import java.util.Collection;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
public abstract class VoteRedisService {
    protected final RedissonClient redissonClient;

    public static final Integer PAGE_SIZE = 10;
    public static final String ANNOUNCE_REGION_TOP_RATED_SORTED_SET_KEY = "ANNOUNCE_REGION_TOP_RATED_SORTED_SET_KEY_";
    public static final String ANNOUNCE_REGION_TRENDING_SORTED_SET_KEY = "ANNOUNCE_REGION_TRENDING_SORTED_SET_KEY_";
    public static final String ANNOUNCE_REGION_NEWEST_LIST_KEY = "ANNOUNCE_REGION_NEWEST_LIST_KEY_";
    public static final String ANNOUNCE_LOCK_KEY = "ANNOUNCE_LOCK_KEY_";
    public static final String ANNOUNCE_SINGLE_VOTE_MAP_PREFIX = "ANNOUNCE_SINGLE_VOTE_MAP_";
    public static final String ANNOUNCE_PAGE_CACHE_MAP = "ANNOUNCE_PAGE_CACHE_MAP";
    public static final String ANNOUNCE_SEARCH_PAGE_CACHE_MAP = "ANNOUNCE_SEARCH_PAGE_CACHE_MAP";

    public List<String> getPaginatedAnnounceIdList(String regionId, Integer pageCount) {
        RScoredSortedSet<String> sortedSet = getScoredSortedSet(regionId);

        Collection<ScoredEntry<String>> scoredEntries = sortedSet.entryRange(pageCount * PAGE_SIZE, (pageCount + 1) * PAGE_SIZE);

        return scoredEntries.stream().map(ScoredEntry::getValue).toList();
    }

    private RScoredSortedSet<String> getScoredSortedSet(String regionId) {
        return redissonClient.getScoredSortedSet(ANNOUNCE_REGION_TOP_RATED_SORTED_SET_KEY + regionId);
    }
}
