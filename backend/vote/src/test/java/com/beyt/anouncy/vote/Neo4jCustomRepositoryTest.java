package com.beyt.anouncy.vote;

import com.beyt.anouncy.common.entity.neo4j.Announce;
import com.beyt.anouncy.common.entity.neo4j.AnonymousUser;
import com.beyt.anouncy.common.entity.neo4j.Region;
import com.beyt.anouncy.common.entity.neo4j.Vote;
import com.beyt.anouncy.common.entity.neo4j.enumeration.RegionStatus;
import com.beyt.anouncy.common.entity.neo4j.enumeration.RegionType;
import com.beyt.anouncy.common.repository.*;
import org.assertj.core.api.Assertions;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Date;
import java.util.UUID;

@SpringBootTest(classes = VoteApplication.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = EmbeddedNeo4jConfig.class)
public class Neo4jCustomRepositoryTest extends EmbeddedNeo4jConfig {

    @Autowired
    private Neo4jCustomRepository neo4jCustomRepository;

    @Autowired
    private VoteRepository voteRepository;

    @Autowired
    private RegionRepository regionRepository;

    @Autowired
    private AnnounceRepository announceRepository;

    @Autowired
    private AnonymousUserRepository anonymousUserRepository;

    private Announce announce1;
    private Announce announce2;
    private Region region;
    private AnonymousUser anonymousUser;

    @BeforeAll
    protected void beforeAll() {
        region = createRegion();
//        voteRepository.deleteAll();
//        regionRepository.deleteAll();
//        announceRepository.deleteAll();
//        anonymousUserRepository.deleteAll();

        region = regionRepository.save(region);

        UUID anonymousUserId = UUID.randomUUID();

        anonymousUser = anonymousUserRepository.save(new AnonymousUser(anonymousUserId));

        announce1 = createAnnounce(region, anonymousUser);
        announce2 = createAnnounce(region, anonymousUser);
        announce1 = announceRepository.save(announce1);
        announce2 = announceRepository.save(announce2);

        voteRepository.save(createVote(anonymousUser, announce1));
        voteRepository.save(createVote(anonymousUser, announce1));
        voteRepository.save(createVote(anonymousUser, announce1));
        voteRepository.save(createVote(anonymousUser, announce2));
        voteRepository.save(createVote(anonymousUser, announce2));
        voteRepository.save(createVote(anonymousUser, announce2));
        voteRepository.save(createVote(anonymousUser, announce2));
//        var voteCountByAnnounceId = neo4jCustomRepository.getVoteCount(announce.getId());
//        var asd = neo4jCustomRepository.getVoteSummaries(anonymousUserId, List.of(announce.getId()));
    }

    @NotNull
    private Region createRegion() {
        Region region = new Region();
        region.setName("Test");
        region.setOrdinal(0);
        region.setLatitude(0.0D);
        region.setLongitude(0.0D);
        region.setLocationId(0L);
        region.setType(RegionType.COUNTRY);
        region.setStatus(RegionStatus.ACTIVE);
        return region;
    }

    @Test
    void getVoteCount() {
        var count1 = neo4jCustomRepository.getVoteCount(region.getId(), announce1.getId());
        var count2 = neo4jCustomRepository.getVoteCount(region.getId(), announce2.getId());
        Assertions.assertThat(3).isEqualTo(count1);
        Assertions.assertThat(4).isEqualTo(count2);
    }

    private Vote createVote(AnonymousUser anonymousUser, Announce announce) {
        Vote vote = new Vote();
        vote.setAnonymousUser(anonymousUser);
        vote.setValue(true);
        vote.setAnnounce(announce);
        vote.setCreateDate(new Date());
        return vote;
    }

    private Announce createAnnounce(Region region, AnonymousUser anonymousUser) {
        Announce announce = new Announce();
        announce.setAnonymousUser(anonymousUser);
        announce.setBody("Test ");
        announce.setBeginRegion(region);
        announce.setCurrentRegion(region);
        announce.setCreateDate(new Date());
        return announce;
    }
}
