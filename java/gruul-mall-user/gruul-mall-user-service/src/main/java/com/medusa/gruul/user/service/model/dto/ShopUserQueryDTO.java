package com.medusa.gruul.user.service.model.dto;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.medusa.gruul.user.api.enums.MemberType;
import com.medusa.gruul.user.service.mp.entity.ShopUserAccount;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;

/**
 * 会员查询DTO
 *
 * @author: WuDi
 * @date: 2022/9/14
 */
@Getter
@Setter
@ToString
@Accessors(chain = true)
public class ShopUserQueryDTO extends Page<ShopUserAccount> {

    /**
     * 店铺id
     */
    private Long shopId;

    /**
     * 用户昵称
     */
    private String userNickname;

    /**
     * 用户手机号
     */
    private String userPhone;

    /**
     * 注册开始时间
     */
    private String registrationStartTime;
    /**
     * 注册结束时间
     */
    private String registrationEndTime;

    /**
     * 生日开始时间
     */
    private String birthdayStartTime;

    /**
     * 生日结束时间
     */
    private String birthdayEndTime;

    /**
     * 会员标签id
     */
    private Long tagId;


    /**
     * 成为会员时间
     */
    private String becomeUserTime;

    /**
     * 会员类型
     */
    private MemberType memberType;

    /**
     * 会员等级
     */
    private Integer rankCode;
}
