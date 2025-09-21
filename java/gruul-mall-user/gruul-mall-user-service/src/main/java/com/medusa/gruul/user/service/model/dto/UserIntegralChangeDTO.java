package com.medusa.gruul.user.service.model.dto;

import com.medusa.gruul.common.model.enums.ChangeType;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;


/**
 * @author shishuqian
 * date 2023/2/9
 * time 11:41
 **/

@Getter
@Setter
@ToString
@Accessors(chain = true)
public class UserIntegralChangeDTO {
    @NotNull
    private Long userId;

    @NotNull
    private Long integral;

    @NotNull
    private ChangeType changeType;
}
