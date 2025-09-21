package com.medusa.gruul.addon.ic.modules.ic.model.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * @author miskw
 * @date 2023/3/2
 */
@Getter
@RequiredArgsConstructor
public enum BillType {
    /**
     * 按距离
     */
    DISTANCE,
    /**
     * 按重量
     */
    WEIGHT

}
