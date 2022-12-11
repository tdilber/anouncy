package com.beyt.anouncy.common.entity.redis;

import com.beyt.anouncy.common.entity.elasticsearch.AnnounceSearchItem;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Collection;
import java.util.Date;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

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

//    public AnnouncePageItemDTO(Announce announce) {
//        this.regionId = announce.getCurrentRegion().getId();
//        this.announceId = announce.getId();
//        this.body = announce.getBody();
//        this.announceCreateDate = announce.getCreateDate();
//    }
//
//    public static AnnouncePageItemDTO blank(Announce announce) {
//        AnnouncePageItemDTO dto = new AnnouncePageItemDTO();
//        dto.setRegionId(announce.getCurrentRegion().getId());
//        dto.setAnnounceId(announce.getId());
//        dto.setBody(announce.getBody());
//        dto.setAnnounceCreateDate(announce.getCreateDate());
//        dto.setYes(0L);
//        dto.setNo(0L);
//        dto.setRegionOrder(0);
//        dto.setVoteUpdateDate(new Date());
//        dto.setCurrentVote(false);
//        return dto;
//    }

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

//    public void update(VoteCount vote) {
//        this.yes = vote.yes();
//        this.no = vote.no();
//        this.regionOrder = null;
//        this.voteUpdateDate = new Date();
//    }

    public static Map<String, Set<String>> getRegionAnnounceIdSetMap(Collection<AnnouncePageItemDTO> announceList) {
        return announceList.stream().collect(Collectors.groupingBy(AnnouncePageItemDTO::getRegionId, Collectors.mapping(AnnouncePageItemDTO::getAnnounceId, Collectors.toSet())));
    }
}
