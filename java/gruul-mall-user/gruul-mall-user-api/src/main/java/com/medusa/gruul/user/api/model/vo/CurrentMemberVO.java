package com.medusa.gruul.user.api.model.vo;

import com.medusa.gruul.user.api.enums.RightsType;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

/**
 * 当前所使用得会员信息
 *
 * @author xiaoq
 * @Description CurrentMemberVO.java
 * @date 2022-11-21 11:26
 */
@Getter
@Setter
@ToString
@Accessors(chain = true)
public class CurrentMemberVO implements Serializable {

    private static final long serialVersionUID = 1L;
    /**
     * 会员卡id
     */
    private Long memberCardId;

    /**
     * 会员卡有效时长
     */
    private LocalDate memberCardValidTime;

    /**
     * 当前会员级别
     */
    private Integer rankCode;

    /**
     * 会员名称
     */
    private String memberName;

    /**
     * 会员级别关联权益
     */
    private Map<RightsType, List<RelevancyRightsVO>> relevancyRights;

}
