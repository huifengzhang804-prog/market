package com.medusa.gruul.carrier.pigeon.api.model;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import com.medusa.gruul.carrier.pigeon.api.enums.Channel;
import com.medusa.gruul.carrier.pigeon.api.enums.MsgType;
import com.medusa.gruul.carrier.pigeon.api.enums.NoticeType;
import com.medusa.gruul.carrier.pigeon.api.enums.SendType;
import com.medusa.gruul.common.model.resp.SystemCode;
import com.medusa.gruul.global.model.exception.GlobalException;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * @author 张治保
 * date 2022/4/29
 */
@Getter
@Setter
@ToString
@Accessors(chain = true)
public class StompMessage implements Serializable {

    /**
     * 消息类型
     */
    @NotNull
    private NoticeType noticeType;
    /**
     * 消息发送类型
     */
    private SendType sendType;
    /**
     * 消息频道
     */
    @NotNull
    private Channel channel;
    /**
     * 内容类型
     */
    @NotNull
    private MsgType msgType;
    /**
     * 消息标题
     */
    @NotBlank
    @Size(min = 2, max = 64)
    private String title;
    /**
     * 消息摘要
     */
    @Size(max = 150)
    private String summary;
    /**
     * 消息url连接 当msgType 为link时不为空
     */
    private String url;
    /**
     * 消息内容
     */
    private String content;

    /**
     * 创建消息的用户id
     */
    private Long createBy;


    /**
     * 尝试转换为带目标用户的消息
     */
    private StompTargetsMessage toStompTargets() {
        if (this instanceof StompTargetsMessage) {
            return (StompTargetsMessage) this;
        }
        return null;
    }

    public void checkParam() {
        StompTargetsMessage targetsMessage = toStompTargets();
        SendType sendType = getSendType();
        boolean isEmptyTargets = targetsMessage == null || CollUtil.isEmpty(targetsMessage.getTargets());
        if (sendType.getMarked() && isEmptyTargets) {
            throw new GlobalException(SystemCode.PARAM_VALID_ERROR);
        }
        MsgType msgType = getMsgType();
        if (msgType.isHashUrl() && StrUtil.isBlank(getUrl())) {
            throw new GlobalException(SystemCode.PARAM_VALID_ERROR);
        }
        if (msgType.isHashContent() && StrUtil.isBlank(getContent())) {
            throw new GlobalException(SystemCode.PARAM_VALID_ERROR);
        }

    }


}
