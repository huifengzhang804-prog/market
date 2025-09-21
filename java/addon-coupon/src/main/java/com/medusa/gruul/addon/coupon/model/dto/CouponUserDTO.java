package com.medusa.gruul.addon.coupon.model.dto;

import com.medusa.gruul.addon.coupon.model.enums.ConsumerQueryStatus;
import com.medusa.gruul.addon.coupon.model.vo.CouponVO;
import com.medusa.gruul.global.model.o.BaseQueryPageDTO;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;

import java.util.Set;


/**
 * 用户优惠券查询入参(用券记录分页查询条件)
 */
@Getter
@Setter
@ToString
@Accessors(chain = true)
public class CouponUserDTO extends BaseQueryPageDTO<CouponVO> {


    /**
     * 店铺id
     */
    private Long shopId;

    /**
     * 查询状态
     */
    private ConsumerQueryStatus status;

    /**
     * 关联使用优惠券的订单编号
     */
    private String associatedOrderNo;

    /**
     * 优惠券名称
     */
    private String name;

    /**
     * 用户与优惠券对应关系id
     */
    private Long couponUserId;

    /**
     * 优惠券用户手机号
     */
    private String userPhone;

    /**
     * 赠券用户id
     */
    private Long giftUserId;

    /**
     * 赠券用户手机号
     */
    private String giftUserPhone;

    /**
     * 导出的ids
     */
    private Set<Long> exportCouponUserIds;


}
