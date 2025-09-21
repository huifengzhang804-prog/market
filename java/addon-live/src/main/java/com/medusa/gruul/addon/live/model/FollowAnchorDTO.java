package com.medusa.gruul.addon.live.model;

import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.experimental.Accessors;


/**
 * @author miskw
 * @date 2023/6/7
 * @describe 关注主播
 */
@Data
@Accessors(chain = true)
public class FollowAnchorDTO {
    /**
     * 主播id
     */
    @NotNull(message = "主播id不能为空")
    private Long anchorId;
    /**
     * 店铺id
     */
    @NotNull(message = "店铺id不能为空")
    private Long shopId;
    /**
     * 关注行为
     */
    @NotNull(message = "请选择关注或者取消关注")
    private Boolean isFollow;
}
