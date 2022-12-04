package com.beyt.anouncy.common.service;

import com.beyt.anouncy.common.entity.redis.AnnounceVoteDTO;
import com.beyt.anouncy.common.repository.Neo4jCustomRepository;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.redisson.api.RMap;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Future;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class VoteService {
    protected final RedissonClient redissonClient;
    protected final Neo4jCustomRepository neo4jCustomRepository;

    public Future<Map<String, AnnounceVoteDTO>> fetchAsync(Map<String, Set<String>> regionIdAnnounceIdSetMap) {
        List<CompletableFuture<Map<String, AnnounceVoteDTO>>> resultList = new ArrayList<>();
        Map<String, RMap<String, AnnounceVoteDTO>> regionIdVoteRedisMapMap = new HashMap<>();

        regionIdAnnounceIdSetMap.forEach((regionId, announceIdList) -> {
            RMap<String, AnnounceVoteDTO> voteMap = redissonClient.getMap(VoteRedisService.ANNOUNCE_SINGLE_VOTE_MAP_PREFIX + regionId);
            CompletableFuture<Map<String, AnnounceVoteDTO>> voteMapAllAsync = voteMap.getAllAsync(announceIdList).toCompletableFuture();

            regionIdVoteRedisMapMap.put(regionId, voteMap);
            resultList.add(voteMapAllAsync);
        });


        return join(resultList)
                .thenApply(result -> result.stream().flatMap(r -> r.entrySet().stream()).collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (v1, v2) -> v1)))
                .thenApply(resultMap -> {
                    List<String> announceIdList = regionIdAnnounceIdSetMap.values().stream().flatMap(Collection::stream).toList();

                    List<String> missingAnnounceIdList = announceIdList.stream().filter(a -> !resultMap.containsKey(a)).toList();

                    if (CollectionUtils.isNotEmpty(missingAnnounceIdList)) {
                        List<Pair<String, String>> regionIdAnnounceIdPairList = regionIdAnnounceIdSetMap.entrySet().stream().filter(e -> CollectionUtils.containsAny(e.getValue(), missingAnnounceIdList))
                                .flatMap(e -> e.getValue().stream().filter(missingAnnounceIdList::contains).map(v -> Pair.of(e.getKey(), v)))
                                .toList();

                        regionIdAnnounceIdPairList.forEach(p -> // TODO change foreach To Neo4j Group By multi fetch
                                neo4jCustomRepository.getVoteCount(p.getRight(), p.getLeft())
                                        .ifPresent(v -> {
                                            AnnounceVoteDTO vote = AnnounceVoteDTO.of(v);
                                            resultMap.put(p.getRight(), vote);
                                            regionIdVoteRedisMapMap.get(p.getRight()).fastPutAsync(p.getLeft(), vote);
                                        }));
                    }

                    return resultMap;
                });
    }

    @SneakyThrows
    public Map<String, AnnounceVoteDTO> fetch(Map<String, Set<String>> regionIdAnnounceIdSetMap) {
        return fetchAsync(regionIdAnnounceIdSetMap).get();
    }

    public Optional<AnnounceVoteDTO> fetchOne(String regionId, String announceId) {
        RMap<String, AnnounceVoteDTO> voteMap = redissonClient.getMap(VoteRedisService.ANNOUNCE_SINGLE_VOTE_MAP_PREFIX + regionId);

        AnnounceVoteDTO voteDTO = voteMap.get(announceId);

        if (Objects.nonNull(voteDTO)) {
            return Optional.of(voteDTO);
        }

        return neo4jCustomRepository.getVoteCount(regionId, announceId).map(v -> {
            AnnounceVoteDTO dto = AnnounceVoteDTO.of(v);
            voteMap.fastPutAsync(announceId, dto);
            return dto;
        });
    }

    private static <T> CompletableFuture<List<T>> join(List<CompletableFuture<T>> executionPromises) {
        CompletableFuture<Void> joinedPromise = CompletableFuture.allOf(executionPromises.toArray(CompletableFuture[]::new));
        return joinedPromise.thenApply(voit -> executionPromises.stream().map(CompletableFuture::join).collect(Collectors.toList()));
    }
}
