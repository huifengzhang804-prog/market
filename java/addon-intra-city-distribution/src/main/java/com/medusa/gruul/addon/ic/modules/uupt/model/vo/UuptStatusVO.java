package com.medusa.gruul.addon.ic.modules.uupt.model.vo;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

/**
 * @author 张治保
 * @since 2024/8/13
 */
@Getter
@Setter
@Accessors(chain = true)
public class UuptStatusVO {

    /**
     * 平台是否已激活
     */
    private Boolean platformActivated;

    /**
     * 当前店铺城市是否已开放
     */
    private Boolean cityOpen;

    /**
     * 是否已激活
     */
    private Boolean activated;

    /**
     * 账户余额
     */
    private Long balance;

    /**
     * 冻结金额
     */
    private Long frozen;

}
