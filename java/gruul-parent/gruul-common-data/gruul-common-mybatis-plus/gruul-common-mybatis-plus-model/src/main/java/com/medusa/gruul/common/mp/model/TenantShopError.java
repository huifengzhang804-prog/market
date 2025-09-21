package com.medusa.gruul.common.mp.model;

import com.medusa.gruul.global.i18n.I18N;
import com.medusa.gruul.global.model.exception.Error;
import lombok.RequiredArgsConstructor;

/**
 * @author 张治保
 * date 2023/6/16
 */
@RequiredArgsConstructor
public enum TenantShopError implements Error {

    /**
     * 店铺id不可用
     */
    SHOP_ID_INVALID(TenantErrorCode.SHOP_ID_INVALID, "mp.shop.id.invalid"),

    /**
     * 店铺id不存在
     */
    SHOP_ID_NOT_EXISTS(TenantErrorCode.SHOP_ID_NOT_EXISTS, "mp.shop.id.not.exist");

    private final int code;
    private final String msgCode;


    @Override
    public int code() {
        return code;
    }

    @Override
    public String msg() {
        return I18N.msg(msgCode);
    }
}
