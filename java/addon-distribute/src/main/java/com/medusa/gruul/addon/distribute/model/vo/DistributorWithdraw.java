package com.medusa.gruul.addon.distribute.model.vo;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * @author 张治保
 * date 2022/11/22
 */
@Getter
@Setter
@ToString
@Accessors(chain = true)
public class DistributorWithdraw implements Serializable {
    
    /**
     * 可提现余额
     */
    private Long undrawn;

    /**
     * 是否可微信提现
     */
    private Boolean wechat;

}
