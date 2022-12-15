package com.beyt.anouncy.listing.service.base.parameter;

import com.beyt.anouncy.listing.dto.enumeration.AnnounceListingType;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;


@Getter
@Setter
public abstract sealed class BaseAnnounceListProviderParam implements Serializable permits RegionAnnounceListProviderParam, SearchAnnounceListProviderParam {
    protected Integer page;
    protected AnnounceListingType type;

    public abstract String generateKey();
}
