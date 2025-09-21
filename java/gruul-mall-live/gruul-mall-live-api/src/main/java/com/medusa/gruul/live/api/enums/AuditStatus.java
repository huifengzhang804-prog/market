package com.medusa.gruul.live.api.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;
import lombok.RequiredArgsConstructor;

/**
 * @author miskw
 * @date 2022/11/8
 * @Describe 直播商品审核状态
 */
@RequiredArgsConstructor
public enum AuditStatus {
    /**
     * 未审核
     */
    UNAPPROVED(0,"未审核"),
    /**
     * 审核中
     */
    UNDER_REVIEW(1,"审核中"),
    /**
     * 审核通过
     */
    APPROVED(2,"审核通过"),
    /**
     * 审核不通过
     */
    FAILED_APPROVED(3,"审核不通过"),
    /**
     * 违规下架
     */
    VIOLATION__OFF_SHELF(4,"违规下架")
    ;

    @EnumValue
    private int code;
    private String describe;

    AuditStatus(int code, String describe) {
        this.code = code;
        this.describe = describe;
    }

    public int getCode() {
        return this.code;
    }

    public String getDescribe() {
        return this.describe;
    }

    public static AuditStatus getByCode(int code) {
        for (AuditStatus it : AuditStatus.values()) {
            if (it.getCode() == code) {
                return it;
            }
        }
        return null;
    }

}
