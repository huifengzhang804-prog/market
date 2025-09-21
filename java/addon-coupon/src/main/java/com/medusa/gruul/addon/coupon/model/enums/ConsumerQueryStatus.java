package com.medusa.gruul.addon.coupon.model.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.time.LocalDate;

/**
 * @author 张治保
 * date 2022/11/2
 */
@Getter
@RequiredArgsConstructor
public enum ConsumerQueryStatus {

    /**
     * 待使用
     * <p>
     * 注: 在[移动端-我的优惠券]页面, 为"待使用" ; 在[平台端或店铺端-用券记录], 为"未使用"
     */
    UNUSED(false, "未使用"),

    /**
     * 待领取
     */
    UNCLAIMED(true, "待领取"),

    /**
     * 已使用
     */
    USED(false, "已使用"),

    /**
     * 已过期
     */
    EXPIRED(false, "已过期");

    /**
     * 是否必须只能店铺查询
     */
    private final boolean shopMust;

    /**
     * 描述文本
     */
    private final String text;


    /**
     * 使用状态
     *
     * @param used    是否使用
     * @param endDate 结束时间
     * @return 优惠价
     */
    public static String useStatusText(boolean used, LocalDate endDate) {
        return used ? "已使用" : !used && !LocalDate.now().isAfter(endDate) ? "未使用" : "已过期";
    }

}
