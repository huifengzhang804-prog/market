package com.medusa.gruul.addon.bargain.model.dto;


import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;


/**
 * @author wudi
 */
@Getter
@Setter
@ToString
@Accessors(chain = true)
public class BargainShopIdDTO {

    /**
     * 店铺id
     */
    @NotNull(message = "店铺id不能为空")
    private Long shopId;

    /**
     * 砍价活动id
     */
    @NotNull(message = "砍价活动id不能为空")
    private Long activityId;
}
