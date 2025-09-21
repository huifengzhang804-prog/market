package com.medusa.gruul.user.service.mp.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.handlers.Fastjson2TypeHandler;
import com.medusa.gruul.common.mp.model.BaseEntity;
import com.medusa.gruul.common.security.model.enums.Roles;
import com.medusa.gruul.service.uaa.api.enums.Gender;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;


/**
 * 会员信息表
 *
 * @author xiaoq
 * @Description Member.java
 * @date 2022-08-31 15:15
 */
@Getter
@Setter
@Accessors(chain = true)
@TableName(value = "t_user_account", autoResultMap = true)
public class UserAccount extends BaseEntity {

    /**
     * 用户id
     */
    @TableField("user_id")
    private Long userId;
    /**
     * 用户姓名
     */
    @TableField("user_name")
    private String userName;

    /**
     * 用户头像
     */
    @TableField("user_head_portrait")
    private String userHeadPortrait;
    /**
     * 用户昵称
     */
    @TableField("user_nickname")
    private String userNickname;

    /**
     * 用户性别
     */
    @TableField("gender")
    private Gender gender;

    /**
     * 用户权益
     */
    @TableField(value = "user_authority", typeHandler = Fastjson2TypeHandler.class)
    private List<Roles> userAuthority;

    /**
     * 用户手机号
     */
    @TableField("user_phone")
    private String userPhone;
    /**
     * 余额
     */
    @TableField("balance")
    private Long balance;


    /**
     * 成长值
     */
    @TableField("growth_value")
    private Long growthValue;


    /**
     * 备注信息
     */
    @TableField("remark")
    private String remark;

    /**
     * 消费次数
     */
    @TableField("consume_count")
    private Integer consumeCount;
    /**
     * 交易总金额
     */
    @TableField("deal_total_money")
    private Long dealTotalMoney;

    /**
     * 用户总积分值
     */
    @TableField("integral_total")
    private Long integralTotal;

    /**
     * 分销次数
     */
    @TableField("distribution_count")
    private Integer distributionCount;
    /**
     * 最后交易时间
     */
    @TableField("last_deal_time")
    private LocalDateTime lastDealTime;
    /**
     * 生日
     */
    @TableField("birthday")
    private LocalDate birthday;

    /**
     * 奖励成长值的支付订单数
     */
    @TableField("growth_pay_order_count")
    private Integer growthPayOrderCount;

    /**
     * 奖励成长值的实付金额
     */
    @TableField("growth_pay_order_money")
    private Long growthPayOrderMoney;

    /**
     * 用户拉黑说明
     */
    @TableField("`explain`")
    private String explain;

    /**
     * 查询用户会员权限是可能存在
     */
    @TableField(exist = false)
    private List<UserMemberCard> memberCards;


}
