package com.medusa.gruul.user.service.model.vo;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;

/**
 * @author 张治保
 * date 2023/2/20
 */
@Getter
@Setter
@ToString
@Accessors(chain = true)
public class UserTagMapVO {

    /**
     * 用户id
     */
    private Long userId;

    /**
     * 会员标签id
     */
    private Long tagId;

    /**
     * 会员标签名称
     */
    private String tagName;
}
