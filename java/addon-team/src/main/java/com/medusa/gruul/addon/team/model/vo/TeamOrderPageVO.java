package com.medusa.gruul.addon.team.model.vo;

import com.medusa.gruul.addon.team.model.enums.TeamOrderStatus;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;

import java.time.LocalDateTime;

/**
 * @author 张治保
 * date 2023/3/11
 */
@Getter
@Setter
@Accessors(chain = true)
@ToString
public class TeamOrderPageVO {

    /**
     * 店铺id
     */
    private Long shopId;

    /**
     * 活动id
     */
    private Long activityId;

    /**
     * 团号
     */
    private String teamNo;

    /**
     * 订单号
     */
    private String orderNo;

    /**
     * 团长用户id
     */
    private Long commanderId;

    /**
     * 团长头像
     */
    private String commanderAvatar;

    /**
     * 团长用户昵称
     */
    private String commanderNickname;

    /**
     * 商品id
     */
    private Long productId;

    /**
     * 商品名称
     */
    private String productName;

    /**
     * 商品图片
     */
    private String productImage;

    /**
     * 开团时间
     */
    private LocalDateTime openTime;

    /**
     * 当前参团人数
     */
    private Integer currentNum;

    /**
     * 成团总人数
     */
    private Integer totalNum;

    /**
     * 订单总金额
     */
    private Long amount;

    /**
     * 拼团有效时间，单位：分钟，大于等于15
     */
    private Integer effectTimeout;

    /**
     * 拼团状态: ING 进行中, SUCCESS 成功, FAIL 失败
     */
    private TeamOrderStatus status;
    /**
     * 是否可以加入 默认都能参加
     */
    private Boolean canJoin=Boolean.TRUE;
}
