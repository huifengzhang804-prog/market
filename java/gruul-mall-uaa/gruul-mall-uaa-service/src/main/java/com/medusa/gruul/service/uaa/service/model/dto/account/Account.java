package com.medusa.gruul.service.uaa.service.model.dto.account;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.medusa.gruul.common.mp.model.BaseEntity;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.time.LocalDateTime;


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
public class Account extends BaseEntity {

    /**
     * 用户id
     */
    @TableField("user_id")
    private Long userId;
    /**
     * 用户姓名
     */
    private String username;

    private String headImgUrl="";

    private String birthday="";

    private String sex="";

    private String nickName="";

    private String type="";

    private String weixinName="";

    private long numUserId;

    private String city="";

    private String levelId="";

    /**
     * 用户头像
     */
    @TableField("password")
    private String password;
    /**
     * 用户昵称
     */
    @TableField("paymentPsw")
    private String paymentPsw;

    /**
     * 用户性别
     */
    @TableField("phone")
    private String phone;


    private String referralCode;

    @TableField("status")
    private int status;

    @TableField("create_time")
    private LocalDateTime createTime;

    @TableField("update_time")
    private LocalDateTime updateTime;



}
