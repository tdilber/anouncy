package com.beyt.anouncy.listing.service.base;

import com.beyt.anouncy.listing.service.base.parameter.BaseAnnounceListProviderParam;
import org.redisson.api.RLock;

public interface IAnnounceListFetchDataLockProvider<T extends BaseAnnounceListProviderParam> {
    RLock getLock(T param);
}
