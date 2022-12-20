package com.beyt.anouncy.common.entity.redis;

import com.beyt.anouncy.common.entity.elasticsearch.AnnounceSearchItem;
import com.beyt.anouncy.common.util.ProtoUtil;
import com.beyt.anouncy.common.v1.AnnouncePTO;
import com.beyt.anouncy.common.v1.VoteCountPTO;
import com.beyt.anouncy.common.vote.v1.AnnounceVotePTO;
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

    public AnnouncePageItemDTO(AnnouncePTO announce) {
        this.regionId = announce.getCurrentRegion().getId();
        this.announceId = announce.getId();
        this.body = announce.getBody();
        this.announceCreateDate = ProtoUtil.toDate(announce.getCreateDate());
    }

    public static AnnouncePageItemDTO blank(AnnouncePTO announce) {
        AnnouncePageItemDTO dto = new AnnouncePageItemDTO();
        dto.setRegionId(announce.getCurrentRegion().getId());
        dto.setAnnounceId(announce.getId());
        dto.setBody(announce.getBody());
        dto.setAnnounceCreateDate(ProtoUtil.toDate(announce.getCreateDate()));
        dto.setYes(0L);
        dto.setNo(0L);
        dto.setRegionOrder(0);
        dto.setVoteUpdateDate(new Date());
        dto.setCurrentVote(false);
        return dto;
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

    public void update(AnnounceVotePTO vote) {
        this.yes = vote.getYes();
        this.no = vote.getNo();
        this.regionOrder = vote.getRegionOrder();
        this.voteUpdateDate = new Date();
    }

    public void update(VoteCountPTO vote) {
        this.yes = vote.getYes();
        this.no = vote.getNo();
        this.regionOrder = null;
        this.voteUpdateDate = new Date();
    }

    public static Map<String, Set<String>> getRegionAnnounceIdSetMap(Collection<AnnouncePageItemDTO> announceList) {
        return announceList.stream().collect(Collectors.groupingBy(AnnouncePageItemDTO::getRegionId, Collectors.mapping(AnnouncePageItemDTO::getAnnounceId, Collectors.toSet())));
    }
}
