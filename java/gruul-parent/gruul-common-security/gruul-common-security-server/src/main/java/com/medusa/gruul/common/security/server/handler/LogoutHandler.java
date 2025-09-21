package com.medusa.gruul.common.security.server.handler;

import com.medusa.gruul.common.model.resp.Result;
import com.medusa.gruul.common.security.model.bean.SecureUser;
import com.medusa.gruul.common.security.resource.helper.ISecurity;
import com.medusa.gruul.common.security.resource.model.SecureUserAuthentication;
import com.medusa.gruul.common.security.resource.tool.ResponseUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.SneakyThrows;
import org.springframework.security.core.Authentication;

/**
 * 登出逻辑 清楚缓存数据
 *
 * @author 张治保
 * @since 2023/11/4
 */
public final class LogoutHandler implements org.springframework.security.web.authentication.logout.LogoutHandler {

    @Override
    @SneakyThrows
    public void logout(HttpServletRequest request, HttpServletResponse response, Authentication authentication) {
        Result<Object> resp = Result.ok();
        if (!(authentication instanceof SecureUserAuthentication secureUserAuthentication)) {
            ResponseUtil.response(response, resp);
            return;
        }
        SecureUser<?> user = secureUserAuthentication.user();
        //用户下线
        ISecurity.offlineUser(user.getClientType(), user.getShopId(), user.getId(), secureUserAuthentication.getTokenId());
        //
        ResponseUtil.response(response, resp);
    }
}
