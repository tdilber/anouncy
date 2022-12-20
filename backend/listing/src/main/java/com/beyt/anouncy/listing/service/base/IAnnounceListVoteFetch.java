package com.beyt.anouncy.listing.service.base;

import com.beyt.anouncy.common.vote.v1.AnnounceVoteListPTO;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.Future;

public interface IAnnounceListVoteFetch {
    Future<AnnounceVoteListPTO> fetchAsync(Map<String, Set<String>> regionIdAnnounceIdSetMap);
}
