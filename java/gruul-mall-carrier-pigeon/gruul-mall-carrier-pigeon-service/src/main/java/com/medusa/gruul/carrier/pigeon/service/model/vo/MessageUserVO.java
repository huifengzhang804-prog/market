package com.medusa.gruul.carrier.pigeon.service.model.vo;

import com.medusa.gruul.carrier.pigeon.service.model.Notice;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * @author 张治保
 * date 2022/10/10
 */
@Getter
@Setter
@ToString
public class MessageUserVO extends Notice {

    /**
     * 主键 id
     */
    private Long id;

    /**
     * 用户id
     */
    private Long userId;

    /**
     * 昵称
     */
    private String nickname;

    /**
     * 头像
     */
    private String avatar;

    /**
     * 是否是已被接待的理消息
     */
    private Boolean handled;

    /**
     * 未读数量
     */
    private Integer unreadNumber;
}
