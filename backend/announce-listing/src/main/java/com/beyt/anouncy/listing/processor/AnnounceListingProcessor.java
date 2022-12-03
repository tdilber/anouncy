package com.beyt.anouncy.listing.processor;

import com.beyt.anouncy.common.entity.redis.AnnouncePageDTO;
import com.beyt.anouncy.common.entity.redis.AnnouncePageItemDTO;
import com.beyt.anouncy.common.entity.redis.AnnounceVoteDTO;
import com.beyt.anouncy.listing.dto.enumeration.AnnounceListingType;
import com.beyt.anouncy.listing.service.base.IAnnounceListContentProvider;
import com.beyt.anouncy.listing.service.base.IAnnounceListFetchDataLockProvider;
import com.beyt.anouncy.listing.service.base.IAnnounceListVoteFetch;
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

/**
 * Most Important Object Of Listing Logic. Because This class contains listing algorithm.
 * The other classes have mostly data fetch, save and helper functions.
 * <p>
 * When You Read The fetchList method you can understand how is going on.
 * <p>
 * Redis Cache Structures:
 * 1. Vote Single Cache: Contains only 1 announce vote summary. (yes, no, current region) [HAVE TTL or IDLE TIME]
 * 2. Listing Page Cache: Contains full return endpoint data. (without current user vote status) [HAVE TTL or IDLE TIME]
 * 3. Top Listing Scored Sorted Map (Sorted By Most Voted) (Score, AnnounceId) [ALWAYS EXIST, ALWAYS UPDATED]
 * 4. Trending Listing Scored Sorted Map (Sorted By Last 1 Hour Voted) (Score, AnnounceId) [ALWAYS EXIST, ALWAYS UPDATED]
 * 5. Newest Listing Id List (Sorted By Newest to Oldest) ([sorted] AnnounceId) [ALWAYS EXIST, ALWAYS UPDATED]
 * <p>
 * Performance Analysis:
 * 1. When every cache are empty then  all data fetched from Neo4J and fill caches. (without lists and sorted sets)
 * 2. (For Region Lists) When any order change received then immediately new order applying and just missing contents, votes fetching. System have immunity for order changing in the same page (if all items same but order changed then new order apply without any Redis update operation).
 * 3. Votes Timeout 1 minute. Every minute fetch only votes from Redis Vote Map with multiKey fetch method. (just one query.)
 * 4. If many request received from same page in same minute then only 2 Redis Get Query fetching end serving for every request. (First fetch order, second current page cache. [ensure the order not changing and votes not in timeout])
 * 5. If we need little update when coming too many request. Then one request lock and update, the others wait before lock. After Unlock waiters receive the fresh update and return immediately.
 * <p>
 * <p>
 * As A Result:
 * 1. (For Region Lists) List Orders always up-to-date immediately.
 * 2. Announce Bodies fetch only missing part of the list.
 * 3. Votes always updated to 1 minute.
 * 4. (In best case) Every request only fetch 2 redis query.
 *
 * @param <T>
 */
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


    /**
     * Most Important method for listing. The Algorithm contains inside.
     *
     * @param param Generic Parameters For Listing. The Parameter may be Region Param or Search Param.
     *              The Region param contains Region info and Search param contains Search text.
     * @return Endpoint DTO ready data. (But not include member likes.)
     * @throws ExecutionException
     * @throws InterruptedException
     */
    public AnnouncePageDTO fetchList(T param) throws ExecutionException, InterruptedException {

        // The Lock only use for updating.
        RLock lock = fetchDataLockProvider.getLock(param);

        // Content Provider provide Announce Body Data (if exist they fetch from cache.)
        IAnnounceListContentProvider.Result contentResult = contentProvider.provide(param);
        // Content Provider return page items all bodies.
        List<AnnouncePageItemDTO> announcePageItems = contentResult.getAnnouncePageItems();
        // Content Provider return new fetched announce what does not have vote infos.
        List<AnnouncePageItemDTO> needToVoteFetchAnnounceList = contentResult.getNeedToVoteFetchAnnounceList();

        Date voteTimeoutTime = getVoteTimeoutTime();
        // Check the cached page items for need to update vote infos.
        List<AnnouncePageItemDTO> needUpdateVoteItemList = announcePageItems.stream().filter(i -> i.getAnnounceCreateDate().compareTo(voteTimeoutTime) < 0 || Objects.isNull(i.getYes()) || Objects.isNull(i.getNo())).toList();

        List<Pair<String, String>> voteFetchList = needUpdateVoteItemList.stream().map(a -> Pair.of(a.getRegionId(), a.getAnnounceId())).collect(Collectors.toList());
        // Merge new Fetched Announces and Vote Timeout Announces.
        voteFetchList.addAll(needToVoteFetchAnnounceList.stream().map(a -> Pair.of(a.getRegionId(), a.getAnnounceId())).toList());

        boolean isUpdating = false;
        Future<Map<String, AnnounceVoteDTO>> voteMapAllAsync = null;
        // If need Update starting.
        if (CollectionUtils.isNotEmpty(voteFetchList)) {
            // This condition wait until lock and get fresh data and this data must be new updated.
            if (lock.isLocked() && lock.tryLock(6, 10, TimeUnit.MILLISECONDS)) { // TODO research how to wait until release.
                lock.unlockAsync();
                // Fetch fresh data and return
                var updatedContentResult = contentProvider.provide(param);
                announcePageItems = updatedContentResult.getAnnouncePageItems();
                return new AnnouncePageDTO(announcePageItems);
            } else { // Need Update and Lock Start
                voteMapAllAsync = voteFetcher.fetchAsync(voteFetchList.stream().collect(Collectors.groupingBy(Pair::getLeft, Collectors.mapping(Pair::getRight, Collectors.toSet()))));
                isUpdating = true;
                lock.lock(5, TimeUnit.SECONDS);
            }
        }

        if (Objects.nonNull(voteMapAllAsync)) {
            // Map using because Announce Votes Fetched Base On Current Region.
            // Then we group the announces based on current regions.
            Map<String, AnnounceVoteDTO> fetchResult = voteMapAllAsync.get();
            // vote fetch complete and updates applying.
            announcePageItems.forEach(item -> {
                AnnounceVoteDTO vote = fetchResult.get(item.getAnnounceId());
                if (Objects.nonNull(vote)) {
                    item.update(vote);
                }
            });
        }

        // NOW we are ready to Return. We are saving and lock release with async.
        AnnouncePageDTO result = new AnnouncePageDTO(announcePageItems);
        if (isUpdating) {
            // Save All Page asyncly.
            // Note: When any fetch receive for the same page in 1 min we are just return the same cache.
            RFuture<Boolean> booleanRFuture = contentProvider.save(param, result);
            // After save operation (both fail or success) lock released. (Note: also the lock have automatic timeout release.)
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

    /**
     * Bean Maps Converted To Processor Map. For Fast access to right processor.
     *
     * @param announceListContentProviderMap
     * @param announceListFetchDataLockMap
     * @param announceListVoteFetchMap
     * @return
     */
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
