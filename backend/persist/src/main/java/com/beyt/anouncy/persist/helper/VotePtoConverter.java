package com.beyt.anouncy.persist.helper;

import com.beyt.anouncy.common.exception.DeveloperMistakeException;
import com.beyt.anouncy.common.persist.VoteListPTO;
import com.beyt.anouncy.common.persist.VotePTO;
import com.beyt.anouncy.common.util.ProtoUtil;
import com.beyt.anouncy.persist.entity.Vote;
import com.beyt.anouncy.persist.helper.base.PtoConverter;
import lombok.RequiredArgsConstructor;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.stream.StreamSupport;


@Component
@RequiredArgsConstructor
public class VotePtoConverter implements PtoConverter<Vote, VotePTO, VoteListPTO> {
    private final AnnouncePtoConverter announcePtoConverter;
    private final AnonymousUserPtoConverter anonymousUserPtoConverter;
    private final RegionPtoConverter regionPtoConverter;

    @Override
    public Vote toEntity(VotePTO pto) {
        if (Objects.isNull(pto)) {
            return null;
        }

        Vote vote = new Vote();
        vote.setId(pto.getId());
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
    public VotePTO toPto(Vote vote) {
        return VotePTO.newBuilder()
                .setId(vote.getId())
                .setValue(vote.getValue())
                .setAnnounce(Optional.ofNullable(vote.getAnnounce()).map(announcePtoConverter::toPto).orElse(null))
                .setAnonymousUser(Optional.ofNullable(vote.getAnonymousUser()).map(anonymousUserPtoConverter::toPto).orElse(null))
                .setRegion(Optional.ofNullable(vote.getRegion()).map(regionPtoConverter::toPto).orElse(null))
                .setCreateDate(Optional.ofNullable(vote.getCreateDate()).map(Date::getTime).orElseThrow(() -> new DeveloperMistakeException("Create Date Mistake Vote")))
                .build();
    }

    @Override
    public VoteListPTO toPtoList(Iterable<Vote> entityList) {
        return VoteListPTO.newBuilder().addAllVoteList(StreamSupport.stream(entityList.spliterator(), false).map(this::toPto).toList()).build();
    }
}
