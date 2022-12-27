package com.beyt.anouncy.persist.controller;

import com.beyt.anouncy.common.persist.v1.RegionPersistServiceGrpc;
import com.beyt.anouncy.common.util.ProtoUtil;
import com.beyt.anouncy.common.v1.*;
import com.beyt.anouncy.persist.controller.base.BasePersistServiceController;
import com.beyt.anouncy.persist.entity.Region;
import com.beyt.anouncy.persist.helper.RegionPtoConverter;
import com.beyt.anouncy.persist.helper.base.PtoConverter;
import com.beyt.anouncy.persist.repository.RegionRepository;
import io.grpc.stub.StreamObserver;
import lombok.RequiredArgsConstructor;
import net.devh.boot.grpc.server.service.GrpcService;
import org.springframework.data.neo4j.repository.Neo4jRepository;

import java.util.List;
import java.util.Optional;


@GrpcService
@RequiredArgsConstructor
public class RegionPersistServiceController extends RegionPersistServiceGrpc.RegionPersistServiceImplBase implements BasePersistServiceController<Region, RegionPTO, RegionListPTO, RegionOptionalPTO> {
    private final RegionRepository regionRepository;
    private final RegionPtoConverter regionPtoConverter;


    @Override
    public void findAllByParentRegionIdIsIn(IdStrList request, StreamObserver<RegionListPTO> responseObserver) {
        List<Region> regionList = regionRepository.findAllByParentRegionIdIsIn(ProtoUtil.of(request));
        responseObserver.onNext(regionPtoConverter.toPtoList(regionList));
        responseObserver.onCompleted();
    }

    @Override
    public void findAllByLocationIdIsIn(IdLongList request, StreamObserver<RegionListPTO> responseObserver) {
        List<Region> regionList = regionRepository.findAllByLocationIdIsIn(ProtoUtil.of(request));
        responseObserver.onNext(regionPtoConverter.toPtoList(regionList));
        responseObserver.onCompleted();
    }

    @Override
    public void findByLocationId(IdLong request, StreamObserver<RegionOptionalPTO> responseObserver) {
        Optional<Region> regionOpt = regionRepository.findByLocationId(ProtoUtil.of(request));
        responseObserver.onNext(regionPtoConverter.toOptionalEntity(regionOpt));
        responseObserver.onCompleted();
    }

    // For Crud
    @Override
    public Neo4jRepository<Region, String> getRepository() {
        return regionRepository;
    }

    @Override
    public PtoConverter<Region, RegionPTO, RegionListPTO, RegionOptionalPTO> getConverter() {
        return regionPtoConverter;
    }

    @Override
    public void save(RegionPTO request, StreamObserver<RegionPTO> responseObserver) {
        BasePersistServiceController.super.save(request, responseObserver);
    }

    @Override
    public void saveAll(RegionListPTO request, StreamObserver<RegionListPTO> responseObserver) {
        BasePersistServiceController.super.saveAll(request, responseObserver);
    }

    @Override
    public void findById(IdStr request, StreamObserver<RegionOptionalPTO> responseObserver) {
        BasePersistServiceController.super.findById(request, responseObserver);
    }

    @Override
    public void existsById(IdStr request, StreamObserver<Exist> responseObserver) {
        BasePersistServiceController.super.existsById(request, responseObserver);
    }

    @Override
    public void findAllById(IdStrList request, StreamObserver<RegionListPTO> responseObserver) {
        BasePersistServiceController.super.findAllById(request, responseObserver);
    }

    @Override
    public void findAll(PageablePTO request, StreamObserver<RegionListPTO> responseObserver) {
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
    public void delete(RegionPTO request, StreamObserver<Empty> responseObserver) {
        BasePersistServiceController.super.delete(request, responseObserver);
    }

    @Override
    public void deleteAllById(IdStrList request, StreamObserver<Empty> responseObserver) {
        BasePersistServiceController.super.deleteAllById(request, responseObserver);
    }

    @Override
    public void deleteAll(RegionListPTO request, StreamObserver<Empty> responseObserver) {
        BasePersistServiceController.super.deleteAll(request, responseObserver);
    }
}
