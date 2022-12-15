package com.beyt.anouncy.listing.service.impl.order;

import com.beyt.anouncy.common.service.VoteRedisService;
import com.beyt.anouncy.listing.service.base.IAnnounceListOrderProvider;
import com.beyt.anouncy.listing.service.base.parameter.RegionAnnounceListProviderParam;
import lombok.RequiredArgsConstructor;
import org.redisson.api.RScoredSortedSet;
import org.redisson.api.RedissonClient;
import org.redisson.client.protocol.ScoredEntry;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.List;

@Service
@RequiredArgsConstructor
public class RegionTopRatedAnnounceListOrderProvider implements IAnnounceListOrderProvider<RegionAnnounceListProviderParam> {
    protected final RedissonClient redissonClient;

    @Override
    public List<String> provide(RegionAnnounceListProviderParam param) {
        RScoredSortedSet<String> sortedSet = getScoredSortedSet(param.getRegionId());

        Collection<ScoredEntry<String>> scoredEntries = sortedSet.entryRange(param.getPage() * VoteRedisService.PAGE_SIZE, (param.getPage() + 1) * VoteRedisService.PAGE_SIZE);

        return scoredEntries.stream().map(ScoredEntry::getValue).toList();
    }

    private RScoredSortedSet<String> getScoredSortedSet(String regionId) {
        return redissonClient.getScoredSortedSet(VoteRedisService.ANNOUNCE_REGION_TOP_RATED_SORTED_SET_KEY + regionId);
    }
}
