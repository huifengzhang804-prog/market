package com.medusa.gruul.user.api.model;

import lombok.Data;
import lombok.experimental.Accessors;

/**
 * @author: WuDi
 * @date: 2022/9/14
 */
@Data
@Accessors(chain = true)
public class UserTagVo {

    /**
     * 会员标签id
     */
    private Long id;
    /**
     * 会员标签名称
     */
    private String tagName;
    /**
     * 是否选中true选中,false未选中
     */
    private Boolean option;
}
