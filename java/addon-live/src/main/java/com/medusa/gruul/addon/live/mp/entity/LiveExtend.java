package com.medusa.gruul.addon.live.mp.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.medusa.gruul.common.mp.model.BaseEntity;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.math.BigDecimal;

/**
 * @author miskw
 * @date 2023/6/5
 * @describe 主播扩展表
 */
@Getter
@Setter
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = true)
@TableName("t_live_extend")
public class LiveExtend extends BaseEntity {
    //'主播表id'
    private Long liveId;
    //店铺id
    private Long shopId;
    //'观看人数'
    private Integer viewership;
    //'直播时长' 分钟
    private BigDecimal duration;
    //'点赞数'
    private Integer likeCount;
}
