package com.beyt.anouncy.vote.service;

import com.beyt.anouncy.common.entity.redis.AnnouncePageItemDTO;
import com.beyt.anouncy.common.entity.redis.AnnounceVoteDTO;
import com.beyt.anouncy.common.exception.ClientErrorException;
import com.beyt.anouncy.common.persist.v1.VotePersistServiceGrpc;
import com.beyt.anouncy.common.service.VoteRedisService;
import com.beyt.anouncy.common.v1.VoteCountPTO;
import com.beyt.anouncy.common.v1.VoteCountSingleRequest;
import com.beyt.anouncy.vote.dto.VoteCreateDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.devh.boot.grpc.client.inject.GrpcClient;
import org.apache.commons.lang3.BooleanUtils;
import org.redisson.api.RList;
import org.redisson.api.RMap;
import org.redisson.api.RScoredSortedSet;
import org.redisson.api.RedissonClient;
import org.redisson.client.protocol.ScoredEntry;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.List;
import java.util.Objects;

@Slf4j
@Service
@RequiredArgsConstructor
public class VoteService {
    private final RedissonClient redissonClient;

    @GrpcClient("persist-grpc-server")
    private VotePersistServiceGrpc.VotePersistServiceBlockingStub votePersistServiceBlockingStub;

    public AnnouncePageItemDTO receiveVote(String announceId, VoteCreateDTO dto) {
        return null;
    }

    public void deleteVote(String announceId) {

    }

    public void saveVoteYesOrNo(String announceId, String regionId, Boolean yesOrNo) {
        RMap<String, AnnounceVoteDTO> voteMap = redissonClient.getMap(VoteRedisService.ANNOUNCE_SINGLE_VOTE_MAP_PREFIX + regionId);
        RScoredSortedSet<String> topRatedSortedSet = getTopRatedScoredSortedSet(regionId);
        RScoredSortedSet<String> trendingSortedSet = getTrendingScoredSortedSet(regionId);
        Integer newOrderTopRated = topRatedSortedSet.addScoreAndGetRank(announceId, BooleanUtils.isTrue(yesOrNo) ? 1 : -1);
        Integer newOrderTrending = trendingSortedSet.addScoreAndGetRank(announceId, BooleanUtils.isTrue(yesOrNo) ? 1 : -1);
        AnnounceVoteDTO announceVote = voteMap.get(announceId);
        if (Objects.isNull(announceVote)) {
            announceVote = fetchAnnounceVotes(announceId, regionId);
        }
        announceVote.receiveVote(regionId, yesOrNo);
        announceVote.setRegionOrder(newOrderTopRated);

        voteMap.fastPut(announceId, announceVote);
    }

    protected AnnounceVoteDTO fetchAnnounceVotes(String announceId, String regionId) {
        VoteCountPTO count = votePersistServiceBlockingStub.getVoteCount(VoteCountSingleRequest.newBuilder().setRegionId(regionId).setAnnounceId(announceId).build());
        AnnounceVoteDTO voteSingleCache = new AnnounceVoteDTO();
        if (Objects.isNull(count)) {
            throw new ClientErrorException("announce.not.found");
        }

        if (count.getCurrentRegionId().equals(regionId)) {
            throw new ClientErrorException("vote.region.not.suitable");
        }

        voteSingleCache.setYes(count.getYes());
        voteSingleCache.setNo(count.getNo());
        voteSingleCache.setCurrentRegionId(count.getCurrentRegionId());
        return voteSingleCache;
    }


    public List<String> getPaginatedAnnounceIdList(String regionId, Integer pageCount) {
        RScoredSortedSet<String> sortedSet = getTopRatedScoredSortedSet(regionId);

        Collection<ScoredEntry<String>> scoredEntries = sortedSet.entryRange(pageCount * VoteRedisService.PAGE_SIZE, (pageCount + 1) * VoteRedisService.PAGE_SIZE);

        return scoredEntries.stream().map(ScoredEntry::getValue).toList();
    }

    private RScoredSortedSet<String> getTopRatedScoredSortedSet(String regionId) {
        return redissonClient.getScoredSortedSet(VoteRedisService.ANNOUNCE_REGION_TOP_RATED_SORTED_SET_KEY + regionId);
    }

    private RScoredSortedSet<String> getTrendingScoredSortedSet(String regionId) {
        return redissonClient.getScoredSortedSet(VoteRedisService.ANNOUNCE_REGION_TRENDING_SORTED_SET_KEY + regionId);
    }

    private RList<String> getNewestList(String regionId) {
        return redissonClient.getList(VoteRedisService.ANNOUNCE_REGION_NEWEST_LIST_KEY + regionId);
    }
}
