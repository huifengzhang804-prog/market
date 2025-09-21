package com.medusa.gruul.common.system.model.filter;

import cn.hutool.core.text.StrPool;
import cn.hutool.core.util.StrUtil;
import com.medusa.gruul.common.system.model.constant.SystemHeaders;
import com.medusa.gruul.common.system.model.context.SystemContextHolder;
import com.medusa.gruul.common.system.model.model.ClientType;
import com.medusa.gruul.common.system.model.model.Platform;
import com.medusa.gruul.common.system.model.model.Systems;
import com.medusa.gruul.common.system.model.util.IpUtils;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;

import java.io.IOException;

/**
 * 当前app请求的版本号 与客户端ip
 *
 * @author 张治保
 * date 2021/12/3
 */
@Slf4j
@Order(Integer.MIN_VALUE + 100)
public class SystemFilter extends AbstractServletFilter {

    private static final String ICO = "/favicon.ico";

    public static void setContextHolder(
            String uri,
            String ip,
            String deviceId,
            String clientTypeStr,
            String version,
            String providerId,
            String shopIdStr,
            String platformStr
    ) {
        String logFormat = """
                \nURI    : {}
                Client : [ip:{},deviceId:{},clientType:{},platform:{}],
                Tenants: [shopId:{}]""";
        log.debug(
                logFormat,
                uri, ip, deviceId, clientTypeStr, platformStr, shopIdStr
        );

        //处理数据
        Platform platform = StrUtil.isBlank(platformStr) ? null : Platform.valueOf(platformStr);
        Long platformId = StrUtil.isBlank(providerId) ? null : Long.valueOf(providerId);
        ClientType clientType = StrUtil.isBlank(clientTypeStr) ? null : ClientType.valueOf(clientTypeStr);
        Long shopId = StrUtil.isBlank(shopIdStr) ? null : Long.valueOf(shopIdStr);
        // 设置上下文中
        SystemContextHolder.set(
                new Systems()
                        .setVersion(version)
                        .setPlatform(platform)
                        .setIp(ip)
                        .setDeviceId(deviceId)
                        .setClientType(clientType)
                        .setPlatformId(platformId)
                        .setShopId(shopId)
        );

    }

    public static void clearAll() {
        SystemContextHolder.clear();
    }

    @Override
    public void doFilter(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws IOException, ServletException {
        if (ICO.equals(request.getRequestURI())) {
            response.setStatus(HttpStatus.NOT_FOUND.value());
            response.getWriter().flush();
            return;
        }

        SystemFilter.setContextHolder(
                request.getMethod() + StrPool.C_SPACE + request.getRequestURI(),
                IpUtils.getIpAddr(request),
                request.getHeader(SystemHeaders.DEVICE_ID),
                request.getHeader(SystemHeaders.CLIENT_TYPE),
                request.getHeader(SystemHeaders.VERSION),
                request.getHeader(SystemHeaders.PROVIDER_ID),
                request.getHeader(SystemHeaders.SHOP_ID),
                request.getHeader(SystemHeaders.PLATFORM)
        );
        try {
            filterChain.doFilter(request, response);
        } finally {
            clearAll();
        }
    }
}
