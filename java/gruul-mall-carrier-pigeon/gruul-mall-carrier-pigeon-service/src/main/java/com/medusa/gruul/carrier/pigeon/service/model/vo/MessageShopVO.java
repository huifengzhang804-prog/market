package com.medusa.gruul.carrier.pigeon.service.model.vo;

import com.medusa.gruul.carrier.pigeon.service.model.Notice;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;

/**
 * @author 张治保
 * date 2022/10/10
 */
@Getter
@Setter
@Accessors(chain = true)
@ToString
public class MessageShopVO extends Notice {

    /**
     * 主键 key
     */
    private Long id;

    /**
     * 店铺id
     */
    private Long shopId;

    /**
     * 店铺名称
     */
    private String shopName;

    /**
     * 店铺logo
     */
    private String shopLogo;



}
