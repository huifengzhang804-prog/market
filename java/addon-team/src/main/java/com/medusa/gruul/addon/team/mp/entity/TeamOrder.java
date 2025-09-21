package com.medusa.gruul.addon.team.mp.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.medusa.gruul.addon.team.model.enums.TeamOrderStatus;
import com.medusa.gruul.common.mp.model.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.time.LocalDateTime;

/**
 * <p>
 * 拼团订单表
 * </p>
 *
 * @author 张治保
 */
@TableName(value = "t_team_order", excludeProperty = "deleted")
@Data
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = false)
public class TeamOrder extends BaseEntity {


    /**
     * 店铺id
     */
    private Long shopId;

    /**
     * 活动id
     */
    private Long activityId;

    /**
     * 拼团有效时间，单位：分钟，大于等于15
     */
    private Integer effectTimeout;

    /**
     * 选择的团员数
     */
    private Integer num;

    /**
     * 拼团状态
     */
    @TableField("`status`")
    private TeamOrderStatus status;

    /**
     * 团号
     */
    private String teamNo;

    /**
     * 订单id
     */
    private String orderNo;

    /**
     * 商品id
     */
    private Long productId;

    /**
     * 商品数量
     */
    private Integer productNum;

    /**
     * 拼团价
     */
    private Long price;

    /**
     * 订单总金额
     */
    private Long amount;

    /**
     * 是否是团长
     */
    private Boolean commander;

    /**
     * 当前用户id
     */
    private Long userId;

    /**
     * 用户昵称
     */
    private String nickname;

    /**
     * 用户头像
     */
    private String avatar;

    /**
     * 开团时间
     */
    private LocalDateTime openTime;

}
