package com.beyt.anouncy.listing.service.impl;

import com.beyt.anouncy.common.persist.AnnounceVoteFetchAllRequest;
import com.beyt.anouncy.common.persist.AnnounceVoteFetchAllRequestItem;
import com.beyt.anouncy.common.persist.AnnounceVoteListPTO;
import com.beyt.anouncy.common.persist.VoteFetchServiceGrpc;
import com.beyt.anouncy.listing.service.base.IAnnounceListVoteFetch;
import com.google.common.util.concurrent.ListenableFuture;
import lombok.RequiredArgsConstructor;
import net.devh.boot.grpc.client.inject.GrpcClient;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.Future;

@Service
@RequiredArgsConstructor
public class RedisAnnounceListVoteFetch implements IAnnounceListVoteFetch {
//    protected final VoteService voteService;


    @GrpcClient("vote-grpc-server")
    private VoteFetchServiceGrpc.VoteFetchServiceFutureStub voteFetchServiceBlockingStub;

    @Override
    public Future<AnnounceVoteListPTO> fetchAsync(Map<String, Set<String>> regionIdAnnounceIdSetMap) {
        AnnounceVoteFetchAllRequest request = AnnounceVoteFetchAllRequest.newBuilder().addAllMap(regionIdAnnounceIdSetMap.entrySet().stream().map(e -> AnnounceVoteFetchAllRequestItem.newBuilder().addAllAnnounceIdList(e.getValue()).setRegionId(e.getKey()).build()).toList()).build();
        ListenableFuture<AnnounceVoteListPTO> announceVoteListPTO = voteFetchServiceBlockingStub.fetchAll(request);
        return announceVoteListPTO;
    }
}
