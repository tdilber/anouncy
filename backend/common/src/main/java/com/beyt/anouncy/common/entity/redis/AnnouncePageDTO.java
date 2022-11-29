package com.beyt.anouncy.common.entity.redis;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AnnouncePageDTO implements Serializable {
    private List<AnnouncePageItemDTO> itemList = new ArrayList<>();

}
