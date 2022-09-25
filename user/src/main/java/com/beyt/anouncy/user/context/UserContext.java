package com.beyt.anouncy.user.context;

import lombok.Data;
import org.springframework.stereotype.Component;
import org.springframework.web.context.annotation.RequestScope;

import java.io.Serializable;

@Data
@Component
@RequestScope
public class UserContext implements Serializable {
    private String userId = null;
    private String anonymousUserId = null;
}
