package com.beyt.anouncy.listing.service.impl.content;

import com.beyt.anouncy.common.repository.AnnounceRepository;
import com.beyt.anouncy.listing.service.base.IAnnounceListOrderProvider;
import com.beyt.anouncy.listing.service.base.parameter.RegionAnnounceListProviderParam;
import com.beyt.anouncy.listing.service.impl.order.RegionNewestAnnounceListOrderProvider;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Service;

@Service
public final class RegionNewestAnnounceListContentProvider extends BaseRegionAnnounceListContentProvider {
    private final RegionNewestAnnounceListOrderProvider orderProvider;

    public RegionNewestAnnounceListContentProvider(RegionNewestAnnounceListOrderProvider orderProvider, RedissonClient redissonClient, AnnounceRepository announceRepository) {
        super(redissonClient, announceRepository);
        this.orderProvider = orderProvider;
    }

    @Override
    public IAnnounceListOrderProvider<RegionAnnounceListProviderParam> getOrderProvider() {
        return orderProvider;
    }
}
