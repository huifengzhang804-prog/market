package com.medusa.gruul.shop.service.model.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.validator.constraints.Length;


/**
 * @author 张治保 date 2022/4/15
 */
@Getter
@Setter
@ToString
public class ShopBankAccountDTO {

    /**
     * 收款人
     */
    @NotBlank
    @Length(max = 120, message = "收款人账号长度不能超过限制")
    private String payee;

    /**
     * 银行名称
     */
    @NotBlank
    private String bankName;

    /**
     * 银行账号
     */
    @NotBlank
    private String bankAccount;

    /**
     * 开户行
     */
    @NotBlank
    private String openAccountBank;
}
