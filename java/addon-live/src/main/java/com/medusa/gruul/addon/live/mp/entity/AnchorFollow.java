package com.medusa.gruul.addon.live.mp.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.medusa.gruul.common.mp.model.BaseEntity;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

/**
 * @author miskw
 * @date 2023/6/5
 * @describe 用户关注主播
 */
@EqualsAndHashCode(callSuper = true)
@Getter
@Setter
@Accessors(chain = true)
@TableName("t_anchor_follow")
public class AnchorFollow extends BaseEntity {
    //主播id
    private Long anchorId;
    //店铺id
    private Long shopId;
    //用户id
    private Long userId;
}
