package com.medusa.gruul.user.api.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

/**
 * 会员类型
 *
 * @author xiaoq
 * @Description MemberType.java
 * @date 2022-11-17 10:13
 */
@Getter
@RequiredArgsConstructor
public enum MemberType {

    /**
     * 免费会员
     */
    FREE_MEMBER(0, "免费"),

    /**
     * 付费会员
     */
    PAID_MEMBER(1, "付费"),
    ;

    @EnumValue
    private final Integer value;

    private final String desc;


}
