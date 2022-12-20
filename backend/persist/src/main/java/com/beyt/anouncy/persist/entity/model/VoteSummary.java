package com.beyt.anouncy.persist.entity.model;


import com.beyt.anouncy.common.v1.VoteSummaryPTO;

public record VoteSummary(String id, Boolean value, String announceId) {

    public VoteSummaryPTO convert() {
        return VoteSummaryPTO.newBuilder().setAnnounceId(this.announceId()).setValue(this.value()).setId(this.id()).build();
    }
}
