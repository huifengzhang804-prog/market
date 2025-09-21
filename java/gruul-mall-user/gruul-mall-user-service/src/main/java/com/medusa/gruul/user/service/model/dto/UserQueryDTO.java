package com.medusa.gruul.user.service.model.dto;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.medusa.gruul.user.api.enums.MemberType;
import com.medusa.gruul.user.service.model.enums.UserSortEnum;
import com.medusa.gruul.user.service.mp.entity.UserAccount;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;

import java.util.Set;

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
public class UserQueryDTO extends Page<UserAccount> {

    /**
     * 店铺id
     */
    private Long shopId;

    /**
     * 用户昵称
     */
    private String userNickname;

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
//
//    /**
//     * 排序类型 1-交易总额降序 2-交易总额升序 3-会员时间升序 4-会员时间降序
//     */
//    private String sortType;
    /**
     * 排序
     */
    private UserSortEnum sortType;

    /**
     * 交易总金额
     */
    private Long dealTotalMoney;

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


    /**
     * id集合
     */
    private Set<Long> ids;

    /**
     * 用户手机号
     */
    private Long userPhone;
}
