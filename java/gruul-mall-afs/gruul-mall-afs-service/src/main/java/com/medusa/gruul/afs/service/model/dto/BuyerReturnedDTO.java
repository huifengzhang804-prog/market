package com.medusa.gruul.afs.service.model.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * <p></p>
 *
 * @author 张治保
 * date 2022/8/9
 */
@Getter
@Setter
@ToString
public class BuyerReturnedDTO {


    /**
     * 快递退货
     */
    private ExpressRefundDTO expressRefund;


    /**
     * 到店退货
     */
    private GoStoreRefundDTO goStoreRefund;


}
