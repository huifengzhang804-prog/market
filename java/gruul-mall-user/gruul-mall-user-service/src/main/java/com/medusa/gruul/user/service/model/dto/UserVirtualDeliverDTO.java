package com.medusa.gruul.user.service.model.dto;

import com.medusa.gruul.common.system.model.model.Platform;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * 用户虚拟发货dto
 *
 * @author xiaoq
 * @Description UserVirtualDeliverDTO.java
 * @date 2024-01-22 18:49
 */
@Data
@Accessors(chain = true)
public class UserVirtualDeliverDTO {

    /**
     * 交易流水号
     */
    private String transactionId;
    /**
     * openid
     */
    private String openId;

    /**
     * 商品描述
     */
    private String desc;

    /**
     * 对应平台
     */
    private Platform platform;
}
