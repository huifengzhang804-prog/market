package com.medusa.gruul.addon.ic.mq;

import org.springframework.stereotype.Component;

/**
 * @author 张治保
 * @since 2024/8/26
 */
@Component
public class ICRabbitListener {

//    @RabbitListener(queues = ICRabbitQueueNames.ORDER_PAID_QUEUE)
//    public void orderPaid(OrderPaidBroadcastDTO orderPaid, Channel channel, Message message) throws IOException {
//        JSONObject extra = orderPaid.getExtra();
//        if (extra == null || !DistributionMode.INTRA_CITY_DISTRIBUTION.equals(extra.get("distributionMode"))) {
//            channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);
//        }
//        
//    }
}
