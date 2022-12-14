package com.beyt.anouncy.persist.helper;

import com.beyt.anouncy.common.exception.DeveloperMistakeException;
import com.beyt.anouncy.common.util.ProtoUtil;
import com.beyt.anouncy.common.v1.VoteListPTO;
import com.beyt.anouncy.common.v1.VoteOptionalPTO;
import com.beyt.anouncy.common.v1.VotePTO;
import com.beyt.anouncy.persist.entity.Vote;
import com.beyt.anouncy.persist.helper.base.PtoConverter;
import lombok.RequiredArgsConstructor;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.stream.StreamSupport;


@Component
@RequiredArgsConstructor
public class VotePtoConverter implements PtoConverter<Vote, VotePTO, VoteListPTO, VoteOptionalPTO> {
    private final AnnouncePtoConverter announcePtoConverter;
    private final AnonymousUserPtoConverter anonymousUserPtoConverter;
    private final RegionPtoConverter regionPtoConverter;

    @Override
    public Vote toEntity(VotePTO pto) {
        if (Objects.isNull(pto)) {
            return null;
        }

        Vote vote = new Vote();
        if (pto.hasId()) {
            vote.setId(pto.getId());
        }
        vote.setValue(pto.getValue());
        vote.setAnnounce(announcePtoConverter.toEntity(pto.getAnnounce()));
        vote.setAnonymousUser(anonymousUserPtoConverter.toEntity(pto.getAnonymousUser()));
        vote.setRegion(regionPtoConverter.toEntity(pto.getRegion()));
        vote.setCreateDate(ProtoUtil.toDate(pto.getCreateDate()));

        return vote;
    }

    @Override
    public List<Vote> toEntityList(VoteListPTO listPTO) {
        if (Objects.isNull(listPTO) || CollectionUtils.isEmpty(listPTO.getVoteListList())) {
            return new ArrayList<>();
        }

        return listPTO.getVoteListList().stream().map(this::toEntity).toList();
    }

    @Override
    public VoteOptionalPTO toOptionalEntity(Optional<Vote> entityOptional) {
        final VoteOptionalPTO.Builder builder = VoteOptionalPTO.newBuilder();
        entityOptional.ifPresent(e -> builder.setVote(toPto(e)));
        return builder.build();
    }

    @Override
    public VotePTO toPto(Vote vote) {
        VotePTO.Builder builder = VotePTO.newBuilder()
                .setId(vote.getId())
                .setValue(vote.getValue())
                .setCreateDate(Optional.ofNullable(vote.getCreateDate()).map(Date::getTime).orElseThrow(() -> new DeveloperMistakeException("Create Date Mistake Vote")));

        Optional.ofNullable(vote.getAnnounce()).map(announcePtoConverter::toPto).ifPresent(builder::setAnnounce);
        Optional.ofNullable(vote.getAnonymousUser()).map(anonymousUserPtoConverter::toPto).ifPresent(builder::setAnonymousUser);
        Optional.ofNullable(vote.getRegion()).map(regionPtoConverter::toPto).ifPresent(builder::setRegion);

        return builder
                .build();
    }

    @Override
    public VoteListPTO toPtoList(Iterable<Vote> entityList) {
        return VoteListPTO.newBuilder().addAllVoteList(StreamSupport.stream(entityList.spliterator(), false).map(this::toPto).toList()).build();
    }
}
