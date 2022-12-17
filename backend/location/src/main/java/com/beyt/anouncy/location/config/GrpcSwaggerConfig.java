package com.beyt.anouncy.location.config;

import com.beyt.anouncy.common.persist.AnnouncePersistServiceGrpc;
import com.beyt.anouncy.common.persist.AnonymousUserPersistServiceGrpc;
import com.beyt.anouncy.common.persist.RegionPersistServiceGrpc;
import com.beyt.anouncy.common.persist.VotePersistServiceGrpc;
import com.beyt.doc.grpc.service.GrpcRepositoryCreator;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Slf4j
@Configuration
public class GrpcSwaggerConfig {

    @Bean
    public GrpcRepositoryCreator grpcRepositoryCreator() {
        return new GrpcRepositoryCreator();
    }

    @Bean
    public Object createControllerVote(GrpcRepositoryCreator grpcRepositoryCreator) {
        return grpcRepositoryCreator.createGrpcController(VotePersistServiceGrpc.VotePersistServiceBlockingStub.class, "persist-grpc-server", "vote");
    }

    @Bean
    public Object createControllerRegion(GrpcRepositoryCreator grpcRepositoryCreator) {
        return grpcRepositoryCreator.createGrpcController(RegionPersistServiceGrpc.RegionPersistServiceBlockingStub.class, "persist-grpc-server", "region");
    }

    @Bean
    public Object createControllerAnonymousUser(GrpcRepositoryCreator grpcRepositoryCreator) {
        return grpcRepositoryCreator.createGrpcController(AnonymousUserPersistServiceGrpc.AnonymousUserPersistServiceBlockingStub.class, "persist-grpc-server", "anonymous-user");
    }

    @Bean
    public Object createControllerAnnounce(GrpcRepositoryCreator grpcRepositoryCreator) {
        return grpcRepositoryCreator.createGrpcController(AnnouncePersistServiceGrpc.AnnouncePersistServiceBlockingStub.class, "persist-grpc-server", "announce");
    }

}
