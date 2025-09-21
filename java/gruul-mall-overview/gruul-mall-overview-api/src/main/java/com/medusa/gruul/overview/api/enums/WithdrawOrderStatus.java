package com.medusa.gruul.overview.api.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * 提现工单状态
 *
 * @author 张治保
 * date 2022/11/19
 */
@Getter
@RequiredArgsConstructor
public enum WithdrawOrderStatus {
    /**
     * 申请中
     */
    APPLYING(1),

    /**
     * 成功 已打款
     */
    SUCCESS(2),

    /**
     * 已关闭
     */
    CLOSED(3),

    /**
     * 已禁用
     */
    FORBIDDEN(4),

    /**
     * 处理中
     */
    PROCESSING(5),

    ;
    
    @EnumValue
    private final Integer value;
}
