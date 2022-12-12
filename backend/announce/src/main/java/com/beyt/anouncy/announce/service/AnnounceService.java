package com.beyt.anouncy.announce.service;

import com.beyt.anouncy.announce.dto.AnnounceCreateDTO;
import com.beyt.anouncy.common.aspect.NeedLogin;
import com.beyt.anouncy.common.context.UserContext;
import com.beyt.anouncy.common.entity.neo4j.Announce;
import com.beyt.anouncy.common.entity.neo4j.model.VoteSummary;
import com.beyt.anouncy.common.entity.redis.AnnouncePageItemDTO;
import com.beyt.anouncy.common.entity.redis.AnnounceVoteDTO;
import com.beyt.anouncy.common.exception.ClientErrorException;
import com.beyt.anouncy.common.persist.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.devh.boot.grpc.client.inject.GrpcClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class AnnounceService {
    private final RegionService regionService;


    @GrpcClient("persist-grpc-server")
    private AnnouncePersistServiceGrpc.AnnouncePersistServiceBlockingStub announcePersistServiceBlockingStub;

    @GrpcClient("vote-grpc-server")
    private VoteFetchServiceGrpc.VoteFetchServiceBlockingStub voteFetchServiceBlockingStub;

    @Autowired
    private UserContext userContext;

    @NeedLogin
    public AnnouncePageItemDTO receiveAnnounce(AnnounceCreateDTO dto) {
        AnnouncePTO announce = AnnouncePTO.getDefaultInstance();
        RegionPTO relatedRegion = regionService.getRelatedRegion();

        AnnouncePTO.newBuilder()
                .setAnonymousUser(AnonymousUserPTO.newBuilder().setId(userContext.getAnonymousUserId().toString()).build()) // TODO try it when no user exist
                .setBody(dto.getBody())
                .setBeginRegion(relatedRegion)
                .setCurrentRegion(relatedRegion)
                .build();

        announce = announcePersistServiceBlockingStub.save(announce);

        return AnnouncePageItemDTO.blank(announce);
    }

    @NeedLogin
    public void deleteAnnounce(String announceId) {
        AnnouncePTO announcePTO = announcePersistServiceBlockingStub.findByIdAndAnonymousUserId(AnnounceIdAndAnonymousUserId.newBuilder().setAnnounceId(announceId).setAnonymousUserId(userContext.getAnonymousUserId().toString()).build());

        if (Objects.isNull(announcePTO)) {
            throw new ClientErrorException("announce.not.found");
        }

        announcePersistServiceBlockingStub.delete(announcePTO);
    }

    public AnnouncePageItemDTO getAnnounce(String announceId) {
        AnnouncePTO announcePTO = announcePersistServiceBlockingStub.findById(IdStr.newBuilder().setId(announceId).build());

        if (Objects.isNull(announcePTO)) {
            throw new ClientErrorException("announce.not.found");
        }

        AnnouncePageItemDTO itemDTO = new AnnouncePageItemDTO(announcePTO);
        AnnounceVotePTO announceVotePTO = voteFetchServiceBlockingStub.fetchOne(AnnounceVoteFetchOneRequest.newBuilder().setRegionId(itemDTO.getRegionId()).setAnnounceId(itemDTO.getAnnounceId()).build());

        if (Objects.nonNull(announceVotePTO)) {
            itemDTO.update(announceVotePTO);
        }

        fillCurrentUserVoteIfLogin(itemDTO);

        return itemDTO;
    }

    protected void fillCurrentUserVoteIfLogin(AnnouncePageItemDTO dto) { // TODO test it
        if (Objects.nonNull(userContext.getAnonymousUserId())) {
            VoteSummaryPTO voteSummaries = voteFetchServiceBlockingStub.getVoteSummaries(VoteSummaryRequest.newBuilder().setAnonymousUserId(userContext.getAnonymousUserId().toString()).setRegionId(dto.getRegionId()).addAllAnnounceIdList(List.of(dto.getAnnounceId())).build());
            voteSummaries
                    .stream().findFirst().ifPresent(v -> dto.setCurrentVote(v.value()));
        }
    }

    @NeedLogin
    public Page<AnnouncePageItemDTO> getAnnounceList(Pageable pageable) {
        Page<Announce> announceList = announcePersistServiceBlockingStub.findAllByAnonymousUserId(userContext.getAnonymousUserId(), pageable);
        List<AnnouncePageItemDTO> dtoList = announceList.stream().map(AnnouncePageItemDTO::new).toList();
        Map<String, Set<String>> regionAnnounceIdSetMap = AnnouncePageItemDTO.getRegionAnnounceIdSetMap(dtoList);
        populateVoteCountToAnnounceItemList(dtoList, regionAnnounceIdSetMap);
        populateCurrentVoteToAnnounceList(dtoList, regionAnnounceIdSetMap);
        return new PageImpl<>(dtoList, announceList.getPageable(), announceList.getTotalElements());
    }

    private void populateCurrentVoteToAnnounceList(List<AnnouncePageItemDTO> dtoList, Map<String, Set<String>> regionAnnounceIdSetMap) {
        regionAnnounceIdSetMap.forEach((regionId, announceSet) -> {
            Collection<VoteSummary> voteSummaries = voteFetchServiceBlockingStub.getVoteSummaries(userContext.getAnonymousUserId(), regionId, announceSet);
            dtoList.forEach(dto -> voteSummaries.stream().filter(v -> v.announceId().equals(dto.getAnnounceId())).findFirst()
                    .ifPresent(v -> dto.setCurrentVote(v.value())));
        });
    }

    private void populateVoteCountToAnnounceItemList(List<AnnouncePageItemDTO> dtoList, Map<String, Set<String>> regionAnnounceIdSetMap) {
        Map<String, AnnounceVoteDTO> voteCounts = voteFetchServiceBlockingStub.fetchAll(regionAnnounceIdSetMap);
        voteCounts.forEach((announceId, voteCount) -> {
            dtoList.stream().filter(dto -> announceId.equals(dto.getAnnounceId())).findFirst()
                    .ifPresent(a -> a.update(voteCount));
        });
    }
}
