package com.beyt.anouncy.common.util;

import com.beyt.anouncy.common.v1.IdStr;
import com.beyt.anouncy.common.v1.IdStrList;
import com.beyt.anouncy.common.v1.PageablePTO;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.Date;
import java.util.List;
import java.util.Objects;

public final class ProtoUtil {
    private ProtoUtil() {
    }

    public static Date toDate(Long time) {
        if (Objects.isNull(time)) {
            return null;
        }

        return new Date(time);
    }

    public static String of(IdStr idStr) {
        return idStr.getId();
    }

    public static List<String> of(IdStrList idStrList) {
        return idStrList.getIdListList().stream().map(ProtoUtil::of).toList();
    }

    public static IdStrList toIdStrList(List<String> idList) {
        return IdStrList.newBuilder().addAllIdList(idList.stream().map(ProtoUtil::toIdStr).toList()).build();
    }

    public static IdStr toIdStr(String i) {
        return IdStr.newBuilder().setId(i).build();
    }

    public static PageablePTO toPageable(Pageable pageable) {
        return PageablePTO.newBuilder().setSize(pageable.getPageSize()).setPage(pageable.getPageNumber()).build();
    }

    public static Pageable toPageable(PageablePTO pageable) {
        return PageRequest.of(pageable.getPage(), pageable.getSize());
    }
}
