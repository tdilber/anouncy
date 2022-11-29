package com.beyt.anouncy.common.service;

import com.beyt.anouncy.common.entity.neo4j.Announce;
import com.beyt.anouncy.common.entity.neo4j.model.VoteCount;
import com.beyt.anouncy.common.entity.redis.AnnouncePageDTO;
import com.beyt.anouncy.common.entity.redis.AnnouncePageItemDTO;
import com.beyt.anouncy.common.entity.redis.AnnounceVoteDTO;
import com.beyt.anouncy.common.exception.ClientErrorException;
import com.beyt.anouncy.common.repository.AnnounceRepository;
import com.beyt.anouncy.common.repository.Neo4jCustomRepository;
import com.beyt.anouncy.common.repository.VoteRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.BooleanUtils;
import org.redisson.api.*;
import org.redisson.client.protocol.ScoredEntry;
import org.springframework.retry.annotation.Retryable;

import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

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


    @Retryable(maxAttempts = 2, value = Exception.class)
    public AnnouncePageDTO fetchList(String regionId, Integer page) throws ExecutionException, InterruptedException {
        RLock lock = redissonClient.getLock(regionId + "_" + page);
        boolean locked = lock.isLocked();

        List<String> announceIdList = getPaginatedAnnounceIdList(regionId, page);
        RMap<String, AnnouncePageDTO> pageMap = redissonClient.getMap(ANNOUNCE_PAGE_CACHE_MAP);

        AnnouncePageDTO pageCache = pageMap.get(regionId + "_" + page);
        boolean isUpdated = false;
        List<AnnouncePageItemDTO> existingAnnouncePageItems = new ArrayList<>();
        List<String> missingAnnounceIdList = new ArrayList<>();

        if (Objects.nonNull(pageCache)) {
            announceIdList.forEach(id -> {
                var itemOpt = pageCache.getItemList().stream().filter(i -> i.getAnnounceId().equals(id)).findFirst();
                if (itemOpt.isPresent()) {
                    existingAnnouncePageItems.add(itemOpt.get());
                } else {
                    missingAnnounceIdList.add(id);
                }
            });
        } else {
            missingAnnounceIdList.addAll(announceIdList);
        }

        Date voteTimeoutTime = getVoteTimeoutTime();
        List<AnnouncePageItemDTO> needUpdateVoteItemList = existingAnnouncePageItems.stream().filter(i -> i.getAnnounceCreateDate().compareTo(voteTimeoutTime) < 0 || Objects.isNull(i.getYes()) || Objects.isNull(i.getNo())).toList();

        Set<String> voteFetchList = needUpdateVoteItemList.stream().map(AnnouncePageItemDTO::getAnnounceId).collect(Collectors.toSet());
        voteFetchList.addAll(missingAnnounceIdList);

        RMap<String, AnnounceVoteDTO> voteMap = redissonClient.getMap(ANNOUNCE_SINGLE_VOTE_MAP_PREFIX + regionId);
        RFuture<Map<String, AnnounceVoteDTO>> voteMapAllAsync = null;
        if (CollectionUtils.isNotEmpty(voteFetchList)) {
            voteMapAllAsync = voteMap.getAllAsync(voteFetchList);
            isUpdated = true;
        }


        if (CollectionUtils.isNotEmpty(missingAnnounceIdList)) {
            List<Announce> announceList = announceRepository.findAllById(missingAnnounceIdList);
            existingAnnouncePageItems.addAll(announceList.stream().map(AnnouncePageItemDTO::new).toList());
        }

        if (Objects.nonNull(voteMapAllAsync)) {
            Map<String, AnnounceVoteDTO> fetchResult = voteMapAllAsync.get();
            if (fetchResult.size() < voteFetchList.size()) {
                // TODO voteMaps not found any vote then go and count from neo4j
            }
            existingAnnouncePageItems.forEach(item -> {
                AnnounceVoteDTO vote = fetchResult.get(item.getAnnounceId());
                if (Objects.nonNull(vote)) {
                    item.update(vote);
                }
            });
        }

        AnnouncePageDTO result = new AnnouncePageDTO(existingAnnouncePageItems);
        if (isUpdated) {
            RFuture<Boolean> booleanRFuture = pageMap.fastPutAsync(regionId + "_" + page, result);
            booleanRFuture.whenCompleteAsync((a, b) -> {
                lock.unlock();
            });
        }
        return result;
    }


    public AnnouncePageDTO populateUserVotes(UUID anonymousUserId, AnnouncePageDTO page) {
        List<String> announceIdList = page.getItemList().stream().map(AnnouncePageItemDTO::getAnnounceId).toList();

        Collection<VoteRepository.VoteSummary> voteList = voteRepository.findByAnonymousUserIdAndAnnounceIdIsIn(anonymousUserId, announceIdList);
        page.getItemList().forEach(i ->
                voteList.stream().filter(v -> v.getAnnounceId().equals(i.getAnnounceId())).findFirst()
                        .ifPresent(v -> i.setCurrentVote(v.getValue()))
        );
        return page;
    }

    private Date getVoteTimeoutTime() {
        Calendar instance = Calendar.getInstance();
        instance.add(Calendar.MINUTE, -1);
        return instance.getTime();
    }

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
