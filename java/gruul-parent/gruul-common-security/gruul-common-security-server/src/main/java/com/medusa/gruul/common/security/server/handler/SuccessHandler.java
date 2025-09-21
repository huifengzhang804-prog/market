package com.medusa.gruul.common.security.server.handler;

import com.medusa.gruul.common.model.resp.Result;
import com.medusa.gruul.common.security.model.bean.SecureUser;
import com.medusa.gruul.common.security.model.enums.SecureCodes;
import com.medusa.gruul.common.security.resource.helper.ISecurity;
import com.medusa.gruul.common.security.resource.model.OnlineUserParam;
import com.medusa.gruul.common.security.resource.model.SecureUserAuthentication;
import com.medusa.gruul.common.security.resource.tool.ResponseUtil;
import com.medusa.gruul.common.security.server.model.AuthenticationResp;
import com.medusa.gruul.common.security.server.tool.JwtEncoder;
import com.medusa.gruul.common.system.model.model.ClientType;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;

import java.io.IOException;
import java.util.Set;

/**
 * @author 张治保
 * @since 2023/11/2
 */
@RequiredArgsConstructor
public final class SuccessHandler implements AuthenticationSuccessHandler {


    private final boolean repeatLogin;
    private final JwtEncoder jwtEncoder;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException {
        SecureUserAuthentication secureUserAuthentication = (SecureUserAuthentication) authentication;
        SecureUser<?> user = secureUserAuthentication.user();
        ClientType clientType = user.getClientType();
        Long shopId = user.getShopId();
        Long userId = user.getId();
        if (clientType == null || shopId == null || userId == null) {
            ResponseUtil.response(response, Result.failed(SecureCodes.ACCOUNT_INVALID));
            return;
        }
        AuthenticationResp token = jwtEncoder.encode(secureUserAuthentication);
        if (!repeatLogin) {
            //用户下线
            ISecurity.kickUsers(clientType, shopId, Set.of(userId), SecureCodes.TOKEN_CHANGED.msg());
        }
        //用户上线
        ISecurity.onlineUser(
                new OnlineUserParam(
                        clientType,
                        shopId,
                        userId,
                        secureUserAuthentication.tokenId(),
                        token.getRefreshToken().getExpireAt()
                )
        );
        //返回token
        ResponseUtil.response(response, Result.ok(token));
    }
}
