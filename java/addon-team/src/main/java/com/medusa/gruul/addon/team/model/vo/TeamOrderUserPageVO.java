package com.medusa.gruul.addon.team.model.vo;

import lombok.Data;
import lombok.ToString;
import lombok.experimental.Accessors;

import java.time.LocalDateTime;

/**
 * 团购订单用户信息
 *
 * @author 张治保
 */
@Data
@ToString
@Accessors(chain = true)
public class TeamOrderUserPageVO {

    /**
     * 订单号
     */
    private String orderNo;

    /**
     * 用户id
     */
    private Long userId;

    /**
     * 用户头像
     */
    private String avatar;

    /**
     * 用户昵称
     */
    private String nickname;

    /**
     * 是否是团长
     */
    private Boolean commander;

    /**
     * 拼团价
     */
    private Long price;

    /**
     * 实付金额
     */
    private Long amount;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;
}
