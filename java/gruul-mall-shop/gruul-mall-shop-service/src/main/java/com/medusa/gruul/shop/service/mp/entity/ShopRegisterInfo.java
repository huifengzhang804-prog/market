package com.medusa.gruul.shop.service.mp.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.medusa.gruul.common.mp.model.BaseEntity;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

/**
 * 
 * 店铺注册信息
 * 
 *
 * @author 张治保
 * @since 2022-04-15
 */
@Getter
@Setter
@Accessors(chain = true)
@TableName("t_shop_register_info")
public class ShopRegisterInfo extends BaseEntity {

    private static final long serialVersionUID = 1L;

    /**
     * 商家注册信息id
     */
    private Long shopId;
    /**
     * 营业执照
     */
    private String license;

    /**
     * 法人身份证 正面
     */
    private String legalPersonIdFront;

    /**
     * 法人身份证 背面
     */
    private String legalPersonIdBack;


}
