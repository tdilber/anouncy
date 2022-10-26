package com.beyt.anouncy.common.context;

import lombok.Data;
import org.springframework.stereotype.Component;
import org.springframework.web.context.annotation.RequestScope;

import java.io.Serializable;
import java.util.UUID;

@Data
@Component
@RequestScope
public class UserContext implements Serializable {
    private UUID userId = null;
    private UUID anonymousUserId = null;
}
