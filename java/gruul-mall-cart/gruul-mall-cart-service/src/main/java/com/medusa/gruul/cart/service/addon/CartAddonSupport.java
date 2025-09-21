package com.medusa.gruul.cart.service.addon;

import com.medusa.gruul.cart.api.constant.CartConstant;
import com.medusa.gruul.common.addon.supporter.annotation.AddonSupporter;

/**
 * 
 * @author jipeng
 * @since 2025/2/27
 */
@AddonSupporter(id = CartConstant.ADDON_SUPPORT_ID)
public interface CartAddonSupport {
    /**
     * @param shopId
     * @return{@link com.medusa.gruul.addon.ic.addon.impl.IcAddonProviderImpl#queryShopIcStatusForCart}
     */
     Boolean queryShopIcStatusForCart(Long shopId);
}
