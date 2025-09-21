package com.medusa.gruul.common.netty.autoconfigure.annotation;

import com.medusa.gruul.common.netty.autoconfigure.NettyWebSocketSelector;
import org.springframework.context.annotation.Import;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author miskw
 * 开启WebSocket
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Import(NettyWebSocketSelector.class)
public @interface EnableWebSocket {

    String[] scanBasePackages() default {};

}
