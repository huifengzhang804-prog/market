package com.medusa.gruul.shop.api.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.medusa.gruul.common.mp.model.BaseEntity;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

/**
 * 商家收款账号信息
 *
 * @author 张治保
 * @since 2022-04-14
 */
@Getter
@Setter
@Accessors(chain = true)
@TableName("t_shop_bank_account")
public class ShopBankAccount extends BaseEntity {

    private static final long serialVersionUID = 1L;

    /**
     * 商家注册信息id
     */
    private Long shopId;
    /**
     * 收款人
     */
    private String payee;

    /**
     * 银行名称
     */
    private String bankName;

    /**
     * 银行账号
     */
    private String bankAccount;

    /**
     * 开户行
     */
    private String openAccountBank;


}
