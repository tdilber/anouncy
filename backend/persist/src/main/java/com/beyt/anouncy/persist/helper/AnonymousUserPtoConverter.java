package com.beyt.anouncy.persist.helper;


import com.beyt.anouncy.common.v1.AnonymousUserListPTO;
import com.beyt.anouncy.common.v1.AnonymousUserPTO;
import com.beyt.anouncy.persist.entity.AnonymousUser;
import com.beyt.anouncy.persist.helper.base.PtoConverter;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.StreamSupport;

@Component
public class AnonymousUserPtoConverter implements PtoConverter<AnonymousUser, AnonymousUserPTO, AnonymousUserListPTO> {

    @Override
    public AnonymousUser toEntity(AnonymousUserPTO pto) {
        if (Objects.isNull(pto)) {
            return null;
        }

        return new AnonymousUser(pto.getId());
    }

    @Override
    public List<AnonymousUser> toEntityList(AnonymousUserListPTO listPTO) {
        if (Objects.isNull(listPTO) || CollectionUtils.isEmpty(listPTO.getAnonymousUserListList())) {
            return new ArrayList<>();
        }

        return listPTO.getAnonymousUserListList().stream().map(this::toEntity).toList();
    }

    @Override
    public AnonymousUserPTO toPto(AnonymousUser anonymousUser) {
        return AnonymousUserPTO.newBuilder()
                .setId(anonymousUser.getId())
                .build();
    }

    @Override
    public AnonymousUserListPTO toPtoList(Iterable<AnonymousUser> entityList) {
        return AnonymousUserListPTO.newBuilder().addAllAnonymousUserList(StreamSupport.stream(entityList.spliterator(), false).map(this::toPto).toList()).build();
    }
}
