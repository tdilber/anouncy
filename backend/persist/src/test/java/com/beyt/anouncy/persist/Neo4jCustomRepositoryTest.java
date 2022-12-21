package com.beyt.anouncy.persist;

import com.beyt.anouncy.common.entity.enumeration.RegionStatus;
import com.beyt.anouncy.common.entity.enumeration.RegionType;
import com.beyt.anouncy.persist.entity.Announce;
import com.beyt.anouncy.persist.entity.AnonymousUser;
import com.beyt.anouncy.persist.entity.Region;
import com.beyt.anouncy.persist.entity.Vote;
import com.beyt.anouncy.persist.entity.model.VoteCount;
import com.beyt.anouncy.persist.repository.*;
import org.assertj.core.api.Assertions;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

import java.util.Date;
import java.util.List;

//@SpringBootTest(classes = PersistApplication.class)
//@TestInstance(TestInstance.Lifecycle.PER_CLASS)
//@ExtendWith(SpringExtension.class)
//@ContextConfiguration(classes = EmbeddedNeo4jConfig.class)
//public class Neo4jCustomRepositoryTest extends EmbeddedNeo4jConfig {
@SpringBootTest(classes = PersistApplication.class, properties = {
        "server.port=8086",
        "grpc.server.port=9096"
})
@SpringJUnitConfig(classes = {MyServiceIntegrationTestConfiguration.class})
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
//@DirtiesContext
public class Neo4jCustomRepositoryTest {

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
        voteRepository.deleteAll();
        regionRepository.deleteAll();
        announceRepository.deleteAll();
        anonymousUserRepository.deleteAll();

        region = createRegion();
        region = regionRepository.save(region);


        anonymousUser = anonymousUserRepository.save(new AnonymousUser());

        announce1 = createAnnounce(region, anonymousUser);
        announce2 = createAnnounce(region, anonymousUser);
        announce1 = announceRepository.save(announce1);
        announce2 = announceRepository.save(announce2);

        voteRepository.save(createVote(anonymousUser, announce1, region));
        voteRepository.save(createVote(anonymousUser, announce1, region));
        voteRepository.save(createVote(anonymousUser, announce1, region));
        voteRepository.save(createVote(anonymousUser, announce2, region));
        voteRepository.save(createVote(anonymousUser, announce2, region));
        voteRepository.save(createVote(anonymousUser, announce2, region));
        voteRepository.save(createVote(anonymousUser, announce2, region));
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
    void getVoteCount(@Autowired Neo4jCustomRepository neo4jCustomRepository) {
        var count1 = neo4jCustomRepository.getVoteCount(region.getId(), announce1.getId());
        var count2 = neo4jCustomRepository.getVoteCount(region.getId(), announce2.getId());

        Assertions.assertThat(count1).isPresent();
        Assertions.assertThat(count2).isPresent();
        Assertions.assertThat(3L).isEqualTo(count1.get().yes());
        Assertions.assertThat(4L).isEqualTo(count2.get().yes());
    }

    @Test
        // NOTE: This test not working because getAllVoteCounts throw nullPointer with no reason. I think this is Spring Boot 3.0.0 error. I think the bug fixing after upgrade the spring version.
    void getAllVoteCounts(@Autowired Neo4jCustomRepository neo4jCustomRepository) {
        var countList = neo4jCustomRepository.getAllVoteCounts(region.getId(), List.of(announce1.getId(), announce2.getId()));

        Assertions.assertThat(countList).hasSize(2);
        Assertions.assertThat(3L).isEqualTo(countList.stream().filter(c -> c.announceId().equals(announce1.getId())).findFirst().map(VoteCount::yes).orElse(null));
        Assertions.assertThat(4L).isEqualTo(countList.stream().filter(c -> c.announceId().equals(announce2.getId())).findFirst().map(VoteCount::yes).orElse(null));
    }

    private Vote createVote(AnonymousUser anonymousUser, Announce announce, Region region) {
        Vote vote = new Vote();
        vote.setAnonymousUser(anonymousUser);
        vote.setValue(true);
        vote.setAnnounce(announce);
        vote.setCreateDate(new Date());
        vote.setRegion(region);
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
