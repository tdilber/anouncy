package com.beyt.anouncy.persist.entity.model;


import com.beyt.anouncy.common.v1.VoteCountPTO;

public record VoteCount(String announceId, Long yes, Long no, String currentRegionId) {

    public VoteCountPTO convert() {
        return VoteCountPTO.newBuilder().setAnnounceId(this.announceId()).setYes(this.yes()).setNo(this.no()).setCurrentRegionId(this.currentRegionId()).build();
    }
}
