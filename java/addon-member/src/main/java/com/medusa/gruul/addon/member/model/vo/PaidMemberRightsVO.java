package com.medusa.gruul.addon.member.model.vo;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.extension.handlers.Fastjson2TypeHandler;
import com.medusa.gruul.user.api.model.dto.MemberLabelDTO;
import com.medusa.gruul.user.api.model.dto.paid.PaidRuleJsonDTO;
import com.medusa.gruul.user.api.model.vo.RelevancyRightsVO;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;

import java.util.List;

/**
 * 付费会员权益VO
 *
 * @author xiaoq
 * @Description PaidMemberRightsVO.java
 * @date 2022-11-17 14:04
 */
@Getter
@Setter
@ToString
@Accessors(chain = true)
public class PaidMemberRightsVO {

    /**
     * List<会员关联权益VO>
     */
    List<RelevancyRightsVO> relevancyRightsList;
    /**
     * 付费会员id
     */
    private Long id;
    /**
     * 付费会员名称
     */
    private String paidMemberName;
    /**
     * 付费会员级别
     */
    private Integer rankCode;
    /**
     * 付费规则
     */
    @TableField(typeHandler = Fastjson2TypeHandler.class)
    private List<PaidRuleJsonDTO> paidRuleJson;
    /**
     * 标签
     */
    private MemberLabelDTO labelJson;

}
