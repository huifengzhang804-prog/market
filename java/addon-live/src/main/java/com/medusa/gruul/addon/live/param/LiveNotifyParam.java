package com.medusa.gruul.addon.live.param;

import lombok.Data;
import lombok.experimental.Accessors;

/**
 * @author miskw
 * @date 2023/6/6
 * @describe 描述
 */
@Data
@Accessors(chain = true)
public class LiveNotifyParam {
    /**
     * 推流地址
     */
    private String pushAddress;
    /**
     * 推流异常事件
     */
    private Integer eventType;
}
