package com.medusa.gruul.addon.ic.modules.opens.uupt.model.order;

import com.medusa.gruul.addon.ic.modules.opens.uupt.model.enums.Bool;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * @author 张治保
 * @since 2024/8/12
 */
@Getter
@Setter
@ToString
public class CancelOrderResp {
    /**
     * 是否可以取消：1-可以 0-不可以（超出取消限额请联系城市负责人）
     */
    private Bool canCancel;

    /**
     * 扣除费用（单位分）
     */
    private Long deductFee;

}
