package com.medusa.gruul.order.service.modules.printer.addon;

import com.medusa.gruul.common.addon.supporter.annotation.AddonSupporter;
import com.medusa.gruul.order.api.addon.OrderAddonConstant;

/**
 * @author 张治保
 * @since 2024/11/9
 */
@AddonSupporter(id = OrderAddonConstant.ORDER_PLATFORM_SUPPORT_ID)
public interface OrderPlatformSupporter {

    /**
     * 获取平台名称
     *
     * @return 平台名称
     */
    String platformName();
}
