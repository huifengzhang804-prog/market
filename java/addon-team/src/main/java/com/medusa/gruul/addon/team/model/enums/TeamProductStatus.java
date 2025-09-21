package com.medusa.gruul.addon.team.model.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * @author 张治保
 * date 2023/3/15
 */
@Getter
@RequiredArgsConstructor
public enum TeamProductStatus {

    /**
     * 不存在
     */
    NO(1),

    /**
     * 未开始
     */
    OPENING(2),

    /**
     * 进行中
     */
    OPEN(3);
    
    @EnumValue
    private final Integer value;
}
