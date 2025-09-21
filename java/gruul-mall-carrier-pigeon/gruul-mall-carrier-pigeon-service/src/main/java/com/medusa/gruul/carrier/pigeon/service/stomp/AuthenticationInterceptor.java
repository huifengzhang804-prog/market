package com.medusa.gruul.carrier.pigeon.service.stomp;

import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson2.JSONObject;
import com.medusa.gruul.common.security.model.bean.SecureUser;
import com.medusa.gruul.common.security.model.enums.SecureCodes;
import com.medusa.gruul.common.security.resource.helper.ISecurity;
import com.medusa.gruul.common.security.resource.tool.jwt.JwtDecoder;
import com.medusa.gruul.global.model.exception.Error;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.lang.NonNull;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.MessagingException;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;


/**
 * @author 张治保
 * date 2022/5/6
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class AuthenticationInterceptor implements ChannelInterceptor {

    private final JwtDecoder jwtDecoder;

    @Override
    public Message<?> preSend(@NonNull Message<?> message, @NonNull MessageChannel channel) {

        StompHeaderAccessor accessor = StompHeaderAccessor.wrap(message);
        StompCommand command = accessor.getCommand();
        if (command == null) {
            return message;
        }
        switch (command) {
            case CONNECT -> authentication(accessor);
            case SUBSCRIBE -> {
                Authentication authentication = authentication(accessor);
                //accessor.getDestination()
                boolean success = ISecurity.withAuthentication(
                        authentication,
                        () -> ISecurity.matcher().eq(SecureUser::getPerms, accessor.getDestination()).match()
                );
                if (!success) {
                    throw messagingException(SecureCodes.ACCESS_DENIED);
                }
            }
            case COMMIT -> throw messagingException(SecureCodes.ACCESS_DENIED);
            default -> {
            }
        }
        return message;
    }

    public MessagingException messagingException(Error error) {
        return new MessagingException(
                new JSONObject()
                        .fluentPut("code", error.code())
                        .fluentPut("msg", error.msg())
                        .toString()
        );
    }

    @Override
    public void afterSendCompletion(@NonNull Message<?> message, @NonNull MessageChannel channel, boolean sent, Exception ex) {
        SecurityContextHolder.clearContext();
    }

    private Authentication authentication(StompHeaderAccessor accessor) {
        Authentication authentication = tokenValid(accessor.getFirstNativeHeader(ISecurity.HEADER));
        if (authentication == null) {
            throw messagingException(SecureCodes.TOKEN_INVALID);
        }
        return authentication;
    }

    private Authentication tokenValid(String token) {
        if (StrUtil.isBlank(token)) {
            return null;
        }
        try {
            return jwtDecoder.authentication(token);
        } catch (Exception exception) {
            return null;
        }
    }
}
