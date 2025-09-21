package com.medusa.gruul.user.api.model.vo;

import com.medusa.gruul.user.api.enums.RightsType;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * 会员关联权益vo
 *
 * @author xiaoq
 * @Description RelevancyRightsVO.java
 * @date 2022-11-14 10:07
 */

@Getter
@Setter
@ToString
@Accessors(chain = true)
public class RelevancyRightsVO implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 会员权益id
     */
    private Long memberRightsId;

    /**
     * 会员权益扩展值
     */
    private Long extendValue;

    /**
     * 权益类型
     */
    private RightsType rightsType;

    /**
     * 权益名称
     */
    private String rightsName;

    /**
     * 权益说明
     */
    private String rightsExplain;

    /**
     * 权益图标
     */
    private String rightsIcon;

}
