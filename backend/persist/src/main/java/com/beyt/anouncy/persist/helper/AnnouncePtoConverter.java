package com.beyt.anouncy.persist.helper;

import com.beyt.anouncy.common.exception.DeveloperMistakeException;
import com.beyt.anouncy.common.persist.AnnounceListPTO;
import com.beyt.anouncy.common.persist.AnnouncePTO;
import com.beyt.anouncy.common.util.ProtoUtil;
import com.beyt.anouncy.persist.entity.Announce;
import com.beyt.anouncy.persist.helper.base.PtoConverter;
import lombok.RequiredArgsConstructor;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.stream.StreamSupport;

@Component
@RequiredArgsConstructor
public class AnnouncePtoConverter implements PtoConverter<Announce, AnnouncePTO, AnnounceListPTO> {
    private final AnonymousUserPtoConverter anonymousUserPtoConverter;
    private final RegionPtoConverter regionPtoConverter;


    @Override
    public Announce toEntity(AnnouncePTO pto) {
        if (Objects.isNull(pto)) {
            return null;
        }
        Announce announce = new Announce();
        announce.setId(pto.getId());
        announce.setBody(pto.getBody());
        announce.setAnonymousUser(anonymousUserPtoConverter.toEntity(pto.getAnonymousUser()));
        announce.setBeginRegion(regionPtoConverter.toEntity(pto.getBeginRegion()));
        announce.setCurrentRegion(regionPtoConverter.toEntity(pto.getCurrentRegion()));
        announce.setCreateDate(ProtoUtil.toDate(pto.getCreateDate()));

        return announce;
    }

    @Override
    public List<Announce> toEntityList(AnnounceListPTO listPTO) {
        if (Objects.isNull(listPTO) || CollectionUtils.isEmpty(listPTO.getAnnounceListList())) {
            return new ArrayList<>();
        }

        return listPTO.getAnnounceListList().stream().map(this::toEntity).toList();
    }

    @Override
    public AnnouncePTO toPto(Announce announce) {
        return AnnouncePTO.newBuilder()
                .setId(announce.getId())
                .setBody(announce.getBody())
                .setAnonymousUser(Optional.ofNullable(announce.getAnonymousUser()).map(anonymousUserPtoConverter::toPto).orElse(null))
                .setBeginRegion(Optional.ofNullable(announce.getBeginRegion()).map(regionPtoConverter::toPto).orElse(null))
                .setCurrentRegion(Optional.ofNullable(announce.getCurrentRegion()).map(regionPtoConverter::toPto).orElse(null))
                .setCreateDate(Optional.ofNullable(announce.getCreateDate()).map(Date::getTime).orElseThrow(() -> new DeveloperMistakeException("Create Date Mistake Announce")))
                .build();
    }

    @Override
    public AnnounceListPTO toPtoList(Iterable<Announce> entityList) {
        return AnnounceListPTO.newBuilder().addAllAnnounceList(StreamSupport.stream(entityList.spliterator(), false).map(this::toPto).toList()).build();
    }
}
