package com.medusa.gruul.user.service.model.vo;

import com.medusa.gruul.service.uaa.api.enums.Gender;
import com.medusa.gruul.user.api.enums.MemberType;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@ToString
public class ShopUserVO {

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
     * 备注信息
     */
    private String remark;

    /**
     * 消费次数
     */
    private Integer consumeCount;

    /**
     * 本店消费
     */
    private Long shopConsumption;

    /**
     * 最后交易时间
     */
    private LocalDateTime lastDealTime;

    /**
     * 注册时间
     */
    private LocalDateTime registerTime;

    /**
     * 生日
     */
    private LocalDate birthday;

    /**
     * 奖励成长值的支付订单数
     */
    private Integer growthPayOrderCount;

    /**
     * 奖励成长值的实付金额
     */
    private Long growthPayOrderMoney;

    /**
     * 会员类型
     */
    private MemberType memberType;

    /**
     * 会员级别
     */
    private Integer rankCode;


    /**
     * 会员标签
     */
    private List<ShopUserVO.TagVO> tags;

    @Data
    @Accessors(chain = true)
    public static class TagVO {
        /**
         * 会员标签id
         */
        private Long tagId;

        /**
         * 会员标签名称
         */
        private String tagName;
    }

}
