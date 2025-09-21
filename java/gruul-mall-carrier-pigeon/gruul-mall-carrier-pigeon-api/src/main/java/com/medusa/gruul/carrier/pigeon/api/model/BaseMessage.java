package com.medusa.gruul.carrier.pigeon.api.model;

import com.medusa.gruul.carrier.pigeon.api.enums.Channel;

import java.io.Serializable;

/**
 * @author 张治保
 * date 2022/10/19
 */
public interface BaseMessage extends Serializable {

    /**
     * 获取消息频道
     *
     * @return 消息频道
     */
    Channel getChannel();
}
