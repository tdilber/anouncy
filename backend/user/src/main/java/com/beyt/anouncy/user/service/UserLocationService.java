package com.beyt.anouncy.user.service;

import com.beyt.anouncy.common.aspect.NeedLogin;
import com.beyt.anouncy.common.context.UserContext;
import com.beyt.anouncy.common.exception.ClientErrorException;
import com.beyt.anouncy.common.exception.DeveloperMistakeException;
import com.beyt.anouncy.common.location.v1.LocationServiceGrpc;
import com.beyt.anouncy.common.util.ProtoUtil;
import com.beyt.anouncy.common.v1.LocationListPTO;
import com.beyt.anouncy.common.v1.LocationPTO;
import com.beyt.anouncy.common.v1.RegionTypePTO;
import com.beyt.anouncy.user.entity.User;
import com.beyt.anouncy.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.devh.boot.grpc.client.inject.GrpcClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserLocationService {
    private final UserRepository userRepository;
    private final UserService userService;

    @Autowired
    private UserContext userContext;

    @GrpcClient("location-grpc-server")
    private LocationServiceGrpc.LocationServiceBlockingStub locationServiceBlockingStub;

    @NeedLogin
    @Transactional
    public UserService.UserJwtResponse updateOrSaveLocation(Long locationId) {
        LocationListPTO listPTO = locationServiceBlockingStub.findAllParents(ProtoUtil.toIdLong(locationId));

        List<LocationPTO> locationList = listPTO.getLocationListList();

        if (locationList.stream().filter(l -> l.getType().equals(RegionTypePTO.COUNTRY)).count() != 1 ||
                locationList.stream().filter(l -> l.getType().equals(RegionTypePTO.CITY)).count() != 1 ||
                locationList.stream().filter(l -> l.getType().equals(RegionTypePTO.COUNTY)).count() != 1) {
            throw new ClientErrorException("user.location.not.valid");
        }

        String locationIdList = locationList.stream().map(LocationPTO::getId).sorted().map(Object::toString).collect(Collectors.joining(User.LOCATION_IDS_SEPARATOR));
        userRepository.updateLocationIdList(userContext.getUserId(), locationIdList);
        User user = userRepository.findById(userContext.getUserId()).orElseThrow(DeveloperMistakeException::new);
        return userService.createUserJwtResponse(user, userContext.getAnonymousUserId());
    }
}
