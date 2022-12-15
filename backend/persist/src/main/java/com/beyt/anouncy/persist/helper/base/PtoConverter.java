package com.beyt.anouncy.persist.helper.base;

import java.util.List;

public interface PtoConverter<Entity, Pto, PtoList> {

    Entity toEntity(Pto pto);

    List<Entity> toEntityList(PtoList ptoList);

    Pto toPto(Entity entity);

    PtoList toPtoList(Iterable<Entity> entityList);
}
