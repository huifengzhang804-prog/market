package com.medusa.gruul.search.service.model.vo;

import com.medusa.gruul.user.api.enums.MemberType;
import com.medusa.gruul.user.api.model.dto.MemberLabelDTO;
import com.medusa.gruul.user.api.model.vo.RelevancyRightsVO;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;

import java.util.List;

/**
 * 会员信息
 * @author wufuzhong
 * @date 2023-12-12
 */
@Getter
@Setter
@ToString
@Accessors(chain = true)
public class SearchMemberInfoVO {

    /**
     * 会员类型
     */
    private MemberType memberType;
    /**
     * 会员价标签
     */
    private MemberLabelDTO memberLabel;

    /**
     * 会员权益
     * 只展示会员折扣的
     */
    private List<RelevancyRightsVO> relevancyRights;
}
