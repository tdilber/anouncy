package com.beyt.anouncy.common.entity.redis;

import com.beyt.anouncy.common.entity.neo4j.Announce;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AnnouncePageCache implements Serializable {
    private List<AnnouncePageItem> itemList = new ArrayList<>();

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class AnnouncePageItem implements Serializable {
        private String announceId;
        private String body;
        private Date announceCreateDate;
        private Long yes;
        private Long no;
        private Integer regionOrder;
        private Date voteUpdateDate;
        private Boolean currentVote = null;

        public AnnouncePageItem(Announce announce) {
            this.announceId = announce.getId();
            this.body = announce.getBody();
            this.announceCreateDate = announce.getCreateDate();
        }

        public void update(VoteSingleCache vote) {
            this.yes = vote.getYes();
            this.no = vote.getNo();
            this.regionOrder = vote.getRegionOrder();
            this.voteUpdateDate = new Date();
        }
    }
}
