package com.medusa.gruul.addon.bargain.model.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;


/**
 * 砍价帮砍人
 *
 * @author wudi
 */

@Getter
@Setter
@ToString
@Accessors(chain = true)
public class BargainHelpPeopleDTO {


    /**
     * 发起人id
     */
    @NotNull(message = "发起人id不能为空")
    private Long sponsorId;

    /**
     * 店铺id
     */
    @NotNull(message = "店铺id不能为空")
    private Long shopId;

    /**
     * 商品id
     */
    @NotNull(message = "商品id不能为空")
    private Long productId;

    /**
     * 活动id
     */
    @NotNull(message = "活动id不能为空")
    private Long activityId;

    /**
     * 砍价订单id
     */
    @NotNull(message = "砍价订单id不能为空")
    private Long bargainOrderId;


    /**
     * 用户头像
     */
    @NotBlank(message = "用户头像不能为空")
    private String userHeadPortrait;

    /**
     * 用户昵称
     */
    @NotBlank(message = "用户昵称不能为空")
    private String userNickName;
}
