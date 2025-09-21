package com.medusa.gruul.afs.api.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;
import lombok.Getter;
import lombok.RequiredArgsConstructor;


/**
 * @author 张治保
 * date 2022/8/3
 */
@Getter
@RequiredArgsConstructor
public enum AfsReason {

    /**
     * 以下为退款原因
     */
    WRONG_FORM(1, true, true, "地址/联系电话 填写错误"),
    EXPRESS_STAGNATED(2, true, false, "快递/物流一直未送到"),
    EXPRESS_REJECTION(3, true, false, "货物破损已拒签"),
    DONT_NEED_IT(4, true, false, "多拍/拍错/不想要"),
    EMPTY_PARCEL(11, true, false, "空包裹"),
    LOGISTICS_NO_UPDATE(13, true, false, "快递/物流无跟踪记录"),
//	PRODUCT_INFO_INCONFORMITY(11,true,true,"商品信息描述不符"),
//	NEGOTIATED_REFUND(12,true,true,"与商家协商一致退款"),
//	PRODUCT_QUALITY_PROBLEM(13,true,true,"产品质量问题"),
//	PACKAGE_PRODUCT_DAMAGED_SOILED(14, true,true,"包装/商品破损/污损"),
//	FAKE_BRAND(15,true,true,"假冒品牌"),
//	OVERTIME_DELIVERY(16,true,true,"未按约定时间发货"),
//	SELLER_WRONGLY_SEND(17,true,true,"卖家发错货"),
//	INVOICE_ISSUE(18,true,true,"发票问题"),


    /**
     * 以下为 退货退款 原因
     */
    SEVEN_DAYS_NO_REASON(5, false, false, "七天无理由退货（商品无损）"),
    WRONG_PRODUCT_DESC(6, false, false, "尺寸/容量/参数与商品描述不符"),
    DONT_LIKE_IT(7, false, false, "不喜欢或效果不好"),
    SOME_MISSING(8, false, false, "缺件/漏发"),
    QUALITY_PROBLEM(9, false, false, "质量问题"),
    WRONG_PRODUCT(10, false, false, "商家发错货");

    @EnumValue
    private final Integer value;

    /**
     * 是否属于仅退款原因
     */
    private final boolean onlyRefund;
    /**
     * 是否是在未发货时才能选择的原因
     */
    private final boolean undelivered;

    /**
     * 描述
     */
    private final String desc;

}
