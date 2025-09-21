package com.medusa.gruul.storage.api.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * 仓存管理状态
 *
 * @author xiaoq
 * @Description StorageManagementStatus.java
 * @date 2023-07-25 14:18
 */
@Getter
@RequiredArgsConstructor
public enum StorageManagementOrderStatus {
    /**
     * 待完成
     */
    WAIT_COMPLETION(0),

    /**
     * 完成
     */
    COMPLETION(1),

    /**
     * 取消
     */
    CANCEL(2),

    ;
    @EnumValue
    private final Integer value;
}
