package com.medusa.gruul.addon.live.model;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;


/**
 * @author miskw
 * @date 2023/6/16
 * @describe 直播间互动DTO
 */
@Getter
@Setter
@Accessors(chain = true)
public class LiveInteractionDTO {
    /**
     * 直播间id
     */
    @NotNull(message = "直播间id不能为空")
    private Long liveId;
    /**
     * 点赞数
     */
    private Integer likeCount = 0;
    /**
     * 观看人数
     */
    private Integer viewership = 0;
}
