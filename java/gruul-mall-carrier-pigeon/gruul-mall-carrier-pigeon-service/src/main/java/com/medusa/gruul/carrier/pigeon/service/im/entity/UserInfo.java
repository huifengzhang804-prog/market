package com.medusa.gruul.carrier.pigeon.service.im.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;

/**
 * <p>用户信息实体类</p>
 *
 * @author An.Yan
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
public class UserInfo {

    private Long userId;
    private String nickname;
    private String avatar;
    private Boolean includeRights;

    public UserInfo(Long userId, String nickname, String avatar) {
        this.userId = userId;
        this.nickname = nickname;
        this.avatar = avatar;
    }


    public String getUserKey() {
        return String.valueOf(userId);
    }
}
