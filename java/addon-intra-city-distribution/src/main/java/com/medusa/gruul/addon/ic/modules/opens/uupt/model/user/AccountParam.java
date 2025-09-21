package com.medusa.gruul.addon.ic.modules.opens.uupt.model.user;

import com.medusa.gruul.addon.ic.modules.opens.uupt.model.enums.PayTypeEnum;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;

/**
 * @author 张治保
 * @since 2024/8/12
 */
@Getter
@Setter
@ToString
@Accessors(chain = true)
public class AccountParam {

    private PayTypeEnum payTypeEnum;
}
