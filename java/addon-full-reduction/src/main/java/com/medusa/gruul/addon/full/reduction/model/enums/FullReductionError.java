package com.medusa.gruul.addon.full.reduction.model.enums;

import com.medusa.gruul.global.i18n.I18N;
import com.medusa.gruul.global.model.exception.Error;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * 满减错误状态枚举类
 *
 * @author jipeng
 * @since 2024/3/13
 */
@Getter
@RequiredArgsConstructor
public enum FullReductionError implements Error {

    /**
     * 当前状态不能编辑
     */
    CANNOT_EDIT(78001, "full.reduction.cannot.edit"),

    /**
     * 当前处于不可下架状态
     */
    CANNOT_SHELF(78002, "full.reduction.cannot.shelf"),

    /**
     * 开始时间不在允许的范围内  当前时间距开始时间不能小于30秒 用于给定时任务留出执行时间
     */
    START_TIME_NOT_IN_RANGE(78003, "full.reduction.start.time.not.in.range"),


    ;
    private final int code;
    private final String msg;

    @Override
    public int code() {
        return getCode();
    }

    @Override
    public String msg() {
        return I18N.msg(msg);
    }
}
