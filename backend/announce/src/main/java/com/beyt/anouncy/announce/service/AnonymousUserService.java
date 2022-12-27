package com.beyt.anouncy.announce.service;

import com.beyt.anouncy.common.persist.v1.AnonymousUserPersistServiceGrpc;
import com.beyt.anouncy.common.util.ProtoUtil;
import com.beyt.anouncy.common.v1.AnonymousUserOptionalPTO;
import com.beyt.anouncy.common.v1.AnonymousUserPTO;
import com.beyt.anouncy.common.v1.IdStr;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.devh.boot.grpc.client.inject.GrpcClient;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class AnonymousUserService {

    @GrpcClient("persist-grpc-server")
    private AnonymousUserPersistServiceGrpc.AnonymousUserPersistServiceBlockingStub persistServiceBlockingStub;

    public AnonymousUserPTO getAnonymousUser(UUID anonymousUserId) {
        final IdStr anonymousUserIdPto = ProtoUtil.toIdStr(anonymousUserId.toString());

        AnonymousUserPTO anonymousUserPTO;
        AnonymousUserOptionalPTO anonymousUserOptPTO = persistServiceBlockingStub.findById(anonymousUserIdPto);
        anonymousUserPTO = anonymousUserOptPTO.getAnonymousUser();

        if (!anonymousUserOptPTO.hasAnonymousUser()) {
            anonymousUserPTO = persistServiceBlockingStub.save(AnonymousUserPTO.newBuilder().setId(anonymousUserId.toString()).build());
        }

        return anonymousUserPTO;
    }
}
