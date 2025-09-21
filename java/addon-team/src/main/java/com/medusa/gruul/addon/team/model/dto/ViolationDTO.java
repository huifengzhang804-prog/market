package com.medusa.gruul.addon.team.model.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * 违规下架DTO
 *
 * @author jipeng
 * @since 2024/3/20
 */
@Data
public class ViolationDTO {

    /**
     * 拼团活动id
     */
    @NotNull
    private Long teamActivityId;
    /**
     * 店铺Id
     */
    @NotNull
    private Long shopId;

    /**
     * 违规下架原因
     */
    @NotNull
    private String violationReason;
}
