package com.beyt.anouncy.listing.service;

import com.beyt.anouncy.common.context.UserContext;
import com.beyt.anouncy.common.entity.redis.AnnouncePageDTO;
import com.beyt.anouncy.common.entity.redis.AnnouncePageItemDTO;
import com.beyt.anouncy.common.repository.VoteRepository;
import com.beyt.anouncy.listing.dto.enumeration.AnnounceListingType;
import com.beyt.anouncy.listing.service.base.AnnounceListingProcessor;
import com.beyt.anouncy.listing.service.base.IAnnounceListContentProvider;
import com.beyt.anouncy.listing.service.base.IAnnounceListFetchDataLockProvider;
import com.beyt.anouncy.listing.service.base.IAnnounceListVoteFetch;
import com.beyt.anouncy.listing.service.base.parameter.BaseAnnounceListProviderParam;
import com.beyt.anouncy.listing.service.base.parameter.RegionAnnounceListProviderParam;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;

import java.util.*;

@Slf4j
@Service
public class AnnounceListingService {
    private final Map<AnnounceListingType, AnnounceListingProcessor<?>> announceListingProcessorMap;
    private final VoteRepository voteRepository;

    @Autowired
    private UserContext userContext;

    public AnnounceListingService(Map<String, IAnnounceListContentProvider<? extends BaseAnnounceListProviderParam>> announceListContentProviderMap, Map<String, IAnnounceListFetchDataLockProvider<? extends BaseAnnounceListProviderParam>> announceListFetchDataLockMap, Map<String, IAnnounceListVoteFetch> announceListVoteFetchMap, VoteRepository voteRepository) {
        announceListingProcessorMap = AnnounceListingProcessor.generateAnnounceListingProcessorMap(announceListContentProviderMap, announceListFetchDataLockMap, announceListVoteFetchMap);
        this.voteRepository = voteRepository;
    }

    @Retryable(maxAttempts = 2, value = Exception.class)
    public AnnouncePageDTO list(String regionId, AnnounceListingType type, Integer page) throws Exception {
        AnnounceListingProcessor<RegionAnnounceListProviderParam> listingProcessor = getListingProcessor(type);
        AnnouncePageDTO announcePageDTO = listingProcessor.fetchList(RegionAnnounceListProviderParam.of(regionId, page, type));

        if (Objects.nonNull(userContext.getAnonymousUserId())) {
            announcePageDTO = populateUserVotes(userContext.getAnonymousUserId(), announcePageDTO);
        }
        return announcePageDTO;
    }

    @SuppressWarnings("unchecked")
    private AnnounceListingProcessor<RegionAnnounceListProviderParam> getListingProcessor(AnnounceListingType type) {
        AnnounceListingProcessor<?> announceListingProcessor = announceListingProcessorMap.get(type);
        return (AnnounceListingProcessor<RegionAnnounceListProviderParam>) announceListingProcessor;
    }

    public AnnouncePageDTO populateUserVotes(UUID anonymousUserId, AnnouncePageDTO page) {
        List<String> announceIdList = page.getItemList().stream().map(AnnouncePageItemDTO::getAnnounceId).toList();

        Collection<VoteRepository.VoteSummary> voteList = voteRepository.findByAnonymousUserIdAndAnnounceIdIsIn(anonymousUserId, announceIdList);
        page.getItemList().forEach(i ->
                voteList.stream().filter(v -> v.getAnnounceId().equals(i.getAnnounceId())).findFirst()
                        .ifPresent(v -> i.setCurrentVote(v.getValue()))
        );
        return page;
    }
}
