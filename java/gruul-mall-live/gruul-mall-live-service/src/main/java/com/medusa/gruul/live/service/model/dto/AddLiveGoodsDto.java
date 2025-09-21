package com.medusa.gruul.live.service.model.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;

/**
 * @author miskw
 * @date 2022/11/15
 * @Describe 直播间导入商品
 */
@Data
public class AddLiveGoodsDto {
    /**
     * 微信GoodsId
     */
    @NotNull(message = "微信GoodsId不能为空")
    private List<Long> goodsIds;
    /**
     * 微信roomId
     */
    @NotNull(message = "微信roomId不能为空")
    private Long roomId;
}
