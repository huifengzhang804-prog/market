package com.medusa.gruul.order.service.modules.printer.mq.service.impl;

import com.medusa.gruul.order.api.enums.OrderRabbit;
import com.medusa.gruul.order.service.modules.printer.model.bo.PrintPublishBO;
import com.medusa.gruul.order.service.modules.printer.mq.service.PrintRabbitService;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

/**
 * @author 张治保
 * @since 2024/11/19
 */
@Service
@RequiredArgsConstructor
public class PrintRabbitServiceImpl implements PrintRabbitService {

    private final RabbitTemplate rabbitTemplate;

    @Override
    public void print(PrintPublishBO publish) {
        rabbitTemplate.convertAndSend(
                OrderRabbit.PRINT_PUBLISH.exchange(),
                OrderRabbit.PRINT_PUBLISH.routingKey(),
                publish
        );
    }
}
