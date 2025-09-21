package com.medusa.gruul.user.service.mp.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.medusa.gruul.common.mp.model.BaseEntity;
import com.medusa.gruul.service.uaa.api.enums.Gender;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.io.Serial;
import java.time.LocalDate;

/**
 *
 * @author WuDi
 * @since 2023-05-17
 */
@Getter
@Setter
@TableName("t_shop_user_account")
@Accessors(chain = true)
public class ShopUserAccount extends BaseEntity {

    @Serial
    private static final long serialVersionUID = 1L;

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
     * 店铺id
     */
    @TableField("shop_id")
    private Long shopId;

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
     * 本店消费
     */
    @TableField("shop_consumption")
    private Long shopConsumption;

    /**
     * 用户总积分值
     */
    @TableField("integral_total")
    private Long integralTotal;

    /**
     * 生日
     */
    @TableField("birthday")
    private LocalDate birthday;

}
