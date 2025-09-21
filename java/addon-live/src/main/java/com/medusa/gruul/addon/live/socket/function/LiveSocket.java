package com.medusa.gruul.addon.live.socket.function;

import com.medusa.gruul.addon.live.socket.enums.SocketMessageType;

import java.lang.annotation.*;

/**
 * @author miskw
 * @date 2023/6/19
 * @describe 描述
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Inherited
@Documented
public @interface LiveSocket {
    SocketMessageType value();
}
