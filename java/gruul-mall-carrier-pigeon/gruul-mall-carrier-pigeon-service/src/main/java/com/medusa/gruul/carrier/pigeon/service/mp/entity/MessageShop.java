package com.medusa.gruul.carrier.pigeon.service.mp.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.medusa.gruul.common.mp.model.BaseEntity;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

/**
 * <p>
 * 店铺信息
 * </p>
 *
 * @author 张治保
 * @since 2022-10-10
 */
@Getter
@Setter
@Accessors(chain = true)
@TableName("t_message_shop")
public class MessageShop extends BaseEntity {

    private static final long serialVersionUID = 1L;
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
