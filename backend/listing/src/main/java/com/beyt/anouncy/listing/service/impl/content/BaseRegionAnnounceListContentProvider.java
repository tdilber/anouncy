package com.beyt.anouncy.listing.service.impl.content;

import com.beyt.anouncy.common.entity.redis.AnnouncePageDTO;
import com.beyt.anouncy.common.entity.redis.AnnouncePageItemDTO;
import com.beyt.anouncy.common.persist.v1.AnnouncePersistServiceGrpc;
import com.beyt.anouncy.common.service.VoteRedisService;
import com.beyt.anouncy.common.util.ProtoUtil;
import com.beyt.anouncy.listing.service.base.IAnnounceListContentProvider;
import com.beyt.anouncy.listing.service.base.IAnnounceListOrderProvider;
import com.beyt.anouncy.listing.service.base.parameter.RegionAnnounceListProviderParam;
import lombok.RequiredArgsConstructor;
import net.devh.boot.grpc.client.inject.GrpcClient;
import org.apache.commons.collections4.CollectionUtils;
import org.redisson.api.RFuture;
import org.redisson.api.RMap;
import org.redisson.api.RedissonClient;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;


@RequiredArgsConstructor
public sealed abstract class BaseRegionAnnounceListContentProvider implements IAnnounceListContentProvider<RegionAnnounceListProviderParam> permits RegionTopRatedAnnounceListContentProvider, RegionTrendingAnnounceListContentProvider, RegionNewestAnnounceListContentProvider {
    protected final RedissonClient redissonClient;

    @GrpcClient("persist-grpc-server")
    private AnnouncePersistServiceGrpc.AnnouncePersistServiceBlockingStub announcePersistServiceBlockingStub;

    public abstract IAnnounceListOrderProvider<RegionAnnounceListProviderParam> getOrderProvider();

    @Override
    public Result provide(RegionAnnounceListProviderParam param) {
        RMap<String, AnnouncePageDTO> pageMap = redissonClient.getMap(VoteRedisService.ANNOUNCE_PAGE_CACHE_MAP);

        List<String> announceIdList = getOrderProvider().provide(param);
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
            var announcePTOList = announcePersistServiceBlockingStub.findAllById(ProtoUtil.toIdStrList(missingAnnounceIdList));
            var announceList = announcePTOList.getAnnounceListList().stream().map(AnnouncePageItemDTO::new).toList();
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
