package com.medusa.gruul.addon.coupon.model.dto;

import com.alibaba.excel.annotation.ExcelProperty;
import lombok.Getter;
import lombok.Setter;

/**
 * 用券记录导出
 */
@Setter
@Getter
public class CouponUseRecordExportDTO {

    /**
     * 编号
     */
    @ExcelProperty(value = "编号")
    private String index;

    /**
     * 用户与优惠券对应关系id
     */
    @ExcelProperty(value = "优惠券号")
    private String couponUserId;

    /**
     * 优惠券用户昵称
     */
    @ExcelProperty(value = "用户")
    private String userNickname;

    /**
     * 优惠券用户手机号
     */
    @ExcelProperty(value = "手机号")
    private String userPhone;

    /**
     * 优惠券名称
     */
    @ExcelProperty(value = "优惠券名称")
    private String name;

    /**
     * 面值(优惠金额amount 或者 折扣比discount)
     */
    @ExcelProperty(value = "面值")
    private String parValue;

    /**
     * 优惠券使用状态
     * ConsumerQueryStatus.class
     */
    @ExcelProperty(value = "状态")
    private String queryStatus;

    /**
     * 优惠券类型
     * CouponType.class
     */
    @ExcelProperty(value = "优惠券类型")
    private String type;

    /**
     * 优惠券类型描述
     * CouponType.class
     */
    @ExcelProperty(value = "优惠券规则")
    private String couponTypeDescription;

    /**
     * 领券方式
     * CouponCollectionType.class
     */
    @ExcelProperty(value = "领取方式")
    private String collectType;

    /**
     * 关联使用优惠券的订单编号
     */
    @ExcelProperty(value = "关联订单")
    private String associatedOrderNo;

    /**
     * 创建时间/领券时间
     */
    @ExcelProperty(value = "领券时间")
    private String createTime;

    /**
     * 到期时间
     */
    @ExcelProperty(value = "到期时间")
    private String endDate;

    /**
     * 赠券用户手机号
     */
    @ExcelProperty(value = "赠券用户")
    private String giftUserPhone;

}
