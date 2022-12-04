package com.beyt.anouncy.announce.service;

import com.beyt.anouncy.announce.dto.AnnounceCreateDTO;
import com.beyt.anouncy.common.aspect.NeedLogin;
import com.beyt.anouncy.common.context.UserContext;
import com.beyt.anouncy.common.entity.neo4j.Announce;
import com.beyt.anouncy.common.entity.neo4j.Region;
import com.beyt.anouncy.common.entity.neo4j.model.VoteSummary;
import com.beyt.anouncy.common.entity.redis.AnnouncePageItemDTO;
import com.beyt.anouncy.common.entity.redis.AnnounceVoteDTO;
import com.beyt.anouncy.common.exception.ClientErrorException;
import com.beyt.anouncy.common.repository.AnnounceRepository;
import com.beyt.anouncy.common.repository.Neo4jCustomRepository;
import com.beyt.anouncy.common.service.AnonymousUserService;
import com.beyt.anouncy.common.service.RegionService;
import com.beyt.anouncy.common.service.VoteService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class AnnounceService {
    private final AnnounceRepository announceRepository;
    private final RegionService regionService;
    private final AnonymousUserService anonymousUserService;
    private final Neo4jCustomRepository neo4jCustomRepository;
    private final UserContext userContext;
    private final VoteService voteService;

    @NeedLogin
    public AnnouncePageItemDTO receiveAnnounce(AnnounceCreateDTO dto) {
        Announce announce = new Announce();
        announce.setAnonymousUser(anonymousUserService.getCurrentUser());
        announce.setBody(dto.getBody());
        Region relatedRegion = regionService.getRelatedRegion();
        announce.setBeginRegion(relatedRegion);
        announce.setCurrentRegion(relatedRegion);

        announceRepository.save(announce);

        return AnnouncePageItemDTO.blank(announce);
    }

    @NeedLogin
    public void deleteAnnounce(String announceId) {
        Optional<Announce> announceOptional = announceRepository.findByIdAndAnonymousUserId(announceId, userContext.getAnonymousUserId());

        if (announceOptional.isEmpty()) {
            throw new ClientErrorException("announce.not.found");
        }

        announceRepository.delete(announceOptional.get());
    }

    public AnnouncePageItemDTO getAnnounce(String announceId) {
        Optional<Announce> announceOptional = announceRepository.findById(announceId);

        if (announceOptional.isEmpty()) {
            throw new ClientErrorException("announce.not.found");
        }

        AnnouncePageItemDTO itemDTO = new AnnouncePageItemDTO(announceOptional.get());
        voteService.fetchOne(itemDTO.getRegionId(), itemDTO.getAnnounceId()).ifPresent(itemDTO::update);
        fillCurrentUserVoteIfLogin(itemDTO);

        return itemDTO;
    }

    protected void fillCurrentUserVoteIfLogin(AnnouncePageItemDTO dto) { // TODO test it
        if (Objects.nonNull(userContext.getAnonymousUserId())) {
            neo4jCustomRepository.getVoteSummaries(userContext.getAnonymousUserId(), dto.getRegionId(), List.of(dto.getAnnounceId()))
                    .stream().findFirst().ifPresent(v -> dto.setCurrentVote(v.value()));
        }
    }

    @NeedLogin
    public Page<AnnouncePageItemDTO> getAnnounceList(Pageable pageable) {
        Page<Announce> announceList = announceRepository.findAllByAnonymousUserId(userContext.getAnonymousUserId(), pageable);
        List<AnnouncePageItemDTO> dtoList = announceList.stream().map(AnnouncePageItemDTO::new).toList();
        Map<String, Set<String>> regionAnnounceIdSetMap = AnnouncePageItemDTO.getRegionAnnounceIdSetMap(dtoList);
        populateVoteCountToAnnounceItemList(dtoList, regionAnnounceIdSetMap);
        populateCurrentVoteToAnnounceList(dtoList, regionAnnounceIdSetMap);
        return new PageImpl<>(dtoList, announceList.getPageable(), announceList.getTotalElements());
    }

    private void populateCurrentVoteToAnnounceList(List<AnnouncePageItemDTO> dtoList, Map<String, Set<String>> regionAnnounceIdSetMap) {
        regionAnnounceIdSetMap.forEach((regionId, announceSet) -> {
            Collection<VoteSummary> voteSummaries = neo4jCustomRepository.getVoteSummaries(userContext.getAnonymousUserId(), regionId, announceSet);
            dtoList.forEach(dto -> voteSummaries.stream().filter(v -> v.announceId().equals(dto.getAnnounceId())).findFirst()
                    .ifPresent(v -> dto.setCurrentVote(v.value())));
        });
    }

    private void populateVoteCountToAnnounceItemList(List<AnnouncePageItemDTO> dtoList, Map<String, Set<String>> regionAnnounceIdSetMap) {
        Map<String, AnnounceVoteDTO> voteCounts = voteService.fetch(regionAnnounceIdSetMap);
        voteCounts.forEach((announceId, voteCount) -> {
            dtoList.stream().filter(dto -> announceId.equals(dto.getAnnounceId())).findFirst()
                    .ifPresent(a -> a.update(voteCount));
        });
    }
}
