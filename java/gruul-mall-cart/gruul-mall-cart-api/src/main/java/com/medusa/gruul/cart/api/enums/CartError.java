package com.medusa.gruul.cart.api.enums;

import com.medusa.gruul.global.i18n.I18N;
import com.medusa.gruul.global.model.exception.Error;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * <p>
 *
 * </p>
 *
 * @author xiaoq
 * @Description
 * @date 2023-06-29 15:37
 */
@Getter
@RequiredArgsConstructor
public enum CartError implements Error {

    /**
     * 店铺不可用
     */
    SHOP_NOT_AVAILABLE(100000, "cart.shop.not.available"),

    /**
     * 商品不可用
     */
    GOODS_NOT_AVAILABLE(100001,"cart.goods.not.available"),

    /**
     * sku不可用
     */
    SKU_NOT_AVAILABLE(100002,"cart.sku.not.available"),


    ;

    private final int code;

    private final String msgCode;

    @Override
    public int code() {
        return getCode();
    }

    @Override
    public String msg() {
        return I18N.msg(getMsgCode());
    }

    @Override
    public String msg(Object... args) {
        return I18N.msg(getMsgCode(), args);
    }
}
