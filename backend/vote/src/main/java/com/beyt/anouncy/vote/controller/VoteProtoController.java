package com.beyt.anouncy.vote.controller;

import com.beyt.anouncy.common.entity.redis.AnnounceVoteDTO;
import com.beyt.anouncy.common.persist.*;
import com.beyt.anouncy.vote.service.VoteFetchService;
import io.grpc.stub.StreamObserver;
import lombok.RequiredArgsConstructor;
import net.devh.boot.grpc.client.inject.GrpcClient;
import net.devh.boot.grpc.server.service.GrpcService;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@GrpcService
@RequiredArgsConstructor
public class VoteProtoController extends VoteFetchServiceGrpc.VoteFetchServiceImplBase {
    private final VoteFetchService voteFetchService;

    @GrpcClient("persist-grpc-server")
    private VotePersistServiceGrpc.VotePersistServiceBlockingStub votePersistServiceBlockingStub;

    @Override
    public void getAllVoteCounts(VoteCountMultiRequest request, StreamObserver<VoteCountListPTO> responseObserver) {
        responseObserver.onNext(votePersistServiceBlockingStub.getAllVoteCounts(request));
        responseObserver.onCompleted();
    }

    @Override
    public void getVoteCount(VoteCountSingleRequest request, StreamObserver<VoteCountPTO> responseObserver) {
        responseObserver.onNext(votePersistServiceBlockingStub.getVoteCount(request));
        responseObserver.onCompleted();
    }

    @Override
    public void getVoteSummaries(VoteSummaryRequest request, StreamObserver<VoteSummaryListPTO> responseObserver) {
        responseObserver.onNext(votePersistServiceBlockingStub.getVoteSummaries(request));
        responseObserver.onCompleted();
    }

    @Override
    public void fetchAll(AnnounceVoteFetchAllRequest request, StreamObserver<AnnounceVoteListPTO> responseObserver) {
        Map<String, Set<String>> collect = request.getMapList().stream().collect(Collectors.toMap(AnnounceVoteFetchAllRequestItem::getRegionId, m -> new HashSet<>(m.getAnnounceIdListList())));
        Map<String, AnnounceVoteDTO> fetch = voteFetchService.fetchAll(collect);
        AnnounceVoteListPTO listPTO = AnnounceVoteListPTO.newBuilder().addAllVoteList(fetch.entrySet().stream().map(e -> AnnounceVoteDTO.of(e.getValue(), e.getKey())).toList()).build();
        responseObserver.onNext(listPTO);
        responseObserver.onCompleted();
    }

    @Override
    public void fetchOne(AnnounceVoteFetchOneRequest request, StreamObserver<AnnounceVotePTO> responseObserver) {
        AnnounceVoteDTO announceVoteDTO = voteFetchService.fetchOne(request.getRegionId(), request.getAnnounceId());
        responseObserver.onNext(AnnounceVoteDTO.of(announceVoteDTO, request.getRegionId()));
        responseObserver.onCompleted();
    }
}
