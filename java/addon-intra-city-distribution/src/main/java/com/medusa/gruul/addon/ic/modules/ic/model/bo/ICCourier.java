package com.medusa.gruul.addon.ic.modules.ic.model.bo;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;

/**
 * 送货员
 *
 * @author 张治保
 * @since 2024/8/29
 */
@Getter
@Setter
@ToString
@Accessors(chain = true)
public class ICCourier {
    /**
     * 送货员名称
     */
    private String name;

    /**
     * 送货员电话号码
     */
    private String mobile;

    /**
     * 送货员头像
     */
    private String avatar;


}
