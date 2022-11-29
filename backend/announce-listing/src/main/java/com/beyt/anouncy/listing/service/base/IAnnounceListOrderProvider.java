package com.beyt.anouncy.listing.service.base;

import com.beyt.anouncy.listing.service.base.parameter.BaseAnnounceListProviderParam;

import java.util.List;

public interface IAnnounceListOrderProvider<T extends BaseAnnounceListProviderParam> {
    List<String> provide(T param); // Ordered Announce Id List
}
