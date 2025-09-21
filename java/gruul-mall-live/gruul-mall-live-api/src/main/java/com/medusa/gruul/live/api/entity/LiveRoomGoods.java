package com.medusa.gruul.live.api.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.medusa.gruul.common.mp.model.BaseEntity;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * @author miskw
 * @date 2022/11/14
 * @Describe 直播间商品关联表
 */
@Data
@TableName("t_live_room_goods")
@Accessors(chain = true)
public class LiveRoomGoods extends BaseEntity {
    /**
     * 直播商品Id
     */
    @TableField("live_goods_examine_id")
    private Long liveGoodsExamineId;
    /**
     * 直播间Id
     */
    @TableField("live_room_id")
    private Long liveRoomId;
}
