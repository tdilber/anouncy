package com.beyt.anouncy.persist.controller;

import com.beyt.anouncy.common.persist.*;
import com.beyt.anouncy.persist.controller.base.BasePersistServiceController;
import com.beyt.anouncy.persist.entity.Announce;
import com.beyt.anouncy.persist.helper.AnnouncePtoConverter;
import com.beyt.anouncy.persist.helper.base.PtoConverter;
import com.beyt.anouncy.persist.repository.AnnounceRepository;
import io.grpc.stub.StreamObserver;
import lombok.RequiredArgsConstructor;
import net.devh.boot.grpc.server.service.GrpcService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;
import java.util.UUID;

@GrpcService
@RequiredArgsConstructor
public class AnnouncePersistServiceController extends AnnouncePersistServiceGrpc.AnnouncePersistServiceImplBase implements BasePersistServiceController<Announce, AnnouncePTO, AnnounceListPTO> {
    private final AnnounceRepository announceRepository;
    private final AnnouncePtoConverter announcePtoConverter;


    @Override
    public void findAllByAnonymousUserId(IdWithPageable request, StreamObserver<AnnounceListPTO> responseObserver) {
        Page<Announce> announcePage = announceRepository.findAllByAnonymousUserId(UUID.fromString(request.getId()), PageRequest.of(request.getPageable().getPage(), request.getPageable().getSize()));
        responseObserver.onNext(announcePtoConverter.toPtoList(announcePage.getContent()));
        responseObserver.onCompleted();
    }

    @Override
    public void findByIdAndAnonymousUserId(AnnounceIdAndAnonymousUserId request, StreamObserver<AnnouncePTO> responseObserver) {
        Optional<Announce> announceOpt = announceRepository.findByIdAndAnonymousUserId(request.getAnnounceId(), UUID.fromString(request.getAnonymousUserId()));
        announceOpt.ifPresent(a -> responseObserver.onNext(announcePtoConverter.toPto(a)));
        responseObserver.onCompleted();
    }


    // For Crud
    @Override
    public CrudRepository<Announce, String> getRepository() {
        return announceRepository;
    }

    @Override
    public PtoConverter<Announce, AnnouncePTO, AnnounceListPTO> getConverter() {
        return announcePtoConverter;
    }

    @Override
    public void save(AnnouncePTO request, StreamObserver<AnnouncePTO> responseObserver) {
        BasePersistServiceController.super.save(request, responseObserver);
    }

    @Override
    public void saveAll(AnnounceListPTO request, StreamObserver<AnnounceListPTO> responseObserver) {
        BasePersistServiceController.super.saveAll(request, responseObserver);
    }

    @Override
    public void findById(IdStr request, StreamObserver<AnnouncePTO> responseObserver) {
        BasePersistServiceController.super.findById(request, responseObserver);
    }

    @Override
    public void existsById(IdStr request, StreamObserver<Exist> responseObserver) {
        BasePersistServiceController.super.existsById(request, responseObserver);
    }

    @Override
    public void findAllById(IdStrList request, StreamObserver<AnnounceListPTO> responseObserver) {
        BasePersistServiceController.super.findAllById(request, responseObserver);
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
    public void delete(AnnouncePTO request, StreamObserver<Empty> responseObserver) {
        BasePersistServiceController.super.delete(request, responseObserver);
    }

    @Override
    public void deleteAllById(IdStrList request, StreamObserver<Empty> responseObserver) {
        BasePersistServiceController.super.deleteAllById(request, responseObserver);
    }

    @Override
    public void deleteAll(AnnounceListPTO request, StreamObserver<Empty> responseObserver) {
        BasePersistServiceController.super.deleteAll(request, responseObserver);
    }
}
