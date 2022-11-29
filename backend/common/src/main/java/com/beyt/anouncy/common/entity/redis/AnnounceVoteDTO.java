package com.beyt.anouncy.common.entity.redis;

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
    private Long yes;
    private Long no;
    private String currentRegionId;
    private Integer regionOrder;

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
}
