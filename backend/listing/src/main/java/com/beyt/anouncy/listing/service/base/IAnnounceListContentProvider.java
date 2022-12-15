package com.beyt.anouncy.listing.service.base;

import com.beyt.anouncy.common.entity.redis.AnnouncePageDTO;
import com.beyt.anouncy.common.entity.redis.AnnouncePageItemDTO;
import com.beyt.anouncy.listing.service.base.parameter.BaseAnnounceListProviderParam;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.redisson.api.RFuture;

import java.util.List;

public interface IAnnounceListContentProvider<T extends BaseAnnounceListProviderParam> {

    Result provide(T param);

    RFuture<Boolean> save(T param, AnnouncePageDTO dto);

    @Data
    @NoArgsConstructor
    @AllArgsConstructor(staticName = "of")
    class Result {
        List<AnnouncePageItemDTO> announcePageItems;
        List<AnnouncePageItemDTO> needToVoteFetchAnnounceList;
    }
}
