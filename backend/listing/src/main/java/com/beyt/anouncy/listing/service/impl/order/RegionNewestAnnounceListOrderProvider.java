package com.beyt.anouncy.listing.service.impl.order;

import com.beyt.anouncy.common.service.VoteRedisService;
import com.beyt.anouncy.listing.service.base.IAnnounceListOrderProvider;
import com.beyt.anouncy.listing.service.base.parameter.RegionAnnounceListProviderParam;
import lombok.RequiredArgsConstructor;
import org.redisson.api.RList;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class RegionNewestAnnounceListOrderProvider implements IAnnounceListOrderProvider<RegionAnnounceListProviderParam> {
    protected final RedissonClient redissonClient;

    @Override
    public List<String> provide(RegionAnnounceListProviderParam param) {
        RList<String> rList = getNewestList(param.getRegionId());
        return rList.subList(param.getPage() * VoteRedisService.PAGE_SIZE, (param.getPage() + 1) * VoteRedisService.PAGE_SIZE);
    }

    private RList<String> getNewestList(String regionId) {
        return redissonClient.getList(VoteRedisService.ANNOUNCE_REGION_NEWEST_LIST_KEY + regionId);
    }
}
