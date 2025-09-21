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
public class ShopRegisterInfoVO {

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
