package com.medusa.gruul.order.api.addon.freight;

import com.medusa.gruul.global.model.express.UserAddressDTO;
import com.medusa.gruul.order.api.enums.SelfShopTypeEnum;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * @author 张治保
 * date 2022/7/28
 */
@Getter
@Setter
@ToString
@Accessors(chain = true)
public class SendDeliveryDTO implements Serializable {
    private static final long serialVersionUID = -5908788408808126132L;
    private String tempid;

    /**
     * 打印机id
     */
    private Long printId;

    /**
     * 包裹扩展
     */
    private String cargo;

    /**
     * 业务单号
     */
    private String no;

    /**
     * 包裹重量
     */
    private BigDecimal weight;

    /**
     * 快递公司code
     */
    private String expressCompanyCode;

    /**
     * 备注
     */
    private String remark;

    /**
     * 发货人信息
     */
    private UserAddressDTO sender;

    /**
     * 收货人信息
     */
    private UserAddressDTO receiver;


    /**
     * 是否直接发货
     */
    private Boolean dropDeliver;

    /**
     * 自营店铺类型
     */
    private SelfShopTypeEnum selfShopType;
}
