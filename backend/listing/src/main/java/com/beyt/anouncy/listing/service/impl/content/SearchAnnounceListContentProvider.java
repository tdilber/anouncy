package com.beyt.anouncy.listing.service.impl.content;

import com.beyt.anouncy.common.entity.elasticsearch.AnnounceSearchItem;
import com.beyt.anouncy.common.entity.redis.AnnouncePageDTO;
import com.beyt.anouncy.common.entity.redis.AnnouncePageItemDTO;
import com.beyt.anouncy.common.repository.elasticsearch.AnnounceSearchRepository;
import com.beyt.anouncy.common.service.VoteRedisService;
import com.beyt.anouncy.listing.service.base.IAnnounceListContentProvider;
import com.beyt.anouncy.listing.service.base.parameter.SearchAnnounceListProviderParam;
import lombok.RequiredArgsConstructor;
import org.redisson.api.RFuture;
import org.redisson.api.RMapCache;
import org.redisson.api.RedissonClient;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class SearchAnnounceListContentProvider implements IAnnounceListContentProvider<SearchAnnounceListProviderParam> {
    protected final RedissonClient redissonClient;
    protected final AnnounceSearchRepository announceSearchRepository;

    @Override
    public Result provide(SearchAnnounceListProviderParam param) {
        RMapCache<String, AnnouncePageDTO> pageMap = redissonClient.getMapCache(VoteRedisService.ANNOUNCE_SEARCH_PAGE_CACHE_MAP);

        AnnouncePageDTO pageCache = pageMap.get(param.generateKey());
        List<AnnouncePageItemDTO> existingAnnouncePageItems;
        List<AnnouncePageItemDTO> needToVoteFetchAnnounceList = new ArrayList<>();

        if (Objects.isNull(pageCache)) {
            Page<AnnounceSearchItem> announceSearchItems = announceSearchRepository.findAllByBodyContaining(param.getQuery(), PageRequest.of(param.getPage() * VoteRedisService.PAGE_SIZE, (param.getPage() + 1) * VoteRedisService.PAGE_SIZE));
            List<AnnouncePageItemDTO> announcePageItemDTOS = announceSearchItems.stream().map(AnnouncePageItemDTO::new).toList();
            existingAnnouncePageItems = announcePageItemDTOS;
            needToVoteFetchAnnounceList = announcePageItemDTOS;
        } else {
            existingAnnouncePageItems = pageCache.getItemList();
        }

        return Result.of(existingAnnouncePageItems, needToVoteFetchAnnounceList);
    }

    @Override
    public RFuture<Boolean> save(SearchAnnounceListProviderParam param, AnnouncePageDTO dto) {
        RMapCache<String, AnnouncePageDTO> pageMap = redissonClient.getMapCache(VoteRedisService.ANNOUNCE_SEARCH_PAGE_CACHE_MAP);
        return pageMap.fastPutAsync(param.generateKey(), dto, 15, TimeUnit.MINUTES);
    }
}
