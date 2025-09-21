package com.medusa.gruul.common.module.app.shop;

import com.baomidou.mybatisplus.annotation.EnumValue;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * 商家状态枚举 0.审核中,  1.正常, -1.禁用, -2审核拒绝
 *
 * @author 张治保
 * date 2022/4/14
 */
@Getter
@RequiredArgsConstructor
public enum ShopStatus {

    /**
     * 审核拒绝
     */
    REJECT(-2),
    /**
     * 禁用
     */
    FORBIDDEN(-1),
    /**
     * 审核中
     */
    UNDER_REVIEW(0),
    /**
     * 正常
     */
    NORMAL(1);

    @EnumValue
    private final Integer value;

}
