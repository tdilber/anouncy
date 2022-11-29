package com.beyt.anouncy.listing.service.impl;

import com.beyt.anouncy.common.entity.redis.AnnounceVoteDTO;
import com.beyt.anouncy.common.repository.Neo4jCustomRepository;
import com.beyt.anouncy.common.service.VoteRedisService;
import com.beyt.anouncy.listing.service.base.IAnnounceListVoteFetch;
import lombok.RequiredArgsConstructor;
import org.apache.commons.collections4.CollectionUtils;
import org.redisson.api.RMap;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Future;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RedisAnnounceListVoteFetch implements IAnnounceListVoteFetch {
    protected final RedissonClient redissonClient;
    protected final Neo4jCustomRepository neo4jCustomRepository;

    @Override
    public Future<Map<String, AnnounceVoteDTO>> fetchAsync(Map<String, Set<String>> regionIdAnnounceIdSetMap) {
        List<CompletableFuture<Map<String, AnnounceVoteDTO>>> resultList = new ArrayList<>();

        regionIdAnnounceIdSetMap.forEach((regionId, announceIdList) -> {
            RMap<String, AnnounceVoteDTO> voteMap = redissonClient.getMap(VoteRedisService.ANNOUNCE_SINGLE_VOTE_MAP_PREFIX + regionId);
            CompletableFuture<Map<String, AnnounceVoteDTO>> voteMapAllAsync = voteMap.getAllAsync(announceIdList).toCompletableFuture();

            resultList.add(voteMapAllAsync);
        });


        CompletableFuture<Map<String, AnnounceVoteDTO>> joinedFeature = join(resultList)
                .thenApply(result -> result.stream().flatMap(r -> r.entrySet().stream()).collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (v1, v2) -> v1)))
                .thenApply(resultMap -> {
                    List<String> announceIdList = regionIdAnnounceIdSetMap.values().stream().flatMap(Collection::stream).toList();

                    List<String> missingAnnounceIdList = announceIdList.stream().filter(a -> !resultMap.containsKey(a)).toList();

                    if (CollectionUtils.isNotEmpty(missingAnnounceIdList)) {
//                        regionIdAnnounceIdSetMap.entrySet().stream().filter(e -> missingAnnounceIdList.stream().anyMatch(e.getValue()))
//                        neo4jCustomRepository.getVoteCount() TODO
                    }

                    return resultMap;
                });
        return joinedFeature;
    }

    private static <T> CompletableFuture<List<T>> join(List<CompletableFuture<T>> executionPromises) {
        CompletableFuture<Void> joinedPromise = CompletableFuture.allOf(executionPromises.toArray(CompletableFuture[]::new));
        return joinedPromise.thenApply(voit -> executionPromises.stream().map(CompletableFuture::join).collect(Collectors.toList()));
    }
}
