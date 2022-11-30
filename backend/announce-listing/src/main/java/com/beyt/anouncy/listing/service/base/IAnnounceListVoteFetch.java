package com.beyt.anouncy.listing.service.base;

import com.beyt.anouncy.common.entity.redis.AnnounceVoteDTO;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.Future;

public interface IAnnounceListVoteFetch {
    Future<Map<String, AnnounceVoteDTO>> fetchAsync(Map<String, Set<String>> regionIdAnnounceIdSetMap);
}
