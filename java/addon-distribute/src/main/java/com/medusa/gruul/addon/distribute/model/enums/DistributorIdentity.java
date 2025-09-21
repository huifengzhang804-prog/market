package com.medusa.gruul.addon.distribute.model.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * @author 张治保
 * date 2022/11/16
 */
@Getter
@RequiredArgsConstructor
public enum DistributorIdentity {
    
    /**
     * 分销员
     */
    USER(1),

    /**
     * 分销商
     */
    AFFAIRS(2);

    @EnumValue
    private final Integer value;
}
