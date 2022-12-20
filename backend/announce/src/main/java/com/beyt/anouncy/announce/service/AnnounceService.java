package com.beyt.anouncy.announce.service;

import com.beyt.anouncy.announce.dto.AnnounceCreateDTO;
import com.beyt.anouncy.common.aspect.NeedLogin;
import com.beyt.anouncy.common.context.UserContext;
import com.beyt.anouncy.common.entity.redis.AnnouncePageItemDTO;
import com.beyt.anouncy.common.exception.ClientErrorException;
import com.beyt.anouncy.common.persist.v1.AnnouncePersistServiceGrpc;
import com.beyt.anouncy.common.v1.*;
import com.beyt.anouncy.common.vote.v1.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.devh.boot.grpc.client.inject.GrpcClient;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

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
            VoteSummaryRequest ptoParam = VoteSummaryRequest.newBuilder().setAnonymousUserId(userContext.getAnonymousUserId().toString()).setRegionId(dto.getRegionId()).addAllAnnounceIdList(List.of(dto.getAnnounceId())).build();
            VoteSummaryListPTO voteSummaries = voteFetchServiceBlockingStub.getVoteSummaries(ptoParam);
            voteSummaries.getVoteSummaryListList().stream().findFirst().ifPresent(v -> dto.setCurrentVote(v.getValue()));
        }
    }

    @NeedLogin
    public Page<AnnouncePageItemDTO> getAnnounceList(Pageable pageable) {
        var ptoParam = IdWithPageable.newBuilder().setPageable(PageablePTO.newBuilder().setPage(pageable.getPageNumber()).setSize(pageable.getPageSize()).build()).setId(userContext.getAnonymousUserId().toString()).build();
        AnnouncePagePTO announceList = announcePersistServiceBlockingStub.findAllByAnonymousUserId(ptoParam);
        List<AnnouncePageItemDTO> dtoList = announceList.getAnnounceListList().stream().map(AnnouncePageItemDTO::new).toList();
        Map<String, Set<String>> regionAnnounceIdSetMap = AnnouncePageItemDTO.getRegionAnnounceIdSetMap(dtoList);
        populateVoteCountToAnnounceItemList(dtoList, regionAnnounceIdSetMap);
        populateCurrentVoteToAnnounceList(dtoList, regionAnnounceIdSetMap);
        return new PageImpl<>(dtoList, pageable, announceList.getTotalElement());
    }

    private void populateCurrentVoteToAnnounceList(List<AnnouncePageItemDTO> dtoList, Map<String, Set<String>> regionAnnounceIdSetMap) {
        regionAnnounceIdSetMap.forEach((regionId, announceSet) -> {
            VoteSummaryListPTO voteSummaries = voteFetchServiceBlockingStub.getVoteSummaries(VoteSummaryRequest.newBuilder().setAnonymousUserId(userContext.getAnonymousUserId().toString()).addAllAnnounceIdList(announceSet).setRegionId(regionId).build());
            if (Objects.nonNull(voteSummaries) && CollectionUtils.isNotEmpty(voteSummaries.getVoteSummaryListList())) {
                dtoList.forEach(dto -> voteSummaries.getVoteSummaryListList().stream().filter(v -> v.getAnnounceId().equals(dto.getAnnounceId())).findFirst()
                        .ifPresent(v -> dto.setCurrentVote(v.getValue())));
            }
        });
    }

    private void populateVoteCountToAnnounceItemList(List<AnnouncePageItemDTO> dtoList, Map<String, Set<String>> regionAnnounceIdSetMap) {
        AnnounceVoteFetchAllRequest request = AnnounceVoteFetchAllRequest.newBuilder().addAllMap(regionAnnounceIdSetMap.entrySet().stream().map(e -> AnnounceVoteFetchAllRequestItem.newBuilder().addAllAnnounceIdList(e.getValue()).setRegionId(e.getKey()).build()).toList()).build();
        AnnounceVoteListPTO voteCounts = voteFetchServiceBlockingStub.fetchAll(request);
        if (Objects.nonNull(voteCounts) && CollectionUtils.isNotEmpty(voteCounts.getVoteListList())) {
            voteCounts.getVoteListList().forEach(voteCountPTO -> {
                dtoList.stream().filter(dto -> voteCountPTO.getAnnounceId().equals(dto.getAnnounceId())).findFirst()
                        .ifPresent(a -> a.update(voteCountPTO));
            });
        }
    }
}
