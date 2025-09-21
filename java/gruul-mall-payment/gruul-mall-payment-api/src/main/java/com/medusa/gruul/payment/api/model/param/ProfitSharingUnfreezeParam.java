package com.medusa.gruul.payment.api.model.param;

import com.medusa.gruul.common.model.base.ShopOrderKey;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * @author 张治保
 * date 2023/6/9
 */
@Getter
@Setter
@Accessors(chain = true)
@ToString
public class ProfitSharingUnfreezeParam implements Serializable {

    /**
     * 分账单号
     */
    @NotBlank
    private String sharingNo;

    /**
     * 店铺订单key
     */
    private ShopOrderKey shopOrderKey;

}
