package com.beyt.anouncy.search.converter;

import com.beyt.anouncy.common.exception.DeveloperMistakeException;
import com.beyt.anouncy.common.util.ProtoUtil;
import com.beyt.anouncy.common.v1.AnnounceListPTO;
import com.beyt.anouncy.common.v1.AnnounceOptionalPTO;
import com.beyt.anouncy.common.v1.AnnouncePTO;
import com.beyt.anouncy.common.v1.AnonymousUserPTO;
import com.beyt.anouncy.search.entity.AnnounceSearchItem;
import lombok.RequiredArgsConstructor;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.stream.StreamSupport;

@Component
@RequiredArgsConstructor
public class AnnouncePtoConverter {
    private final RegionPtoConverter regionPtoConverter;


    public AnnounceSearchItem toEntity(AnnouncePTO pto) {
        if (Objects.isNull(pto)) {
            return null;
        }
        AnnounceSearchItem announce = new AnnounceSearchItem();
        announce.setId(pto.getId());
        announce.setBody(pto.getBody());
        announce.setAnonymousUserId(pto.getAnonymousUser().getId());
        announce.setBeginRegion(regionPtoConverter.toEntity(pto.getBeginRegion()));
        announce.setCreateDate(ProtoUtil.toDate(pto.getCreateDate()));

        return announce;
    }

    public List<AnnounceSearchItem> toEntityList(AnnounceListPTO listPTO) {
        if (Objects.isNull(listPTO) || CollectionUtils.isEmpty(listPTO.getAnnounceListList())) {
            return new ArrayList<>();
        }

        return listPTO.getAnnounceListList().stream().map(this::toEntity).toList();
    }

    public AnnounceOptionalPTO toOptionalEntity(Optional<AnnounceSearchItem> entityOptional) {
        final AnnounceOptionalPTO.Builder builder = AnnounceOptionalPTO.newBuilder();
        entityOptional.ifPresent(e -> builder.setAnnounce(toPto(e)));
        return builder.build();
    }

    public AnnouncePTO toPto(AnnounceSearchItem announce) {
        AnnouncePTO.Builder builder = AnnouncePTO.newBuilder()
                .setId(announce.getId())
                .setBody(announce.getBody())
                .setAnonymousUser(AnonymousUserPTO.newBuilder().setId(announce.getAnonymousUserId()).build())
                .setCreateDate(Optional.ofNullable(announce.getCreateDate()).map(Date::getTime).orElseThrow(() -> new DeveloperMistakeException("Create Date Mistake AnnounceSearchItem")));

        Optional.ofNullable(announce.getBeginRegion()).map(regionPtoConverter::toPto).ifPresent(builder::setBeginRegion);

        return builder
                .build();
    }

    public AnnounceListPTO toPtoList(Iterable<AnnounceSearchItem> entityList) {
        return AnnounceListPTO.newBuilder().addAllAnnounceList(StreamSupport.stream(entityList.spliterator(), false).map(this::toPto).toList()).build();
    }
}
