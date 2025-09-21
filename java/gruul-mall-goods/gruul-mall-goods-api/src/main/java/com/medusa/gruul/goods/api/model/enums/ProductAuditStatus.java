package com.medusa.gruul.goods.api.model.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;
import lombok.Getter;

/**
 * 商品审核状态 仅展示时使用
 *
 * @author xiaoq
 * @Description ProductAuditStatus.java
 * @date 2023-09-26 13:15
 */
@Getter
public enum ProductAuditStatus {

    /**
     * 已拒绝
     */
    REFUSE(-2, "已拒绝"),
    /**
     * 审核中
     */
    UNDER_REVIEW(-1, "审核中"),


    /**
     * 包含除 REFUSE  UNDER_REVIEW  以外的所有商品状态
     */
    ALREADY_PASSED(0,"已通过")
    ;


    /**
     * 值
     */
    @EnumValue
    private final int status;

    /**
     * 描述
     */
    private final String desc;

    ProductAuditStatus(int status, String desc) {
        this.status = status;
        this.desc = desc;
    }

}
