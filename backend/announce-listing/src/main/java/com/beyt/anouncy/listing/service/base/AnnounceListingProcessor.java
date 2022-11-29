package com.beyt.anouncy.listing.service.base;

import com.beyt.anouncy.common.entity.redis.AnnouncePageDTO;
import com.beyt.anouncy.common.entity.redis.AnnouncePageItemDTO;
import com.beyt.anouncy.common.entity.redis.AnnounceVoteDTO;
import com.beyt.anouncy.listing.dto.enumeration.AnnounceListingType;
import com.beyt.anouncy.listing.service.base.parameter.BaseAnnounceListProviderParam;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.redisson.api.RFuture;
import org.redisson.api.RLock;

import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Slf4j
public class AnnounceListingProcessor<T extends BaseAnnounceListProviderParam> {
    private final IAnnounceListContentProvider<T> contentProvider;
    private final IAnnounceListFetchDataLockProvider<T> fetchDataLockProvider;
    private final IAnnounceListVoteFetch voteFetcher;


    public AnnounceListingProcessor(IAnnounceListContentProvider<T> contentProvider, IAnnounceListFetchDataLockProvider<T> fetchDataLockProvider, IAnnounceListVoteFetch voteFetcher) {
        this.contentProvider = contentProvider;
        this.fetchDataLockProvider = fetchDataLockProvider;
        this.voteFetcher = voteFetcher;
    }


    public AnnouncePageDTO fetchList(T param) throws ExecutionException, InterruptedException {

        RLock lock = fetchDataLockProvider.getLock(param);

        IAnnounceListContentProvider.Result contentResult = contentProvider.provide(param);
        List<AnnouncePageItemDTO> existingAnnouncePageItems = contentResult.getExistingAnnounceItems();
        List<AnnouncePageItemDTO> needToVoteFetchAnnounceList = contentResult.getNeedToVoteFetchAnnounceList();

        Date voteTimeoutTime = getVoteTimeoutTime();
        List<AnnouncePageItemDTO> needUpdateVoteItemList = existingAnnouncePageItems.stream().filter(i -> i.getAnnounceCreateDate().compareTo(voteTimeoutTime) < 0 || Objects.isNull(i.getYes()) || Objects.isNull(i.getNo())).toList();

        List<Pair<String, String>> voteFetchList = needUpdateVoteItemList.stream().map(a -> Pair.of(a.getRegionId(), a.getAnnounceId())).collect(Collectors.toList());
        voteFetchList.addAll(needToVoteFetchAnnounceList.stream().map(a -> Pair.of(a.getRegionId(), a.getAnnounceId())).toList());

        boolean isUpdating = false;
        Future<Map<String, AnnounceVoteDTO>> voteMapAllAsync = null;
        if (CollectionUtils.isNotEmpty(voteFetchList)) {
            // This condition wait until lock and get fresh data and this data must be updated.
            if (lock.isLocked() && lock.tryLock(6, 10, TimeUnit.MILLISECONDS)) { // TODO research how to wait until release.
                lock.unlockAsync();
                var updatedContentResult = contentProvider.provide(param);
                existingAnnouncePageItems = updatedContentResult.getExistingAnnounceItems();
                needToVoteFetchAnnounceList.clear();
            } else {
                voteMapAllAsync = voteFetcher.fetchAsync(voteFetchList.stream().collect(Collectors.groupingBy(Pair::getLeft, Collectors.mapping(Pair::getRight, Collectors.toSet()))));
                isUpdating = true;
                lock.lock(5, TimeUnit.SECONDS);
            }
        }

        if (Objects.nonNull(voteMapAllAsync)) {
            Map<String, AnnounceVoteDTO> fetchResult = voteMapAllAsync.get();
            existingAnnouncePageItems.forEach(item -> {
                AnnounceVoteDTO vote = fetchResult.get(item.getAnnounceId());
                if (Objects.nonNull(vote)) {
                    item.update(vote);
                }
            });
        }

        AnnouncePageDTO result = new AnnouncePageDTO(existingAnnouncePageItems);
        if (isUpdating) {
            RFuture<Boolean> booleanRFuture = contentProvider.save(param, result);
            booleanRFuture.whenCompleteAsync((a, b) -> { // TODO check
                lock.unlock();
            });
        }
        return result;
    }

    protected Date getVoteTimeoutTime() {
        Calendar instance = Calendar.getInstance();
        instance.add(Calendar.MINUTE, -1); // TODO move to config
        return instance.getTime();
    }

    public static Map<AnnounceListingType, AnnounceListingProcessor<? extends BaseAnnounceListProviderParam>> generateAnnounceListingProcessorMap(Map<String, IAnnounceListContentProvider<? extends BaseAnnounceListProviderParam>> announceListContentProviderMap, Map<String, IAnnounceListFetchDataLockProvider<? extends BaseAnnounceListProviderParam>> announceListFetchDataLockMap, Map<String, IAnnounceListVoteFetch> announceListVoteFetchMap) {
        final Map<AnnounceListingType, AnnounceListingProcessor<?>> announceListingProcessorMap;
        announceListingProcessorMap = new HashMap<>();
        for (AnnounceListingType type : AnnounceListingType.values()) {
            generateSingleTypeBasedProcessor(announceListContentProviderMap, announceListFetchDataLockMap, announceListVoteFetchMap, announceListingProcessorMap, type);
        }
        return announceListingProcessorMap;
    }

    @SuppressWarnings("unchecked")
    private static <T extends BaseAnnounceListProviderParam> void generateSingleTypeBasedProcessor(Map<String, IAnnounceListContentProvider<? extends BaseAnnounceListProviderParam>> announceListContentProviderMap, Map<String, IAnnounceListFetchDataLockProvider<? extends BaseAnnounceListProviderParam>> announceListFetchDataLockMap, Map<String, IAnnounceListVoteFetch> announceListVoteFetchMap, Map<AnnounceListingType, AnnounceListingProcessor<?>> announceListingProcessorMap, AnnounceListingType type) {
        IAnnounceListContentProvider<T> contentProvider = (IAnnounceListContentProvider<T>) announceListContentProviderMap.get(type.getContentProviderBean());
        IAnnounceListFetchDataLockProvider<T> fetchDataLockProvider = (IAnnounceListFetchDataLockProvider<T>) announceListFetchDataLockMap.get(type.getFetchDataLockBean());
        IAnnounceListVoteFetch voteFetcher = announceListVoteFetchMap.get(type.getVoteFetchBean());

        announceListingProcessorMap.put(type, new AnnounceListingProcessor<>(contentProvider, fetchDataLockProvider, voteFetcher));
    }
}
