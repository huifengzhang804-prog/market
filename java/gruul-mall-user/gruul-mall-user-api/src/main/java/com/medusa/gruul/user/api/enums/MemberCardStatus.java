package com.medusa.gruul.user.api.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * 会员卡状态
 *
 * @author xiaoq
 * @Description MemberCardStatus.java
 * @date 2022-11-17 10:22
 */
@Getter
@RequiredArgsConstructor
public enum MemberCardStatus {
    /**
     * 正常
     */
    NORMAL(1),


    /**
     * 异常 (指会员未到期,但以失效/多数为降级)
     */
    ABNORMAL(2),


    /**
     * 会员正常到期(失效)
     */
    EXPIRE(3),

    ;


    @EnumValue
    private final Integer value;

}
