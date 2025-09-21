package com.medusa.gruul.live.api.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;
import lombok.Getter;

/**
 * @author miskw
 * @date 2022/11/17
 * @describe 用户直播角色枚举
 */
@Getter
public enum LiveRole {
    /**
     * 管理员
     */
    LIVE_ADMIN(1,"管理员"),
    /**
     * 主播
     */
    LIVE_ANCHOR(2,"主播"),
    /**
     * 运营者
     */
    LIVE_OPERATOR(3,"运营者");

    LiveRole(int code,String describe){
        this.code = code;
        this.describe = describe;
    }
    @EnumValue
    private int code;
    private String describe;

    public int getCode() {
        return code;
    }

    public String getDescribe() {
        return describe;
    }

}
