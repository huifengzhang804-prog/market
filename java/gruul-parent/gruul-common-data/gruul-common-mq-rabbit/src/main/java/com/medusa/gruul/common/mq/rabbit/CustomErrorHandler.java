package com.medusa.gruul.common.mq.rabbit;

import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.listener.ConditionalRejectingErrorHandler;
import org.springframework.lang.NonNull;

@Slf4j
public class CustomErrorHandler extends ConditionalRejectingErrorHandler {
    @Override
    protected void log(@NonNull Throwable err) {
        if (log.isErrorEnabled()) {
            log.error("》》》Rabbit MQ ERROR", err);
        }
    }
}