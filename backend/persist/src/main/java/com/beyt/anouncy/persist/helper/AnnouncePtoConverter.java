package com.beyt.anouncy.persist.helper;

import com.beyt.anouncy.common.exception.DeveloperMistakeException;
import com.beyt.anouncy.common.util.ProtoUtil;
import com.beyt.anouncy.common.v1.AnnounceListPTO;
import com.beyt.anouncy.common.v1.AnnounceOptionalPTO;
import com.beyt.anouncy.common.v1.AnnouncePTO;
import com.beyt.anouncy.persist.entity.Announce;
import com.beyt.anouncy.persist.helper.base.PtoConverter;
import lombok.RequiredArgsConstructor;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.stream.StreamSupport;

@Component
@RequiredArgsConstructor
public class AnnouncePtoConverter implements PtoConverter<Announce, AnnouncePTO, AnnounceListPTO, AnnounceOptionalPTO> {
    private final AnonymousUserPtoConverter anonymousUserPtoConverter;
    private final RegionPtoConverter regionPtoConverter;


    @Override
    public Announce toEntity(AnnouncePTO pto) {
        if (Objects.isNull(pto)) {
            return null;
        }
        Announce announce = new Announce();
        if (pto.hasId()) {
            announce.setId(pto.getId());
        }
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
    public AnnounceOptionalPTO toOptionalEntity(Optional<Announce> entityOptional) {
        final AnnounceOptionalPTO.Builder builder = AnnounceOptionalPTO.newBuilder();
        entityOptional.ifPresent(e -> builder.setAnnounce(toPto(e)));
        return builder.build();
    }

    @Override
    public AnnouncePTO toPto(Announce announce) {
        AnnouncePTO.Builder builder = AnnouncePTO.newBuilder()
                .setId(announce.getId())
                .setBody(announce.getBody())
                .setCreateDate(Optional.ofNullable(announce.getCreateDate()).map(Date::getTime).orElseThrow(() -> new DeveloperMistakeException("Create Date Mistake Announce")));

        Optional.ofNullable(announce.getAnonymousUser()).map(anonymousUserPtoConverter::toPto).ifPresent(builder::setAnonymousUser);
        Optional.ofNullable(announce.getBeginRegion()).map(regionPtoConverter::toPto).ifPresent(builder::setBeginRegion);
        Optional.ofNullable(announce.getCurrentRegion()).map(regionPtoConverter::toPto).ifPresent(builder::setCurrentRegion);

        return builder
                .build();
    }

    @Override
    public AnnounceListPTO toPtoList(Iterable<Announce> entityList) {
        return AnnounceListPTO.newBuilder().addAllAnnounceList(StreamSupport.stream(entityList.spliterator(), false).map(this::toPto).toList()).build();
    }
}
