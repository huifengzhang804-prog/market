package com.medusa.gruul.user.service.model.vo;

import lombok.Data;

/**
 * @author xiaoq
 * @since 2024/7/24
 *
 * 用户所属会员VO
 */
@Data
public class RankedMemberVO {
    /**
     * 用户id
     */
    private Long userId;
    /**
     * 用户成长值
     */
    private Long growthValue;

    /**
     * 用户免费会员id
     */
    private Long freeMemberId;

    /**
     * 会员级别
     */
    private Long rankCode;

}
