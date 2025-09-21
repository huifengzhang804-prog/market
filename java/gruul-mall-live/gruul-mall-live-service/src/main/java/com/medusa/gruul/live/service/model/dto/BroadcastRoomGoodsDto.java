package com.medusa.gruul.live.service.model.dto;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import jakarta.validation.constraints.NotNull;
import lombok.Data;


/**
 * @author miskw
 * @date 2022/11/18
 * @describe 直播间商品dto
 */
@Data
public class BroadcastRoomGoodsDto extends Page<Object> {
    /**
     * 商品名称
     */
    private String keywords;

    /**
     *
     */
    @NotNull(message = "请选择直播间")
    private Long roomId;

}
