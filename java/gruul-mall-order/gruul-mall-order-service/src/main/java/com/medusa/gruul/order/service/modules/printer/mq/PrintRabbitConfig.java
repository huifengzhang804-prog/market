package com.medusa.gruul.order.service.modules.printer.mq;

import com.medusa.gruul.order.api.enums.OrderRabbit;
import org.springframework.amqp.core.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 打印rabbitmq配置
 *
 * @author 张治保
 * @since 2024/11/19
 */
@Configuration
public class PrintRabbitConfig {

    /**
     * 打印订单队列 单一消费者 顺序消费 队列
     *
     * @return 队列
     */
    @Bean
    public Queue printQueue() {
        return QueueBuilder.durable(PrintRabbitQueueNames.DO_PRINT_QUEUE)
                .build();
    }

    @Bean
    public Binding printBinding(Exchange orderExchange) {
        return BindingBuilder.bind(printQueue())
                .to(orderExchange)
                .with(OrderRabbit.PRINT_PUBLISH.routingKey())
                .noargs();
    }

}
