package com.medusa.gruul.common.security.model.bean;

import com.medusa.gruul.common.security.model.enums.TokenState;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * @author 张治保
 * @since 2023/11/23
 */
@Getter
@Setter
@ToString
@Accessors(chain = true)
public class CacheToken implements Serializable {

    public static final String TOKEN_ID_FILED = "tokenId";
    public static final String STATE_FILED = "state";
    public static final String MESSAGE_FILED = "message";

    /**
     * token
     */
    private TokenState state;

    /**
     * 异常状态提示消息
     */
    private String message;
}
