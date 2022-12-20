package com.beyt.anouncy.persist.controller;

import com.beyt.anouncy.common.persist.v1.VotePersistServiceGrpc;
import com.beyt.anouncy.common.v1.*;
import com.beyt.anouncy.persist.controller.base.BasePersistServiceController;
import com.beyt.anouncy.persist.entity.Vote;
import com.beyt.anouncy.persist.entity.model.VoteCount;
import com.beyt.anouncy.persist.entity.model.VoteSummary;
import com.beyt.anouncy.persist.helper.VotePtoConverter;
import com.beyt.anouncy.persist.helper.base.PtoConverter;
import com.beyt.anouncy.persist.repository.Neo4jCustomRepository;
import com.beyt.anouncy.persist.repository.VoteRepository;
import io.grpc.stub.StreamObserver;
import lombok.RequiredArgsConstructor;
import net.devh.boot.grpc.server.service.GrpcService;
import org.springframework.data.neo4j.repository.Neo4jRepository;

import java.util.Collection;
import java.util.Optional;
import java.util.UUID;

@GrpcService
@RequiredArgsConstructor
public class VotePersistServiceController extends VotePersistServiceGrpc.VotePersistServiceImplBase implements BasePersistServiceController<Vote, VotePTO, VoteListPTO> {
    private final Neo4jCustomRepository neo4jCustomRepository;
    private final VoteRepository voteRepository;
    private final VotePtoConverter votePtoConverter;

    @Override
    public void getAllVoteCounts(VoteCountMultiRequest request, StreamObserver<VoteCountListPTO> responseObserver) {
        Collection<com.beyt.anouncy.persist.entity.model.VoteCount> allVoteCounts = neo4jCustomRepository.getAllVoteCounts(request.getRegionId(), request.getAnnounceIdListList());
        responseObserver.onNext(VoteCountListPTO.newBuilder().addAllVoteCountList(allVoteCounts.stream().map(VoteCount::convert).toList()).build());
        responseObserver.onCompleted();
    }

    @Override
    public void getVoteCount(VoteCountSingleRequest request, StreamObserver<VoteCountPTO> responseObserver) {
        Optional<com.beyt.anouncy.persist.entity.model.VoteCount> voteCount = neo4jCustomRepository.getVoteCount(request.getRegionId(), request.getAnnounceId());
        voteCount.ifPresent(v -> responseObserver.onNext(v.convert()));
        responseObserver.onCompleted();
    }

    @Override
    public void getVoteSummaries(VoteSummaryRequest request, StreamObserver<VoteSummaryListPTO> responseObserver) {
        Collection<com.beyt.anouncy.persist.entity.model.VoteSummary> voteSummaries = neo4jCustomRepository.getVoteSummaries(UUID.fromString(request.getAnonymousUserId()), request.getRegionId(), request.getAnnounceIdListList());
        var result = VoteSummaryListPTO.newBuilder().addAllVoteSummaryList(voteSummaries.stream().map(VoteSummary::convert).toList()).build();
        responseObserver.onNext(result);
        responseObserver.onCompleted();
    }

    // CRUD Side
    @Override
    public Neo4jRepository<Vote, String> getRepository() {
        return voteRepository;
    }

    @Override
    public PtoConverter<Vote, VotePTO, VoteListPTO> getConverter() {
        return votePtoConverter;
    }

    @Override
    public void save(VotePTO request, StreamObserver<VotePTO> responseObserver) {
        BasePersistServiceController.super.save(request, responseObserver);
    }

    @Override
    public void saveAll(VoteListPTO request, StreamObserver<VoteListPTO> responseObserver) {
        BasePersistServiceController.super.saveAll(request, responseObserver);
    }

    @Override
    public void findById(IdStr request, StreamObserver<VotePTO> responseObserver) {
        BasePersistServiceController.super.findById(request, responseObserver);
    }

    @Override
    public void existsById(IdStr request, StreamObserver<Exist> responseObserver) {
        BasePersistServiceController.super.existsById(request, responseObserver);
    }

    @Override
    public void findAllById(IdStrList request, StreamObserver<VoteListPTO> responseObserver) {
        BasePersistServiceController.super.findAllById(request, responseObserver);
    }

    @Override
    public void findAll(PageablePTO request, StreamObserver<VoteListPTO> responseObserver) {
        BasePersistServiceController.super.findAll(request, responseObserver);
    }

    @Override
    public void count(Empty request, StreamObserver<Count> responseObserver) {
        BasePersistServiceController.super.count(request, responseObserver);
    }

    @Override
    public void deleteById(IdStr request, StreamObserver<Empty> responseObserver) {
        BasePersistServiceController.super.deleteById(request, responseObserver);
    }

    @Override
    public void delete(VotePTO request, StreamObserver<Empty> responseObserver) {
        BasePersistServiceController.super.delete(request, responseObserver);
    }

    @Override
    public void deleteAllById(IdStrList request, StreamObserver<Empty> responseObserver) {
        BasePersistServiceController.super.deleteAllById(request, responseObserver);
    }

    @Override
    public void deleteAll(VoteListPTO request, StreamObserver<Empty> responseObserver) {
        BasePersistServiceController.super.deleteAll(request, responseObserver);
    }
}
