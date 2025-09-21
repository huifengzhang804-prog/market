package com.medusa.gruul.carrier.pigeon.service.im.entity;

import com.medusa.gruul.carrier.pigeon.api.enums.MessageType;
import com.medusa.gruul.common.mp.model.BaseEntity;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

import static com.medusa.gruul.carrier.pigeon.service.im.constants.DateConstant.DATE_FORMAT;

/**
 * <p>消息实体类</p>
 * @author An.Yan
 */
@Getter
@Setter
@Accessors(chain = true)
public class ChatMessage extends BaseEntity {

    private static final long serialVersionUID = 1L;

    private ChatMessageSender sender;

    private ChatMessageReceiver receiver;

    /**
     * 消息媒体类型
     */
    private MessageType messageType;

    /**
     * 内容
     */
    private String message;

    /**
     * 是否已读
     */
    private Boolean read;

    private Long sendTime;

    /**
     * 是否是已被处理
     */
    private Boolean handled;

    /**
     * 是否展示红点提示
     */
    private Boolean show;

    /**
     * 检查聊天室的创建日期与{@code target}是否匹配
     * @param target 目标日期
     * @return true:匹配;false:不匹配
     */
    public Boolean checkWhetherCreatedToday(LocalDateTime target) {
        DateTimeFormatter dfDate = DateTimeFormatter.ofPattern(DATE_FORMAT);
        return dfDate.format(LocalDateTime.ofInstant(Instant.ofEpochMilli(this.sendTime), ZoneId.systemDefault())).equals(dfDate.format(target));
    }
}
