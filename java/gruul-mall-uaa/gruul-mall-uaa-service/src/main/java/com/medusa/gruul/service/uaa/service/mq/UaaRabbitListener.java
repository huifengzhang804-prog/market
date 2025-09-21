package com.medusa.gruul.service.uaa.service.mq;

import com.medusa.gruul.common.mp.exception.TenantException;
import com.medusa.gruul.common.mp.model.TenantErrorCode;
import com.medusa.gruul.service.uaa.api.constant.UaaConstant;
import com.medusa.gruul.service.uaa.service.service.ShopAdminService;
import com.medusa.gruul.shop.api.model.dto.ShopAdminMapDTO;
import com.medusa.gruul.shop.api.model.dto.ShopsEnableDisableDTO;
import com.rabbitmq.client.Channel;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * <p>
 * 店铺管理mq监听
 * </p>
 *
 * @author 张治保
 * date 2022/5/25
 */
@Component
@RequiredArgsConstructor
public class UaaRabbitListener {

    private final ShopAdminService shopAdminService;

    /**
     * 店铺管理员修改
     */
    @RabbitListener(queues = UaaConstant.SHOP_ADMIN_CHANGE_QUEUE)
    public void shopAdminChange(ShopAdminMapDTO shopAdminMap, Channel channel, Message message) throws IOException {
        shopAdminMap.validParam();
        try {
            shopAdminService.shopAdminChange(shopAdminMap);
        } catch (TenantException tenantException) {
            if (TenantErrorCode.SHOP_ID_INVALID == tenantException.code()) {
                channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);
                return;
            }
        }
        channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);
    }

    /**
     * 启用/禁用店铺
     */
    @RabbitListener(queues = UaaConstant.SHOP_ENABLE_DISABLE_QUEUE)
    public void shopEnableDisable(ShopsEnableDisableDTO shopsEnableDisable, Channel channel, Message message) throws IOException {
        shopsEnableDisable.validParam();
        try {
            shopAdminService.shopEnableDisable(shopsEnableDisable);
        } catch (TenantException tenantException) {
            if (TenantErrorCode.SHOP_ID_INVALID == tenantException.code()) {
                channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);
                return;
            }
        }
        channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);
    }
}
