package com.medusa.gruul.addon.distribute.model.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * 分销商申请状态 1. 未申请 2.申请中 3.关闭 4.申请通过
 *
 * @author 张治保
 * date 2022/11/16
 */
@Getter
@RequiredArgsConstructor
public enum DistributorStatus {

    /**
     * 未申请
     */
    NOT_APPLIED(1),

    /**
     * 申请中
     */
    APPLYING(2),

    /**
     * 申请关闭
     */
    CLOSED(3),

    /**
     * 申请通过
     */
    SUCCESS(4);


    @EnumValue
    private final Integer value;

}
