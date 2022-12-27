package com.beyt.anouncy.location.controller;

import com.beyt.anouncy.common.location.v1.LocationServiceGrpc;
import com.beyt.anouncy.common.util.ProtoUtil;
import com.beyt.anouncy.common.v1.Empty;
import com.beyt.anouncy.common.v1.IdLong;
import com.beyt.anouncy.common.v1.LocationListPTO;
import com.beyt.anouncy.location.entity.Location;
import com.beyt.anouncy.location.service.LocationService;
import com.beyt.anouncy.location.util.LocationProtoUtil;
import io.grpc.stub.StreamObserver;
import lombok.RequiredArgsConstructor;
import net.devh.boot.grpc.server.service.GrpcService;

import java.util.List;

@GrpcService
@RequiredArgsConstructor
public class LocationProtoController extends LocationServiceGrpc.LocationServiceImplBase {
    private final LocationService locationService;

    @Override
    public void findAllParents(IdLong request, StreamObserver<LocationListPTO> responseObserver) {
        Long childId = ProtoUtil.of(request);
        List<Location> parents = locationService.findAllByParentId(childId);
        responseObserver.onNext(LocationProtoUtil.getLocationListPTO(parents));
        responseObserver.onCompleted();
    }

    @Override
    public void findAllCountries(Empty request, StreamObserver<LocationListPTO> responseObserver) {
        List<Location> parents = locationService.findAllCountries();
        responseObserver.onNext(LocationProtoUtil.getLocationListPTO(parents));
        responseObserver.onCompleted();
    }
}
