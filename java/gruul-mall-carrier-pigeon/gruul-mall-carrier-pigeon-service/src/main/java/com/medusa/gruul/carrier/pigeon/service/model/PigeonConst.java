package com.medusa.gruul.carrier.pigeon.service.model;

/**
 * @author 张治保
 * date 2022/10/18
 */
public interface PigeonConst {
    /**
     * 发送消息给用户消息锁前缀
     */
    String PIGEON_SEND_USER_MSG_LOCK = "send:user:msg:lock";
    /**
     * 发送消息给客服 锁前缀
     */
    String PIGEON_SEND_ADMIN_MSG_LOCK = "send:admin:msg:lock";
}
