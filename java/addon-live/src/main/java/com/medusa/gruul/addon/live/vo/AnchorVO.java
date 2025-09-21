package com.medusa.gruul.addon.live.vo;

import com.medusa.gruul.addon.live.enums.AnchorStatus;
import com.medusa.gruul.service.uaa.api.enums.Gender;
import lombok.Data;
import lombok.experimental.Accessors;

import java.math.BigDecimal;

/**
 * @author miskw
 * @date 2023/6/9
 * @describe 主播vo
 */
@Data
@Accessors(chain = true)
public class AnchorVO {
    /**
     * 主播id
     */
    private Long id;
    /**
     * 店铺id
     */
    private Long shopId;
    /**
     * 主播昵称
     */
    private String anchorNickname;
    /**
     * 主播简介
     */
    private String anchorSynopsis;
    /**
     * 主播头像
     */
    private String anchorIcon;
    /**
     * 主播状态
     */
    private AnchorStatus status;
    /**
     * 性别
     */
    private Gender gender;
    /**
     * 手机号
     */
    private String phone;
    /**
     * 粉丝数量
     */
    private Integer followCount;
    /**
     * 观看数量
     */
    private Integer viewership;
    /**
     * 直播时长
     */
    private BigDecimal duration;


}
