package com.medusa.gruul.common.mq.rabbit;

import lombok.Getter;
import lombok.Setter;
import org.springframework.amqp.core.AcknowledgeMode;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.time.Duration;

/**
 * @author 张治保
 * date 2023/1/10
 */
@Getter
@Setter
@ConfigurationProperties(prefix = "gruul.rabbit")
public class CustomRabbitProperties {

    /**
     * 每次批量消费的数量 默认每批300个
     */
    private Integer batchSize = 300;

    /**
     * 消费者等待数据的时间 默认 700毫秒
     */
    private Duration receiveTimeout = Duration.ofMillis(700);

    /**
     * 批量消费ack确认模式 默认手动确认
     */
    private AcknowledgeMode batchAckMode = AcknowledgeMode.MANUAL;


}
