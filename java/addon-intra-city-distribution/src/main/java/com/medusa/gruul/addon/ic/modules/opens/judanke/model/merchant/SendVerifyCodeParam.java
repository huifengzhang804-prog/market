package com.medusa.gruul.addon.ic.modules.opens.judanke.model.merchant;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * @author 张治保
 * @since 2024/8/6
 */
@Getter
@Setter
@ToString
@Accessors(chain = true)
public class SendVerifyCodeParam implements Serializable {
    /**
     * 手机号
     */
    private String phone;
}
