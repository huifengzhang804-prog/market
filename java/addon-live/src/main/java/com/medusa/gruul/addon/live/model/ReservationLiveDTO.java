package com.medusa.gruul.addon.live.model;

import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.experimental.Accessors;


/**
 * @author miskw
 * @date 2023/6/6
 * @describe 预约直播间
 */
@Data
@Accessors(chain = true)
public class ReservationLiveDTO {
    /**
     * 直播间id
     */
    @NotNull(message = "直播间id不能为空")
    private Long liveId;
    /**
     * 店铺id
     */
    @NotNull(message = "店铺id不能为空")
    private Long shopId;
    /**
     * 是否关注
     */
    @NotNull(message = "请选择关注行为")
    private Boolean isReservation;

}
