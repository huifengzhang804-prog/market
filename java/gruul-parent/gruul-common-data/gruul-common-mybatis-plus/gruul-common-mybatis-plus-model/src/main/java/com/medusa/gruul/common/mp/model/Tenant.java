package com.medusa.gruul.common.mp.model;

import lombok.Getter;
import lombok.Setter;

/**
 * @author 张治保
 * date 2022/4/16
 */
@Getter
@Setter
public class Tenant {
    /**
     * 是否禁用平台多租户
     */
    private boolean disableProvider = false;
    /**
     * 是否禁用商家店铺多租户
     */
    private boolean disableShop = false;
}
