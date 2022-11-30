package com.beyt.anouncy.listing.dto.enumeration;

import com.beyt.anouncy.listing.service.base.parameter.BaseAnnounceListProviderParam;

public enum AnnounceListingType {
    NEW("", "generalLockAnnounceListFetchDataLockProvider", "redisAnnounceListVoteFetch", BaseAnnounceListProviderParam.class),
    TOP("regionTopRatedAnnounceListContentProvider", "generalLockAnnounceListFetchDataLockProvider", "redisAnnounceListVoteFetch", BaseAnnounceListProviderParam.class),
    TRENDING("regionTrendingAnnounceListContentProvider", "generalLockAnnounceListFetchDataLockProvider", "redisAnnounceListVoteFetch", BaseAnnounceListProviderParam.class),
    SEARCH("", "generalLockAnnounceListFetchDataLockProvider", "redisAnnounceListVoteFetch", BaseAnnounceListProviderParam.class);

    private final String contentProviderBean;
    private final String fetchDataLockBean;
    private final String voteFetchBean;
    private final Class<? extends BaseAnnounceListProviderParam> paramClassType;

    AnnounceListingType(String contentProviderBean, String fetchDataLockBean, String voteFillerBean, Class<? extends BaseAnnounceListProviderParam> paramClassType) {
        this.contentProviderBean = contentProviderBean;
        this.fetchDataLockBean = fetchDataLockBean;
        this.voteFetchBean = voteFillerBean;
        this.paramClassType = paramClassType;
    }

    public boolean isAvailableForRegionListing() {
        return this == NEW || this == TOP || this == TRENDING;
    }

    public String getContentProviderBean() {
        return contentProviderBean;
    }

    public String getFetchDataLockBean() {
        return fetchDataLockBean;
    }

    public String getVoteFetchBean() {
        return voteFetchBean;
    }

    public Class<? extends BaseAnnounceListProviderParam> getParamClassType() {
        return paramClassType;
    }
}
