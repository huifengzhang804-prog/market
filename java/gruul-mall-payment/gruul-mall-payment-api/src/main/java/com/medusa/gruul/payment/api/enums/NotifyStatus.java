package com.medusa.gruul.payment.api.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 *
 * 业务处理状态enum
 *
 *
 * @author xiaoq
 * @ Description 业务处理状态enum
 * @date 2022-07-25 13:55
 */

@Getter
@RequiredArgsConstructor
public enum NotifyStatus {

    /**
     * 未处理完成
     */
    UNFINISHED(0),

    /**
     * 已处理完成
     */
    ACCOMPLISH(1),
    ;
    @EnumValue
    private final Integer status;
}
