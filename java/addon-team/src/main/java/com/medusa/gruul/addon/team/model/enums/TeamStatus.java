package com.medusa.gruul.addon.team.model.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * 拼团活动状态
 *
 * @author 张治保
 * date 2023/3/10
 */
@Getter
@RequiredArgsConstructor
public enum TeamStatus {

    /**
     * 正常状态  未开始  进行中 已结束 都属于正常状态 用于DB存储
     */
    OK(0),
    /**
     * 未开始 用于前端搜索
     */
    OPENING(1),

    /**
     * 预热中 用于前端搜索
     */
    PREHEAT(2),

    /**
     * 进行中 用于前端搜索
     */
    OPEN(3),

    /**
     * 已结束 用于前端搜索
     */
    FINISHED(4),

    /**
     * 违规 用于前端搜索  DB存储
     */
    VIOLATION(5),

    /**
     * 店铺下架 用于前端搜索  DB存储
     */
    SHOP_OFF_SHELF(6),
    ;

    @EnumValue
    private final Integer value;
}
