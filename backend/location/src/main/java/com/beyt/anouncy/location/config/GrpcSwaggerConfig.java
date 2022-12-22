package com.beyt.anouncy.location.config;

import com.beyt.anouncy.common.persist.v1.AnnouncePersistServiceGrpc;
import com.beyt.anouncy.common.persist.v1.AnonymousUserPersistServiceGrpc;
import com.beyt.anouncy.common.persist.v1.RegionPersistServiceGrpc;
import com.beyt.anouncy.common.persist.v1.VotePersistServiceGrpc;
import com.beyt.anouncy.common.search.v1.AnnounceSearchServiceGrpc;
import com.beyt.anouncy.common.vote.v1.VoteFetchServiceGrpc;
import com.beyt.doc.grpc.service.GrpcRepositoryCreator;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Slf4j
@Configuration
@ConditionalOnProperty("springdoc.grpc.enable")
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

    @Bean
    public Object createControllerSearch(GrpcRepositoryCreator grpcRepositoryCreator) {
        return grpcRepositoryCreator.createGrpcController(AnnounceSearchServiceGrpc.AnnounceSearchServiceBlockingStub.class, "search-grpc-server", "search");
    }

    @Bean
    public Object createControllerVoteService(GrpcRepositoryCreator grpcRepositoryCreator) {
        return grpcRepositoryCreator.createGrpcController(VoteFetchServiceGrpc.VoteFetchServiceBlockingStub.class, "vote-grpc-server", "vote-service");
    }
}
