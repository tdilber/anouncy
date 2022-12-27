package com.beyt.anouncy.persist.helper.base;

import java.util.List;
import java.util.Optional;

public interface PtoConverter<Entity, Pto, PtoList, OptionalPto> {

    Entity toEntity(Pto pto);

    List<Entity> toEntityList(PtoList ptoList);

    OptionalPto toOptionalEntity(Optional<Entity> entityOptional);

    Pto toPto(Entity entity);

    PtoList toPtoList(Iterable<Entity> entityList);
}
