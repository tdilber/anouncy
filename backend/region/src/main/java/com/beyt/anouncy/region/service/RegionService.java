package com.beyt.anouncy.region.service;

import com.beyt.anouncy.common.location.v1.LocationServiceGrpc;
import com.beyt.anouncy.common.persist.v1.RegionPersistServiceGrpc;
import com.beyt.anouncy.common.util.ProtoUtil;
import com.beyt.anouncy.common.v1.*;
import com.beyt.anouncy.region.dto.RegionDTO;
import com.beyt.anouncy.region.util.RegionProtoUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.devh.boot.grpc.client.inject.GrpcClient;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class RegionService implements ApplicationRunner {

    @GrpcClient("persist-grpc-server")
    private RegionPersistServiceGrpc.RegionPersistServiceBlockingStub regionPersistServiceBlockingStub;

    @GrpcClient("location-grpc-server")
    private LocationServiceGrpc.LocationServiceBlockingStub locationServiceBlockingStub;

    public RegionDTO findAllByParentId(String parentRegionId) {
        RegionListPTO regionListPTO = regionPersistServiceBlockingStub.findAllByParentRegionIdIsIn(ProtoUtil.toIdStrList(List.of(parentRegionId)));

        return new RegionDTO(regionListPTO.getRegionListList());
    }


//    @Scheduled(fixedDelay = 10000L, initialDelay = 3000L)
//    protected void regionSchedule() {
// TODO
//    }

    @Override
    public void run(ApplicationArguments args) throws Exception {
        Count count = regionPersistServiceBlockingStub.count(Empty.newBuilder().build());
        if (count.getCount() != 0) {
            return;
        }

        log.info("Default Regions Not Found Then New Regions Saving Started!");
        LocationListPTO allCountries = locationServiceBlockingStub.findAllCountries(ProtoUtil.EMPTY);

        List<RegionPTO> regionPTOS = allCountries.getLocationListList().stream().map(RegionProtoUtil::to).toList();

        regionPersistServiceBlockingStub.saveAll(RegionListPTO.newBuilder().addAllRegionList(regionPTOS).build());
        log.info("Default Regions Saved! count : {} countries : {}", regionPTOS.size(), regionPTOS.stream().map(RegionPTO::getName).collect(Collectors.joining(", ")));
    }

}
