package com.medusa.gruul.addon.team.enums;

import com.medusa.gruul.global.model.exception.Error;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * 砍价活动异常
 *
 * @author jipeng
 * @since 2024/3/14
 */
@Getter
@RequiredArgsConstructor
public enum TeamErrorEnums implements Error {

    CAN_NOT_DELETE(37000,"未开始,进行中的活动不能删除"),
    CAN_NOT_REMOVE(37001,"进行中的活动不能删除"),
    SHOP_NOT_MATCH(37002,"店铺id不匹配"),
    TEAM_STATE_ERROR(37003,"业务状态不正确"),
    NO_PERMISSION(37004,"没有权限"),

    ;
    private final int code;
    private final String msgCode;
    @Override
    public int code() {
        return code;
    }

    @Override
    public String msg() {
        return msgCode;
    }
}
