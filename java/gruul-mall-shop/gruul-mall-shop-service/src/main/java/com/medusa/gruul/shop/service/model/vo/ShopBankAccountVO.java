package com.medusa.gruul.shop.service.model.vo;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDateTime;

/**
 * @author 张治保
 * date 2022/4/15
 */
@Getter
@Setter
@ToString
public class ShopBankAccountVO {

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
    /**
     * 创建时间
     */
    private LocalDateTime createTime;
    /**
     * 更新时间
     */
    private LocalDateTime updateTime;
    /**
     * 版本号
     */
    private Integer version;
}
