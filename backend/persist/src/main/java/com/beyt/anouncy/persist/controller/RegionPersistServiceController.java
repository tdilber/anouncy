package com.beyt.anouncy.persist.controller;

import com.beyt.anouncy.common.persist.v1.RegionPersistServiceGrpc;
import com.beyt.anouncy.common.v1.*;
import com.beyt.anouncy.persist.controller.base.BasePersistServiceController;
import com.beyt.anouncy.persist.entity.Region;
import com.beyt.anouncy.persist.helper.RegionPtoConverter;
import com.beyt.anouncy.persist.helper.base.PtoConverter;
import com.beyt.anouncy.persist.repository.RegionRepository;
import io.grpc.stub.StreamObserver;
import lombok.RequiredArgsConstructor;
import net.devh.boot.grpc.server.service.GrpcService;
import org.apache.commons.lang3.NotImplementedException;
import org.springframework.data.neo4j.repository.Neo4jRepository;


@GrpcService
@RequiredArgsConstructor
public class RegionPersistServiceController extends RegionPersistServiceGrpc.RegionPersistServiceImplBase implements BasePersistServiceController<Region, RegionPTO, RegionListPTO> {
    private final RegionRepository regionRepository;
    private final RegionPtoConverter regionPtoConverter;


    @Override
    public void findAllByParentRegionIdIsIn(IdStrList request, StreamObserver<RegionListPTO> responseObserver) {
        throw new NotImplementedException();
    }

    @Override
    public void findAllByLocationIdIsIn(IdStrList request, StreamObserver<RegionListPTO> responseObserver) {
        throw new NotImplementedException();
    }

    @Override
    public void findByLocationId(IdStr request, StreamObserver<RegionPTO> responseObserver) {
        throw new NotImplementedException();
    }

    // For Crud
    @Override
    public Neo4jRepository<Region, String> getRepository() {
        return regionRepository;
    }

    @Override
    public PtoConverter<Region, RegionPTO, RegionListPTO> getConverter() {
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
    public void findById(IdStr request, StreamObserver<RegionPTO> responseObserver) {
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
