package com.beyt.anouncy.common.model;

import com.beyt.anouncy.common.util.DateUtil;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Date;
import java.util.List;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserJwtModel implements Serializable {
    private UUID userId;
    private String name;
    private String surname;
    private String username;
    private String userSessionId;
    private Date createAt;
    private List<Long> selectedLocationIdList;

    @JsonIgnore
    public String getFullName() {
        return name + " " + surname;
    }

    public boolean isExpired(Integer second) {
        return DateUtil.isExpired(createAt, second);
    }
}
