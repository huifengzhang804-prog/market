package com.medusa.gruul.user.api.model.vo;

import com.baomidou.mybatisplus.annotation.TableField;
import com.medusa.gruul.user.api.model.dto.MemberLabelDTO;
import com.medusa.gruul.user.api.model.dto.paid.PaidRuleJsonDTO;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.List;

/**
 * 付费会员vo
 *
 * @author xiaoq
 * @Description PaidMemberInfoVO.java
 * @date 2022-11-21 17:21
 */

@Getter
@Setter
@ToString
@Accessors(chain = true)
public class PaidMemberInfoVO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 使用付费会员级别下标
     */
    private Long memberId;

    /**
     * 付费会员级别名称
     */
    private String paidMemberName;

    /**
     * 会员关联基础权益List<VO>
     */
    private List<MemberBasicsRelevancyRightsVO> memberBasicsRelevancyRightsList;


    /**
     * 不可用的会员ids
     */
    private List<Long> memberCloseIds;

    /**
     * 付费会员级别
     */
    private Integer rankCode;

    /**
     * 付费规则
     */
    private List<PaidRuleJsonDTO> paidRuleJson;

    /**
     * 会员价标签
     */
    private MemberLabelDTO memberLabel;
}
