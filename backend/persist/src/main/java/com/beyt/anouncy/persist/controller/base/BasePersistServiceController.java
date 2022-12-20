package com.beyt.anouncy.persist.controller.base;

import com.beyt.anouncy.common.util.ProtoUtil;
import com.beyt.anouncy.common.v1.*;
import com.beyt.anouncy.persist.helper.base.PtoConverter;
import io.grpc.stub.StreamObserver;
import org.springframework.data.neo4j.repository.Neo4jRepository;

import java.util.Objects;
import java.util.Optional;

public interface BasePersistServiceController<Entity, Pto, PtoList> {

    Neo4jRepository<Entity, String> getRepository();

    PtoConverter<Entity, Pto, PtoList> getConverter();


    default void save(Pto request, StreamObserver<Pto> responseObserver) {
        Entity entity = getRepository().save(Objects.requireNonNull(getConverter().toEntity(request)));
        responseObserver.onNext(getConverter().toPto(entity));
        responseObserver.onCompleted();
    }

    default void saveAll(PtoList request, StreamObserver<PtoList> responseObserver) {
        Iterable<Entity> voteList = getRepository().saveAll(getConverter().toEntityList(request));
        responseObserver.onNext(getConverter().toPtoList(voteList));
        responseObserver.onCompleted();
    }

    default void findById(IdStr request, StreamObserver<Pto> responseObserver) {
        Optional<Entity> voteOpt = getRepository().findById(ProtoUtil.of(request));
        voteOpt.ifPresent(v -> responseObserver.onNext(getConverter().toPto(v)));
        responseObserver.onCompleted();
    }

    default void existsById(IdStr request, StreamObserver<Exist> responseObserver) {
        boolean exists = getRepository().existsById(ProtoUtil.of(request));
        responseObserver.onNext(Exist.newBuilder().setExist(exists).build());
        responseObserver.onCompleted();
    }

    default void findAllById(IdStrList request, StreamObserver<PtoList> responseObserver) {
        Iterable<Entity> voteList = getRepository().findAllById(ProtoUtil.of(request));
        responseObserver.onNext(getConverter().toPtoList(voteList));
        responseObserver.onCompleted();
    }

    default void findAll(PageablePTO request, StreamObserver<PtoList> responseObserver) {
        Iterable<Entity> voteList = getRepository().findAll(ProtoUtil.toPageable(request));
        responseObserver.onNext(getConverter().toPtoList(voteList));
        responseObserver.onCompleted();
    }

    default void count(Empty request, StreamObserver<Count> responseObserver) {
        long count = getRepository().count();
        responseObserver.onNext(Count.newBuilder().setCount(count).build());
        responseObserver.onCompleted();
    }

    default void deleteById(IdStr request, StreamObserver<Empty> responseObserver) {
        getRepository().deleteById(ProtoUtil.of(request));
        responseObserver.onNext(Empty.newBuilder().build());
        responseObserver.onCompleted();
    }

    default void delete(Pto request, StreamObserver<Empty> responseObserver) {
        getRepository().delete(Objects.requireNonNull(getConverter().toEntity(request)));
        responseObserver.onNext(Empty.newBuilder().build());
        responseObserver.onCompleted();
    }

    default void deleteAllById(IdStrList request, StreamObserver<Empty> responseObserver) {
        getRepository().deleteAllById(ProtoUtil.of(request));
        responseObserver.onNext(Empty.newBuilder().build());
        responseObserver.onCompleted();
    }

    default void deleteAll(PtoList request, StreamObserver<Empty> responseObserver) {
        getRepository().deleteAll(getConverter().toEntityList(request));
        responseObserver.onNext(Empty.newBuilder().build());
        responseObserver.onCompleted();
    }
}
