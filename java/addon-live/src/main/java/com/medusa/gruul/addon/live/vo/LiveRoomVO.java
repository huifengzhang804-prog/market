package com.medusa.gruul.addon.live.vo;

import com.medusa.gruul.addon.live.mp.entity.Anchor;
import com.medusa.gruul.addon.live.mp.entity.BaseLive;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.math.BigDecimal;

/**
 * @author miskw
 * @date 2023/6/7
 * @describe 直播间列表
 */
@Data
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = true)
public class LiveRoomVO extends BaseLive {
    /**
     * 主播信息
     */
    private Anchor anchor;
    /**
     * '观看人数'
     */
    private Integer viewership;
    /**
     * '直播时长'
     */
    private BigDecimal duration;
}
