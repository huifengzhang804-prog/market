package com.medusa.gruul.user.service.model.vo;

import com.medusa.gruul.user.api.model.dto.MemberLabelDTO;
import com.medusa.gruul.user.api.model.vo.RelevancyRightsVO;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;

import java.util.List;

/**
 * 免费级别会员权益VO
 *
 * @author xiaoq
 * @Description FreeMemberRightsVO.java
 * @date 2022-11-14 10:03
 */

@Getter
@Setter
@ToString
@Accessors(chain = true)
public class FreeMemberRightsVO {

    /**
     * 免费会员id
     */
    private Long id;


    /**
     * 免费会员名称
     */
    private String freeMemberName;


    /**
     * 所需成长值
     */
    private Long needValue;


    /**
     * List<免费会员关联权益VO>
     */
    List<RelevancyRightsVO> relevancyRightsList;

    /**
     * 会员标签
     */
    private MemberLabelDTO labelJson;
}
