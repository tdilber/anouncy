package com.beyt.anouncy.listing.service.impl.content;

import com.beyt.anouncy.listing.service.base.IAnnounceListOrderProvider;
import com.beyt.anouncy.listing.service.base.parameter.RegionAnnounceListProviderParam;
import com.beyt.anouncy.listing.service.impl.order.RegionTrendingAnnounceListOrderProvider;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Service;

@Service
public final class RegionTrendingAnnounceListContentProvider extends BaseRegionAnnounceListContentProvider {
    private final RegionTrendingAnnounceListOrderProvider orderProvider;

    public RegionTrendingAnnounceListContentProvider(RegionTrendingAnnounceListOrderProvider orderProvider, RedissonClient redissonClient) {
        super(redissonClient);
        this.orderProvider = orderProvider;
    }

    @Override
    public IAnnounceListOrderProvider<RegionAnnounceListProviderParam> getOrderProvider() {
        return orderProvider;
    }
}
