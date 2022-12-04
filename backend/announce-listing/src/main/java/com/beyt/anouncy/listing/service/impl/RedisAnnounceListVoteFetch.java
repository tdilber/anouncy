package com.beyt.anouncy.listing.service.impl;

import com.beyt.anouncy.common.entity.redis.AnnounceVoteDTO;
import com.beyt.anouncy.common.service.VoteService;
import com.beyt.anouncy.listing.service.base.IAnnounceListVoteFetch;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.Future;

@Service
@RequiredArgsConstructor
public class RedisAnnounceListVoteFetch implements IAnnounceListVoteFetch {
    protected final VoteService voteService;

    @Override
    public Future<Map<String, AnnounceVoteDTO>> fetchAsync(Map<String, Set<String>> regionIdAnnounceIdSetMap) {
        return voteService.fetchAsync(regionIdAnnounceIdSetMap);
    }
}
