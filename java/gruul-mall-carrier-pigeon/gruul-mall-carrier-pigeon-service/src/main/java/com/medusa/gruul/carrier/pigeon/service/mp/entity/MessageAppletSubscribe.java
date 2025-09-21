package com.medusa.gruul.carrier.pigeon.service.mp.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.medusa.gruul.carrier.pigeon.api.enums.SubscribeMsg;
import com.medusa.gruul.common.mp.model.BaseEntity;
import lombok.Getter;
import lombok.Setter;

/**
 * <p>
 * 小程序订阅消息
 * </p>
 *
 * @author WuDi
 * @since 2023-01-09
 */
@Getter
@Setter
@TableName("t_message_applet_subscribe")
public class MessageAppletSubscribe extends BaseEntity {

    private static final long serialVersionUID = 1L;

    /**
     * 模板id
     */
    private String templateId;

    /**
     * 页面
     */
    private String page;

    /**
     * 模板类型
     */
    private SubscribeMsg subscribeMsg;


}
