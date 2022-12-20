package com.beyt.anouncy.region.controller;

import com.beyt.anouncy.common.persist.v1.RegionPersistServiceGrpc;
import com.beyt.anouncy.common.region.v1.RegionServiceGrpc;
import com.beyt.anouncy.common.v1.IdStr;
import com.beyt.anouncy.common.v1.IdStrList;
import com.beyt.anouncy.common.v1.RegionListPTO;
import com.beyt.anouncy.common.v1.RegionPTO;
import io.grpc.stub.StreamObserver;
import lombok.RequiredArgsConstructor;
import net.devh.boot.grpc.client.inject.GrpcClient;
import net.devh.boot.grpc.server.service.GrpcService;

@GrpcService
@RequiredArgsConstructor
public class RegionProtoController extends RegionServiceGrpc.RegionServiceImplBase {

    @GrpcClient("persist-grpc-server")
    private RegionPersistServiceGrpc.RegionPersistServiceBlockingStub regionPersistServiceBlockingStub;

    @Override
    public void findAllByParentRegionIdIsIn(IdStrList request, StreamObserver<RegionListPTO> responseObserver) {
        responseObserver.onNext(regionPersistServiceBlockingStub.findAllByParentRegionIdIsIn(request));
        responseObserver.onCompleted();
    }

    @Override
    public void findAllByLocationIdIsIn(IdStrList request, StreamObserver<RegionListPTO> responseObserver) {
        responseObserver.onNext(regionPersistServiceBlockingStub.findAllByLocationIdIsIn(request));
        responseObserver.onCompleted();
    }

    @Override
    public void findByLocationId(IdStr request, StreamObserver<RegionPTO> responseObserver) {
        responseObserver.onNext(regionPersistServiceBlockingStub.findByLocationId(request));
        responseObserver.onCompleted();
    }
}
