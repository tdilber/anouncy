package com.beyt.anouncy.region.service;

import com.beyt.anouncy.common.persist.RegionListPTO;
import com.beyt.anouncy.common.persist.RegionPersistServiceGrpc;
import com.beyt.anouncy.common.util.ProtoUtil;
import com.beyt.anouncy.region.dto.RegionDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.devh.boot.grpc.client.inject.GrpcClient;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class RegionService {

    @GrpcClient("persist-grpc-server")
    private RegionPersistServiceGrpc.RegionPersistServiceBlockingStub regionPersistServiceBlockingStub;

    public RegionDTO findAllByParentId(String parentRegionId) {
        RegionListPTO regionListPTO = regionPersistServiceBlockingStub.findAllByParentRegionIdIsIn(ProtoUtil.toIdStrList(List.of(parentRegionId)));

        return new RegionDTO(regionListPTO.getRegionListList());
    }
}
