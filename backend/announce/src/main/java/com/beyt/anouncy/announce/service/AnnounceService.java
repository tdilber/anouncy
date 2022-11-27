package com.beyt.anouncy.announce.service;

import com.beyt.anouncy.announce.dto.AnnounceCreateDTO;
import com.beyt.anouncy.common.aspect.NeedLogin;
import com.beyt.anouncy.common.context.UserContext;
import com.beyt.anouncy.common.entity.neo4j.Announce;
import com.beyt.anouncy.common.entity.neo4j.Region;
import com.beyt.anouncy.common.repository.AnnounceRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class AnnounceService {
    private final AnnounceRepository announceRepository;
    private final UserContext userContext;

    @NeedLogin
    public void receiveAnnounce(AnnounceCreateDTO dto) {
        Announce announce = new Announce();
        announce.setAnonymousUserId(userContext.getAnonymousUserId());
        announce.setBody(dto.getBody());
        announce.setBeginRegion(new Region());
        announce.setCurrentRegion(new Region());
    }

    public Region getRelatedRegion() {

    }
}
