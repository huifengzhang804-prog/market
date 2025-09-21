package com.medusa.gruul.user.api.model.vo;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * 会员基础权益关联VO
 *
 * @author xiaoq
 * @Description MemberBasicsRelevancyRightsVO.java
 * @date 2022-11-21 17:34
 */
@Getter
@Setter
@ToString
@Accessors(chain = true)
public class MemberBasicsRelevancyRightsVO implements Serializable {
    private static final long serialVersionUID = 1L;
    /**
     * 会员权益id
     */
    private Long memberRightsId;

    /**
     * 会员权益扩展值
     */
    private Long extendValue;
}
