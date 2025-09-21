package com.medusa.gruul.addon.coupon.model.dto;

import com.medusa.gruul.addon.coupon.model.enums.CouponCollectionType;
import com.medusa.gruul.global.model.o.BaseDTO;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;


/**
 * 用户优惠券中用户信息
 */
@Getter
@Setter
@ToString
@Accessors(chain = true)
public class CouponUserInfoDTO extends CouponWorkingEditDTO implements BaseDTO {

    /**
     * 优惠券用户id
     */
    private Long couponUserId;

    /**
     * 优惠券用户
     */
    private UserInfo couponUserInfo;

    /**
     * 赠券用户
     */
    private UserInfo giftUserInfo;

    /**
     * 领券方式
     */
    private CouponCollectionType collectType;


    /**
     * 用户基础信息
     */
    @Getter
    @Setter
    // @AllArgsConstructor
    public static class UserInfo {
        /**
         * 用户 id
         */
        private Long userId;

        /**
         * 用户手机号
         */
        private String mobile;

        /**
         * 用户昵称
         */
        private String nickname;

    }

}
