package com.beyt.anouncy.common.entity.redis;

import com.beyt.anouncy.common.entity.elasticsearch.AnnounceSearchItem;
import com.beyt.anouncy.common.entity.neo4j.Announce;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AnnouncePageItemDTO implements Serializable {
    private String regionId;
    private String announceId;
    private String body;
    private Date announceCreateDate;
    private Long yes;
    private Long no;
    private Integer regionOrder;
    private Date voteUpdateDate;
    private Boolean currentVote = null;

    public AnnouncePageItemDTO(Announce announce) {
        this.regionId = announce.getCurrentRegion().getId();
        this.announceId = announce.getId();
        this.body = announce.getBody();
        this.announceCreateDate = announce.getCreateDate();
    }

    public AnnouncePageItemDTO(AnnounceSearchItem announce) {
        this.regionId = announce.getBeginRegion().getId(); // TODO think about it
        this.announceId = announce.getId();
        this.body = announce.getBody();
        this.announceCreateDate = announce.getCreateDate();
    }

    public void update(AnnounceVoteDTO vote) {
        this.yes = vote.getYes();
        this.no = vote.getNo();
        this.regionOrder = vote.getRegionOrder();
        this.voteUpdateDate = new Date();
    }
}
