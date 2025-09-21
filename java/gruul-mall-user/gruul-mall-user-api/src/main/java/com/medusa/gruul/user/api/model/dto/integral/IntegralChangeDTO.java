package com.medusa.gruul.user.api.model.dto.integral;

import com.medusa.gruul.common.model.message.DataChangeMessage;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * 积分变化dto
 *
 * @author xiaoq
 * @Description IntegralChangeDTO.class
 * @date 2023-02-07 14:50
 */
@Getter
@Setter
@ToString
@Accessors(chain = true)
public class IntegralChangeDTO implements Serializable {
    private static final long serialVersionUID = -1L;

    /**
     * 外部订单号
     */
    private String no;
    /**
     * 数据变化消息
     */
    private DataChangeMessage dataChangeMessage;
}
