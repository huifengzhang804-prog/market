package com.medusa.gruul.overview.api.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * 提现者类型
 *
 * @author 张治保
 * date 2022/11/19
 */
@Getter
@RequiredArgsConstructor
public enum OwnerType {

    /**
     * 返利
     */
    REBATE(1),



    /**
     * 分销商
     */
    DISTRIBUTOR(2),

    /**
     * 商铺
     */
    SHOP(3),

    /**
     * 供应商
     */
    SUPPLIER(4);

    @EnumValue
    private final Integer value;
}
