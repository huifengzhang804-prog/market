package com.medusa.gruul.carrier.pigeon.api.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * 消息内容类型
 *
 * @author 张治保
 * date 2022/4/29
 */
@Getter
@RequiredArgsConstructor
public enum MsgType {
    /**
     * 啥都没有 仅标题通知
     */
    NONE(0,false,false),
    /**
     * 文本
     */
    TEXT(1,false,true),
    /**
     * 富文本
     */
    RICH_TEXT(2,false,true),
    /**
     * 页面
     */
    PAGE(3,true,false),
    /**
     * 连接
     */
    LINK(4,true,false);


    @EnumValue
    private final Integer value;
    /**
     * 是否要有url
     */
    private final boolean hashUrl;
    /**
     * 是否要有content
     */
    private final boolean hashContent;
}
