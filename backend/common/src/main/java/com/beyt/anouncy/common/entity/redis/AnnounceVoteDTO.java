package com.beyt.anouncy.common.entity.redis;

import com.beyt.anouncy.common.persist.AnnounceVotePTO;
import com.beyt.anouncy.common.persist.VoteCountPTO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.BooleanUtils;

import java.io.Serializable;
import java.util.Objects;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AnnounceVoteDTO implements Serializable {

    private String announceId;
    private Long yes;
    private Long no;
    private String currentRegionId;
    private Integer regionOrder;

    public static AnnounceVoteDTO of(AnnounceVotePTO pto) {
        AnnounceVoteDTO dto = new AnnounceVoteDTO();
        dto.setAnnounceId(pto.getAnnounceId());
        dto.setYes(pto.getYes());
        dto.setNo(pto.getNo());
        dto.setCurrentRegionId(pto.getCurrentRegionId());
        dto.setRegionOrder(pto.getRegionOrder());
        return dto;
    }

    public void receiveVote(String regionId, Boolean yesOrNo) {
        if (Objects.isNull(yesOrNo) || Objects.isNull(regionId)) {
            throw new IllegalArgumentException();
        }

        if (currentRegionId.equals(regionId)) {
            if (BooleanUtils.isTrue(yesOrNo)) {
                this.yes += 1;
            } else {
                this.no += 1;
            }
        } else {
            this.currentRegionId = regionId;

            if (BooleanUtils.isTrue(yesOrNo)) {
                this.yes = 1L;
                this.no = 0L;
            } else {
                this.yes = 0L;
                this.no = 1L;
            }
        }
    }

    public static AnnounceVoteDTO of(VoteCountPTO voteCount) {
        AnnounceVoteDTO announceVoteDTO = new AnnounceVoteDTO();
        announceVoteDTO.setYes(voteCount.getYes());
        announceVoteDTO.setNo(voteCount.getNo());
        announceVoteDTO.setCurrentRegionId(voteCount.getCurrentRegionId());
        announceVoteDTO.setRegionOrder(null);

        return announceVoteDTO;
    }

    public static AnnounceVotePTO of(AnnounceVoteDTO voteCount, String regionId) {
        return AnnounceVotePTO.newBuilder()
                .setAnnounceId(voteCount.getAnnounceId())
                .setYes(voteCount.getYes())
                .setNo(voteCount.getNo())
                .setCurrentRegionId(voteCount.getCurrentRegionId())
                .setRegionOrder(voteCount.getRegionOrder()).setRegionId(regionId).build();
    }
}
