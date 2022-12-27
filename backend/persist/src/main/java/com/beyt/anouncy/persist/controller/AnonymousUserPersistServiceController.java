package com.beyt.anouncy.persist.controller;

import com.beyt.anouncy.common.persist.v1.AnonymousUserPersistServiceGrpc;
import com.beyt.anouncy.common.v1.*;
import com.beyt.anouncy.persist.controller.base.BasePersistServiceController;
import com.beyt.anouncy.persist.entity.AnonymousUser;
import com.beyt.anouncy.persist.helper.AnonymousUserPtoConverter;
import com.beyt.anouncy.persist.helper.base.PtoConverter;
import com.beyt.anouncy.persist.repository.AnonymousUserRepository;
import io.grpc.stub.StreamObserver;
import lombok.RequiredArgsConstructor;
import net.devh.boot.grpc.server.service.GrpcService;
import org.springframework.data.neo4j.repository.Neo4jRepository;


@GrpcService
@RequiredArgsConstructor
public class AnonymousUserPersistServiceController extends AnonymousUserPersistServiceGrpc.AnonymousUserPersistServiceImplBase implements BasePersistServiceController<AnonymousUser, AnonymousUserPTO, AnonymousUserListPTO, AnonymousUserOptionalPTO> {
    private final AnonymousUserRepository anonymousUserRepository;
    private final AnonymousUserPtoConverter anonymousUserPtoConverter;

    // For Crud
    @Override
    public Neo4jRepository<AnonymousUser, String> getRepository() {
        return anonymousUserRepository;
    }

    @Override
    public PtoConverter<AnonymousUser, AnonymousUserPTO, AnonymousUserListPTO, AnonymousUserOptionalPTO> getConverter() {
        return anonymousUserPtoConverter;
    }

    @Override
    public void save(AnonymousUserPTO request, StreamObserver<AnonymousUserPTO> responseObserver) {
        BasePersistServiceController.super.save(request, responseObserver);
    }

    @Override
    public void saveAll(AnonymousUserListPTO request, StreamObserver<AnonymousUserListPTO> responseObserver) {
        BasePersistServiceController.super.saveAll(request, responseObserver);
    }

    @Override
    public void findById(IdStr request, StreamObserver<AnonymousUserOptionalPTO> responseObserver) {
        BasePersistServiceController.super.findById(request, responseObserver);
    }

    @Override
    public void existsById(IdStr request, StreamObserver<Exist> responseObserver) {
        BasePersistServiceController.super.existsById(request, responseObserver);
    }

    @Override
    public void findAllById(IdStrList request, StreamObserver<AnonymousUserListPTO> responseObserver) {
        BasePersistServiceController.super.findAllById(request, responseObserver);
    }

    @Override
    public void findAll(PageablePTO request, StreamObserver<AnonymousUserListPTO> responseObserver) {
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
    public void delete(AnonymousUserPTO request, StreamObserver<Empty> responseObserver) {
        BasePersistServiceController.super.delete(request, responseObserver);
    }

    @Override
    public void deleteAllById(IdStrList request, StreamObserver<Empty> responseObserver) {
        BasePersistServiceController.super.deleteAllById(request, responseObserver);
    }

    @Override
    public void deleteAll(AnonymousUserListPTO request, StreamObserver<Empty> responseObserver) {
        BasePersistServiceController.super.deleteAll(request, responseObserver);
    }
}
