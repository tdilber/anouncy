package com.beyt.anouncy.search.controller;

import com.beyt.anouncy.common.search.v1.AnnounceSearchServiceGrpc;
import com.beyt.anouncy.common.search.v1.SearchParam;
import com.beyt.anouncy.common.util.ProtoUtil;
import com.beyt.anouncy.common.v1.*;
import com.beyt.anouncy.search.converter.AnnouncePtoConverter;
import com.beyt.anouncy.search.entity.AnnounceSearchItem;
import com.beyt.anouncy.search.repository.AnnounceSearchRepository;
import io.grpc.stub.StreamObserver;
import lombok.RequiredArgsConstructor;
import net.devh.boot.grpc.server.service.GrpcService;
import org.springframework.data.domain.Page;

import java.util.Objects;
import java.util.Optional;

@GrpcService
@RequiredArgsConstructor
public class AnnounceSearchServiceController extends AnnounceSearchServiceGrpc.AnnounceSearchServiceImplBase {
    private final AnnounceSearchRepository announceSearchRepository;
    private final AnnouncePtoConverter announcePtoConverter;

    @Override
    public void search(SearchParam request, StreamObserver<AnnouncePagePTO> responseObserver) {
        Page<AnnounceSearchItem> announcePage = announceSearchRepository.findAllByBodyContaining(request.getQuery(), ProtoUtil.toPageable(request.getPageable()));
        responseObserver.onNext(AnnouncePagePTO.newBuilder().addAllAnnounceList(announcePage.getContent().stream().map(announcePtoConverter::toPto).toList()).setPageable(ProtoUtil.toPageable(announcePage.getPageable())).setTotalElement(announcePage.getTotalElements()).build());
        responseObserver.onCompleted();
    }

    @Override
    public void save(AnnouncePTO request, StreamObserver<AnnouncePTO> responseObserver) {
        AnnounceSearchItem entity = announceSearchRepository.save(Objects.requireNonNull(announcePtoConverter.toEntity(request)));
        responseObserver.onNext(announcePtoConverter.toPto(entity));
        responseObserver.onCompleted();
    }

    @Override
    public void saveAll(AnnounceListPTO request, StreamObserver<AnnounceListPTO> responseObserver) {
        Iterable<AnnounceSearchItem> voteList = announceSearchRepository.saveAll(announcePtoConverter.toEntityList(request));
        responseObserver.onNext(announcePtoConverter.toPtoList(voteList));
        responseObserver.onCompleted();
    }

    @Override
    public void findById(IdStr request, StreamObserver<AnnounceOptionalPTO> responseObserver) {
        Optional<AnnounceSearchItem> voteOpt = announceSearchRepository.findById(ProtoUtil.of(request));
        responseObserver.onNext(announcePtoConverter.toOptionalEntity(voteOpt));
        responseObserver.onCompleted();
    }

    @Override
    public void existsById(IdStr request, StreamObserver<Exist> responseObserver) {
        boolean exists = announceSearchRepository.existsById(ProtoUtil.of(request));
        responseObserver.onNext(Exist.newBuilder().setExist(exists).build());
        responseObserver.onCompleted();
    }

    @Override
    public void findAllById(IdStrList request, StreamObserver<AnnounceListPTO> responseObserver) {
        Iterable<AnnounceSearchItem> voteList = announceSearchRepository.findAllById(ProtoUtil.of(request));
        responseObserver.onNext(announcePtoConverter.toPtoList(voteList));
        responseObserver.onCompleted();
    }

    @Override
    public void findAll(PageablePTO request, StreamObserver<AnnounceListPTO> responseObserver) {
        Iterable<AnnounceSearchItem> voteList = announceSearchRepository.findAll(ProtoUtil.toPageable(request));
        responseObserver.onNext(announcePtoConverter.toPtoList(voteList));
        responseObserver.onCompleted();
    }

    @Override
    public void count(Empty request, StreamObserver<Count> responseObserver) {
        long count = announceSearchRepository.count();
        responseObserver.onNext(Count.newBuilder().setCount(count).build());
        responseObserver.onCompleted();
    }

    @Override
    public void deleteById(IdStr request, StreamObserver<Empty> responseObserver) {
        announceSearchRepository.deleteById(ProtoUtil.of(request));
        responseObserver.onNext(Empty.newBuilder().build());
        responseObserver.onCompleted();
    }

    @Override
    public void delete(AnnouncePTO request, StreamObserver<Empty> responseObserver) {
        announceSearchRepository.delete(Objects.requireNonNull(announcePtoConverter.toEntity(request)));
        responseObserver.onNext(Empty.newBuilder().build());
        responseObserver.onCompleted();
    }

    @Override
    public void deleteAllById(IdStrList request, StreamObserver<Empty> responseObserver) {
        announceSearchRepository.deleteAllById(ProtoUtil.of(request));
        responseObserver.onNext(Empty.newBuilder().build());
        responseObserver.onCompleted();
    }

    @Override
    public void deleteAll(AnnounceListPTO request, StreamObserver<Empty> responseObserver) {
        announceSearchRepository.deleteAll(announcePtoConverter.toEntityList(request));
        responseObserver.onNext(Empty.newBuilder().build());
        responseObserver.onCompleted();
    }
}
