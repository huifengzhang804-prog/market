package com.medusa.gruul.addon.coupon.model.vo;

import com.alibaba.excel.annotation.ExcelProperty;
import com.medusa.gruul.addon.coupon.model.BaseCouponModel;
import com.medusa.gruul.addon.coupon.model.enums.ConsumerQueryStatus;
import com.medusa.gruul.addon.coupon.model.enums.CouponCollectionType;
import com.medusa.gruul.common.model.constant.CommonPool;
import lombok.Data;
import lombok.experimental.Accessors;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 用户优惠券记录
 */
@Data
@Accessors(chain = true)
public class CouponUserVO extends BaseCouponModel {

    /**
     * 用户与优惠券对应关系id
     */
    private Long couponUserId;


    /**
     * 用户Id
     */
    private Long userId;


    /**
     * 店铺名称
     */
    private String shopName;

    /**
     * 优惠券id
     */
    private Long id;


    /**
     * 计算出的 优惠金额 目前仅在 结算页使用优惠券
     */
    private Long discountAmount;

    /**
     * 创建时间/领券时间
     */
    private LocalDateTime createTime;

    /**
     * 优惠券使用规则描述
     */
    private String typeDescription;

    // -------------------------------------------------------


    /**
     * 是否已使用
     */
    private Boolean used;

    /**
     * 优惠券用户昵称
     */
    private String userNickname;

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
     * 领券方式
     */
    private CouponCollectionType collectType;

    /**
     * 领券方式
     */
    private String collectTypeText;

    /**
     * 关联使用优惠券的订单编号
     */
    private String associatedOrderNo;

    /**
     * 使用状态
     */
    private ConsumerQueryStatus queryStatus;

    /**
     * 使用状态
     */
    private String queryStatusText;


    /**
     * 使用状态
     *
     * @param used    是否使用
     * @param endDate 结束时间
     * @return 使用状态
     */
    public ConsumerQueryStatus queryStatus(boolean used, LocalDate endDate) {
        return used ? ConsumerQueryStatus.USED : !used && !LocalDate.now().isAfter(endDate) ? ConsumerQueryStatus.UNUSED : ConsumerQueryStatus.EXPIRED;
    }


    /**
     * 使用状态
     *
     * @param used    是否使用
     * @param endDate 结束时间
     * @return 使用状态文本描述
     */
    public String useStatusText(boolean used, LocalDate endDate) {
        return used ? "已使用" : !used && !LocalDate.now().isAfter(endDate) ? "未使用" : "已过期";
    }


}
