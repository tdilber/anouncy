package com.beyt.anouncy.listing.service.impl;

import com.beyt.anouncy.listing.service.base.IAnnounceListFetchDataLockProvider;
import com.beyt.anouncy.listing.service.base.parameter.BaseAnnounceListProviderParam;
import lombok.RequiredArgsConstructor;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class GeneralLockAnnounceListFetchDataLockProvider<T extends BaseAnnounceListProviderParam> implements IAnnounceListFetchDataLockProvider<T> {
    protected final RedissonClient redissonClient;

    @Override
    public RLock getLock(T param) {
        return redissonClient.getLock(param.generateKey());
    }
}
