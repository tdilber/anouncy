package com.beyt.anouncy.announce.service;

import com.beyt.anouncy.common.context.UserContext;
import com.beyt.anouncy.common.exception.ClientErrorException;
import com.beyt.anouncy.common.exception.DeveloperMistakeException;
import com.beyt.anouncy.common.region.v1.RegionServiceGrpc;
import com.beyt.anouncy.common.util.ProtoUtil;
import com.beyt.anouncy.common.v1.RegionListPTO;
import com.beyt.anouncy.common.v1.RegionPTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.devh.boot.grpc.client.inject.GrpcClient;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Service;

import java.util.Objects;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class RegionService {
    private final UserContext userContext;

    @GrpcClient("region-grpc-server")
    private RegionServiceGrpc.RegionServiceBlockingStub regionServiceBlockingStub;

    public RegionPTO getRelatedRegion() {
        log.warn("");
        if (CollectionUtils.isEmpty(userContext.getUserJwtModel().getSelectedLocationIdList())) {
            throw new ClientErrorException("user.region.not.selected");
        }

        RegionListPTO regionListPTO = regionServiceBlockingStub.findAllByLocationIdIsIn(ProtoUtil.toIdStrList(userContext.getUserJwtModel().getSelectedLocationIdList().stream().map(Objects::toString).toList()));

        if (regionListPTO.getRegionListList().size() != 1) {
            throw new DeveloperMistakeException(String.format("User Have active Region problem Anonymous User Id : %s Region Ids : %s Locations : %s", userContext.getAnonymousUserId(), regionListPTO.getRegionListList().stream().map(RegionPTO::getId).collect(Collectors.joining(" ,")), userContext.getUserJwtModel().getSelectedLocationIdList().stream().map(Object::toString).collect(Collectors.joining(" ,"))));
        }

        return regionListPTO.getRegionListList().get(0);
    }
}
