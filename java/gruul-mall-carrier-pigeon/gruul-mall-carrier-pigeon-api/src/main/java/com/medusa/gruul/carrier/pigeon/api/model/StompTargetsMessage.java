package com.medusa.gruul.carrier.pigeon.api.model;

import jakarta.validation.constraints.NotEmpty;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;

import java.util.Set;

/**
 * @author 张治保
 * date 2022/4/29
 */
@Getter
@Setter
@ToString
@Accessors(chain = true)
public class StompTargetsMessage extends StompMessage {

    /**
     * 消息id 自动生成
     */
    private Long messageId;
    /**
     * 发送目标
     */
    @NotEmpty
    private Set<Long> targets;

    /**
     * 隐藏发送目标
     */
    public void hideTargets() {
        this.targets = null;
    }
}
