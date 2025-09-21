package com.medusa.gruul.shop.api.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * @author: wufuzhong
 * @Date: 2023/11/07 09:58:58
 * @Description: 自营商家类型枚举
 */

@Getter
@RequiredArgsConstructor
public enum SelfShopTypeEnum {

    /**
     * 自营店铺
     */
    SHOP,
    /**
     * 自营供应商
     */
    SUPPLIER;

}
