package com.beyt.anouncy.user.entity;

import com.beyt.anouncy.common.model.UserJwtModel;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.logging.log4j.util.Strings;
import org.springframework.util.StringUtils;

import java.time.Instant;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Data
@Table(name = "users")
@Entity
@NoArgsConstructor
@AllArgsConstructor
public class User extends BaseUuidEntity {
    public static final String LOGIN_REGEX = "^(?>[a-zA-Z0-9!$&*+=?^_`{|}~.-]+@[a-zA-Z0-9-]+(?:\\.[a-zA-Z0-9-]+)*)|(?>[_.@A-Za-z0-9-]+)$";
    public static final String LOCATION_IDS_SEPARATOR = ";";

    @NotNull
    @Pattern(regexp = LOGIN_REGEX)
    @Size(min = 1, max = 50)
    @Column(length = 50, unique = true, nullable = false)
    private String username;

    @JsonIgnore
    @NotNull
    @Size(min = 60, max = 60)
    @Column(name = "password_hash", length = 60, nullable = false)
    private String password;

    @Size(max = 50)
    @Column(name = "first_name", length = 50)
    private String firstName;

    @Size(max = 50)
    @Column(name = "last_name", length = 50)
    private String lastName;

    @Email
    @Size(min = 5, max = 254)
    @Column(length = 254, unique = true)
    private String email;

    @NotNull
    @Column(nullable = false)
    private boolean activated = false;

    @Size(min = 2, max = 10)
    @Column(name = "lang_key", length = 10)
    private String langKey;

    @Size(max = 256)
    @Column(name = "image_url", length = 256)
    private String imageUrl;

    @Size(max = 1000)
    @Column(name = "selected_location_ids", length = 1000)
    private String selectedLocationIds;

    @Size(max = 100)
    @Column(name = "activation_key", length = 100)
    @JsonIgnore
    private String activationKey;

    @Column(name = "activation_date")
    private Instant activationDate = null;

    @Size(max = 20)
    @Column(name = "reset_key", length = 20)
    @JsonIgnore
    private String resetKey;

    @Column(name = "reset_date")
    private Instant resetDate = null;

    public List<Long> getSelectedLocationIdList() {
        if (Strings.isBlank(selectedLocationIds)) {
            return List.of();
        }

        return Arrays.stream(Optional.ofNullable(StringUtils.split(selectedLocationIds, LOCATION_IDS_SEPARATOR)).orElse(new String[0])).map(idStr -> Long.parseLong(idStr.trim())).toList();
    }

    public void setSelectedLocationIdList(List<Long> selectedLocationIdList) {
        if (CollectionUtils.isEmpty(selectedLocationIdList)) {
            this.selectedLocationIds = null;
        }
        this.selectedLocationIds = Strings.join(selectedLocationIdList.stream().map(Object::toString).toList(), LOCATION_IDS_SEPARATOR.charAt(0));
    }

    public UserJwtModel createJwtModel(String sessionId) {
        UserJwtModel jwtModel = new UserJwtModel();
        jwtModel.setUserId(this.getId());
        jwtModel.setName(this.getFirstName());
        jwtModel.setSurname(this.getLastName());
        jwtModel.setUsername(this.getUsername());
        jwtModel.setUserSessionId(sessionId);
        jwtModel.setCreateAt(new Date());
        jwtModel.setSelectedLocationIdList(this.getSelectedLocationIdList());

        return jwtModel;
    }
}
