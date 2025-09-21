package com.medusa.gruul.goods.api.model.vo;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * @author 张治保
 * @since 2024/11/15
 */
@Getter
@Setter
@ToString
@Accessors(chain = true)
public class ShopFollowVO implements Serializable {

    /**
     * 关注人数
     */
    private Long follow;

    /**
     * 当前用户是否关注
     */
    private Boolean isFollow;

}
