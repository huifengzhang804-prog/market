package com.medusa.gruul.shop.api.model.dto;

import com.alibaba.fastjson2.annotation.JSONField;
import com.medusa.gruul.shop.api.enums.ShopSettledWayEnum;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * @author xiaoq
 * @since 2024/6/29
 */

@Getter
@Setter
@ToString
@Accessors(chain = true)
public class ShopExtraDTO implements Serializable {
    /**
     * 拒绝原因
     */
    private String reasonForRejection;

    /**
     * 审核时间
     */
    @JSONField(format = "yyyy-MM-dd")
    private LocalDateTime auditTime;
    /**
     * 入驻方式
     */
    private ShopSettledWayEnum settledWay;
    /**
     * 操作人或者审核人的用户id
     */
    private Long operatorUserId;

    /**
     * 操作人或者审核人的手机号
     */
    private String operatorName;
    /**
     * 操作人或者审核人的手机号
     */
    private String operatorPhone;


}