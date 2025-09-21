package com.medusa.gruul.addon.integral.model.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * 获取规则类型
 *
 * @author xiaoq
 * @Description 获取规则类型
 * @date 2023-02-02 11:39
 */
@Getter
@RequiredArgsConstructor
public enum GainRuleType {
    /**
     * 分享
     */
    SHARE(0),
    /**
     * 登入
     */
    LOGIN(1),
    /**
     * 签到
     */
    SING_IN(2),
    /**
     * 消费
     */
    CONSUME(3)
    ;

    @EnumValue
    private final Integer value;

}
