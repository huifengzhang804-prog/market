package com.medusa.gruul.addon.live.model;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * @author miskw
 * @date 2023/6/5
 * @describe 立即创建直播间
 */
@Data
public class PreviewLiveRoomInstantDTO {
    /**
     * 标题
     */
    @Size(max = 10, message = "直播主题长度不能超过10个字")
    @NotNull(message = "直播主题不能为空")
    private String title;
    /**
     * 简介
     */
    @Size(max = 15, message = "直播简介不能超过15个字")
    private String liveSynopsis;
    /**
     * 封面图
     */
    @NotNull(message = "直播背景图不能为空")
    private String pic;
    /**
     * 用户id
     */
    private Long userId;

}
