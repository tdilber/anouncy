package com.beyt.anouncy.listing.service.base.parameter;

import com.beyt.anouncy.listing.dto.enumeration.AnnounceListingType;
import lombok.Getter;
import lombok.Setter;
import org.springframework.util.StringUtils;

import java.util.List;

@Getter
@Setter
public final class SearchAnnounceListProviderParam extends BaseAnnounceListProviderParam {
    private String query;

    public static SearchAnnounceListProviderParam of(String query, Integer page, AnnounceListingType type) {
        SearchAnnounceListProviderParam param = new SearchAnnounceListProviderParam();
        param.setPage(page);
        param.setQuery(query);
        param.setType(type);
        return param;
    }

    public String generateKey() {
        return "search_" + StringUtils.collectionToDelimitedString(List.of(type, query, page), "_");
    }
}
