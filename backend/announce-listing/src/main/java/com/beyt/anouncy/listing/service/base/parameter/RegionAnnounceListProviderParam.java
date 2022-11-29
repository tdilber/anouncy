package com.beyt.anouncy.listing.service.base.parameter;

import com.beyt.anouncy.listing.dto.enumeration.AnnounceListingType;
import lombok.Getter;
import lombok.Setter;
import org.springframework.util.StringUtils;

import java.util.List;

@Getter
@Setter
public final class RegionAnnounceListProviderParam extends BaseAnnounceListProviderParam {
    private String regionId;

    public static RegionAnnounceListProviderParam of(String regionId, Integer page, AnnounceListingType type) {
        RegionAnnounceListProviderParam param = new RegionAnnounceListProviderParam();
        param.setPage(page);
        param.setRegionId(regionId);
        param.setType(type);
        return param;
    }

    public String generateKey() {
        return "region_" + StringUtils.collectionToDelimitedString(List.of(type, regionId, page), "_");
    }
}
