package com.medusa.gruul.global.model.enums;

import cn.hutool.core.lang.PatternPool;
import com.baomidou.mybatisplus.annotation.EnumValue;
import com.medusa.gruul.global.model.constant.RegexPools;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;

import java.util.regex.Pattern;

/**
 * 店铺表达设置 输入框 （电话，身份证 文本 数字 图片 日期 时间 日期时间）
 *
 * @author 张治保
 * date 2022/10/26
 */
@Getter
@RequiredArgsConstructor
public enum InputType {

    /**
     * 电话
     */
    MOBILE(1, PatternPool.MOBILE),

    /**
     * 身份证
     */
    CITIZEN_ID(2, PatternPool.CITIZEN_ID),

    /**
     * 文本
     */
    TEXT(3, null),

    /**
     * 数字
     */
    NUMBER(4, PatternPool.NUMBERS),

    /**
     * 图片
     */
    IMAGE(5, PatternPool.URL_HTTP),

    /**
     * 日期
     */
    DATE(6, PatternPool.get(RegexPools.DATE)),

    /**
     * 时间
     */
    TIME(7, PatternPool.TIME),

    /**
     * 日期时间
     */
    DATETIME(8, PatternPool.get(RegexPools.DATETIME)),

    /**
     * 备注
     */
    REMARK(9, null),
    ;


    @EnumValue
    private final Integer value;
    /**
     * 正则表达式
     */
    private final Pattern pattern;

    /**
     * 校验输入是否符合正则
     *
     * @param input 输入内容
     * @return 是否符合正则
     */
    public final boolean matches(@NotNull String input) {
        if (pattern == null) {
            return true;
        }
        return pattern.matcher(input).matches();
    }

}
