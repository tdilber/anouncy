package com.beyt.anouncy.common.service;

import com.beyt.anouncy.common.context.UserContext;
import com.beyt.anouncy.common.entity.neo4j.Region;
import com.beyt.anouncy.common.exception.ClientErrorException;
import com.beyt.anouncy.common.exception.DeveloperMistakeException;
import com.beyt.anouncy.common.repository.RegionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class RegionService {
    private final RegionRepository repository;
    private final UserContext userContext;

    public Region getRelatedRegion() {
        log.warn("");
        if (CollectionUtils.isEmpty(userContext.getUserJwtModel().getSelectedLocationIdList())) {
            throw new ClientErrorException("user.region.not.selected");
        }

        List<Region> regionList = repository.findAllByLocationIdIsIn(userContext.getUserJwtModel().getSelectedLocationIdList());

        if (regionList.size() != 1) {
            throw new DeveloperMistakeException(String.format("User Have active Region problem Anonymous User Id : %s Region Ids : %s Locations : %s", userContext.getAnonymousUserId(), regionList.stream().map(Region::getId).collect(Collectors.joining(" ,")), userContext.getUserJwtModel().getSelectedLocationIdList().stream().map(Object::toString).collect(Collectors.joining(" ,"))));
        }

        return regionList.get(0);
    }
}
