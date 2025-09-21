package com.medusa.gruul.user.service.model.vo;

import com.medusa.gruul.service.uaa.api.enums.Gender;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.List;


/**
 * @author wudi
 * ShopUserDetailVO.class
 */
@Getter
@Setter
@ToString
public class ShopUserDetailVO {

    /**
     * id
     */
    private Long id;

    /**
     * 用户id
     */
    private Long userId;
    /**
     * 用户姓名
     */
    private String userName;

    /**
     * 用户头像
     */
    private String userHeadPortrait;
    /**
     * 用户昵称
     */
    private String userNickname;

    /**
     * 用户性别
     */
    private Gender gender;

    /**
     * 店铺id
     */
    private Long shopId;

    /**
     * 用户手机号
     */
    private String userPhone;


    /**
     * 成长值
     */
    private Long growthValue;


    /**
     * 会员标签
     */
    public List<ShopUserVO.TagVO> tags;
}
