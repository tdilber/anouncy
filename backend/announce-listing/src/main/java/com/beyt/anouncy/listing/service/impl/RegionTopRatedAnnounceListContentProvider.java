package com.beyt.anouncy.listing.service.impl;

import com.beyt.anouncy.common.entity.redis.AnnouncePageDTO;
import com.beyt.anouncy.common.entity.redis.AnnouncePageItemDTO;
import com.beyt.anouncy.common.repository.AnnounceRepository;
import com.beyt.anouncy.common.service.VoteRedisService;
import com.beyt.anouncy.listing.service.base.IAnnounceListContentProvider;
import com.beyt.anouncy.listing.service.base.parameter.RegionAnnounceListProviderParam;
import lombok.RequiredArgsConstructor;
import org.apache.commons.collections4.CollectionUtils;
import org.redisson.api.RFuture;
import org.redisson.api.RMap;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class RegionTopRatedAnnounceListContentProvider implements IAnnounceListContentProvider<RegionAnnounceListProviderParam> {
    protected final RedissonClient redissonClient;
    protected final RegionTopRatedAnnounceListOrderProvider orderProvider;
    protected final AnnounceRepository announceRepository;

    @Override
    public Result provide(RegionAnnounceListProviderParam param) {
        RMap<String, AnnouncePageDTO> pageMap = redissonClient.getMap(VoteRedisService.ANNOUNCE_PAGE_CACHE_MAP);

        List<String> announceIdList = orderProvider.provide(param);
        AnnouncePageDTO pageCache = pageMap.get(param.generateKey());
        List<AnnouncePageItemDTO> existingAnnouncePageItems = new ArrayList<>();
        List<String> missingAnnounceIdList = new ArrayList<>();
        List<AnnouncePageItemDTO> needToVoteFetchAnnounceList = new ArrayList<>();

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

        if (CollectionUtils.isNotEmpty(missingAnnounceIdList)) {
            var announceList = announceRepository.findAllById(missingAnnounceIdList).stream().map(AnnouncePageItemDTO::new).toList();
            needToVoteFetchAnnounceList.addAll(announceList);
            existingAnnouncePageItems.addAll(announceList);
        }

        return Result.of(existingAnnouncePageItems, needToVoteFetchAnnounceList);
    }

    @Override
    public RFuture<Boolean> save(RegionAnnounceListProviderParam param, AnnouncePageDTO dto) {
        RMap<String, AnnouncePageDTO> pageMap = redissonClient.getMap(VoteRedisService.ANNOUNCE_PAGE_CACHE_MAP);
        return pageMap.fastPutAsync(param.generateKey(), dto);
    }
}
