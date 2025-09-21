package com.medusa.gruul.user.service.model.vo;

import com.medusa.gruul.common.security.model.enums.Roles;
import com.medusa.gruul.service.uaa.api.enums.Gender;
import com.medusa.gruul.user.api.enums.MemberType;
import lombok.Data;
import lombok.experimental.Accessors;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 用户列表VO
 *
 * @author xiaoq
 * @Description UserListVO.java
 * @date 2022-11-25 14:56
 */
@Data
public class UserListVO {

    /**
     * 会员列表id
     */
    private Long id;

    /**
     * 用户id
     */
    private Long userId;
    /**
     * 姓名
     */
    private String userName;
    /**
     * 生日
     */
    private LocalDate birthday;

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
     * 用户权益
     */
    private List<Roles> userAuthority;

    /**
     * 用户手机号
     */
    private String userPhone;

    /**
     * 余额
     */
    private Long balance;

    /**
     * 成长值
     */
    private Long growthValue;

    /**
     * 用户总积分值
     */
    private Long integralTotal;

    /**
     * 交易总金额
     */
    private Long dealTotalMoney;

    /**
     * 注册时间
     */
    private LocalDateTime createTime;

    /**
     * 会员数据
     */
    private UserListVO.MemberVO member;

    /**
     * 会员标签集合
     */
    private List<UserListVO.TagVO> userTagVOList;

    /**
     * 会员类型
     */
    private MemberType memberType;

    /**
     * 会员级别
     */
    private Integer rankCode;

    /**
     * 拉黑处理说明
     */
    private String explain;
  /**
   * 最后交易时间
   */
  private String lastDealTime;

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

    @Data
    @Accessors(chain = true)
    public static class MemberVO {
        /**
         * 会员类型
         */
        private MemberType memberType;

        /**
         * 会员级别
         */
        private Integer rankCode;
    }
}
