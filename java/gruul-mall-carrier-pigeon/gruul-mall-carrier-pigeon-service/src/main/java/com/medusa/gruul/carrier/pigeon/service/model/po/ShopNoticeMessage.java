package com.medusa.gruul.carrier.pigeon.service.model.po;

import com.medusa.gruul.carrier.pigeon.api.enums.NoticeType;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.util.Set;

/**
 * 店铺消息bean
 * @author 张治保
 * date 2022/5/10
 */
@Getter
@Setter
@Accessors(chain = true)
public class ShopNoticeMessage {
    /**
     * 消息类型
     */
    private NoticeType type;
    /**
     * 消息标题
     */
    private String title;
    /**
     * 消息摘要
     */
    private String summary;
    /**
     * 消息内容
     */
    private String content;

    /**
     * 店铺id列表  为空 则群发所有店铺
     */
    private Set<Long> shopIds;

}
