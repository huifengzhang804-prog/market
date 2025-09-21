# com.medusa.gruul.common.system.model.ISystem

**文件路径**: `system\model\ISystem.java`

## 代码文档
///
@author 张治保
date 2022/2/11
 
///

///
获取系统信息

@return 可能为空的系统信息
     
///

///
获取系统信息

@return 系统信息
     
///

///
平台

@return 可能为空的平台
     
///

///
平台

@return 平台
     
///

///
客户端ip

@return 可能为空的ip
     
///

///
客户端ip

@return ip
     
///

///
设备id

@return 可能为空的设备id
     
///

///
设备id

@return 设备id
     
///

///
客户端类型

@return 可能为空的客户端类型
     
///

///
客户端类型

@return 客户端类型
     
///

///
请求版本号

@return 可能为空的版本号
     
///

///
请求版本号

@return 版本号
     
///

///
租户id 可能为空

@return 租户id 可能为空
     
///

///
租户id

@return 租户id
     
///

///
shopId 可能为空

@return shopId 可能为空
     
///

///
shopId

@return shopId
     
///

///
修改shopId 并执行后续操作
并且 在后续操完成后 修改回原来的shopId

@param shopId  shopId
@param factory 后续操作
@param <T>     后续操作返回值类型
@return 后续操作返回值
     
///

///
修改shopId 并执行后续操作
并且 在后续操完成后 修改回原来的shopId

@param shopId   shopId
@param runnable 后续操作
     
///


## 文件结构
```
项目根目录
└── system\model
    └── ISystem.java
```

## 完整代码
```java
package com.medusa.gruul.common.system.model;

import com.medusa.gruul.common.system.model.context.SystemContextHolder;
import com.medusa.gruul.common.system.model.model.ClientType;
import com.medusa.gruul.common.system.model.model.Platform;
import com.medusa.gruul.common.system.model.model.Systems;
import com.medusa.gruul.global.model.constant.GlobalCode;
import com.medusa.gruul.global.model.exception.GlobalException;
import io.vavr.control.Option;

import java.util.function.Supplier;

/**
 * @author 张治保
 * date 2022/2/11
 */
public interface ISystem {

    /**
     * 获取系统信息
     *
     * @return 可能为空的系统信息
     */
    static Option<Systems> systemOpt() {
        return Option.of(SystemContextHolder.get());
    }

    /**
     * 获取系统信息
     *
     * @return 系统信息
     */
    static Systems systemMust() {
        return systemOpt().getOrElseThrow(() -> new GlobalException(GlobalCode.REQUEST_INVALID, "bad request"));
    }

    /**
     * 平台
     *
     * @return 可能为空的平台
     */
    static Option<Platform> platformOpt() {
        return systemOpt().map(Systems::getPlatform);
    }

    /**
     * 平台
     *
     * @return 平台
     */
    static Platform platformMust() {
        return platformOpt().getOrElseThrow(() -> new GlobalException(GlobalCode.REQUEST_INVALID, "bad request"));
    }

    /**
     * 客户端ip
     *
     * @return 可能为空的ip
     */
    static Option<String> ipOpt() {
        return systemOpt().map(Systems::getIp);
    }

    /**
     * 客户端ip
     *
     * @return ip
     */
    static String ipMust() {
        return ipOpt().getOrElseThrow(() -> new GlobalException(GlobalCode.REQUEST_INVALID, "bad request"));
    }


    /**
     * 设备id
     *
     * @return 可能为空的设备id
     */
    static Option<String> deviceIdOpt() {
        return systemOpt().map(Systems::getDeviceId);
    }

    /**
     * 设备id
     *
     * @return 设备id
     */
    static String deviceIdMust() {
        return deviceIdOpt().getOrElseThrow(() -> new GlobalException(GlobalCode.REQUEST_INVALID, "bad request"));
    }

    /**
     * 客户端类型
     *
     * @return 可能为空的客户端类型
     */
    static Option<ClientType> clientTypeOpt() {
        return systemOpt().map(Systems::getClientType);
    }

    /**
     * 客户端类型
     *
     * @return 客户端类型
     */
    static ClientType clientTypeMust() {
        return clientTypeOpt().getOrElseThrow(() -> new GlobalException(GlobalCode.REQUEST_INVALID, "bad request"));
    }

    /**
     * 请求版本号
     *
     * @return 可能为空的版本号
     */
    static Option<String> versionOpt() {
        return systemOpt().map(Systems::getVersion);
    }

    /**
     * 请求版本号
     *
     * @return 版本号
     */
    static String versionMust() {
        return versionOpt().getOrElseThrow(() -> new GlobalException(GlobalCode.REQUEST_INVALID, "bad request"));
    }

    /**
     * 租户id 可能为空
     *
     * @return 租户id 可能为空
     */
    static Option<Long> platformIdOpt() {
        return systemOpt().map(Systems::getPlatformId);
    }

    /**
     * 租户id
     *
     * @return 租户id
     */
    static Long platformIdMust() {
        return platformIdOpt().getOrElseThrow(() -> new GlobalException(GlobalCode.REQUEST_INVALID, "bad request"));
    }

    /**
     * shopId 可能为空
     *
     * @return shopId 可能为空
     */
    static Option<Long> shopIdOpt() {
        return systemOpt().map(Systems::getShopId);
    }

    /**
     * shopId
     *
     * @return shopId
     */
    static Long shopIdMust() {
        return shopIdOpt().getOrElseThrow(() -> new GlobalException(GlobalCode.REQUEST_INVALID, "bad request"));
    }

    /**
     * 修改shopId 并执行后续操作
     * 并且 在后续操完成后 修改回原来的shopId
     *
     * @param shopId  shopId
     * @param factory 后续操作
     * @param <T>     后续操作返回值类型
     * @return 后续操作返回值
     */
    static <T> T shopId(Long shopId, Supplier<T> factory) {
        Systems systems = ISystem.systemOpt()
                .getOrElse(() -> {
                    Systems newSystems = new Systems();
                    SystemContextHolder.set(newSystems);
                    return newSystems;
                });
        Long preShopId = systems.getShopId();
        systems.setShopId(shopId);
        try {
            return factory.get();
        } finally {
            if (preShopId == null) {
                SystemContextHolder.clear();
            } else {
                systems.setShopId(preShopId);
            }
        }
    }

    /**
     * 修改shopId 并执行后续操作
     * 并且 在后续操完成后 修改回原来的shopId
     *
     * @param shopId   shopId
     * @param runnable 后续操作
     */
    static void shopId(Long shopId, Runnable runnable) {
        ISystem.shopId(shopId, () -> {
            runnable.run();
            return null;
        });
    }
}

```

# com.medusa.gruul.common.system.model.SystemAutoConfigure

**文件路径**: `system\model\SystemAutoConfigure.java`

## 代码文档
///
@author 张治保
date 2022/2/11
 
///


## 文件结构
```
项目根目录
└── system\model
    └── SystemAutoConfigure.java
```

## 完整代码
```java
package com.medusa.gruul.common.system.model;

import com.medusa.gruul.common.system.model.remote.SystemFeignRequestInterceptor;
import com.medusa.gruul.common.system.model.filter.SystemFilter;
import com.medusa.gruul.common.system.model.remote.SystemRestTemplateRequestInterceptor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.context.annotation.Import;

/**
 * @author 张治保
 * date 2022/2/11
 */
@Import(
    {
        SystemFilter.class,
        SystemFeignRequestInterceptor.class,
        SystemRestTemplateRequestInterceptor.class
    }
)
@ConditionalOnClass(name = "jakarta.servlet.Filter")
public class SystemAutoConfigure {
}

```

# com.medusa.gruul.common.system.model.context.SystemContextHolder

**文件路径**: `model\context\SystemContextHolder.java`

## 代码文档
///
客户端ip地址上下文
@author 张治保
date 2021/12/15
 
///


## 文件结构
```
项目根目录
└── model\context
    └── SystemContextHolder.java
```

## 完整代码
```java
package com.medusa.gruul.common.system.model.context;

import com.alibaba.ttl.TransmittableThreadLocal;
import com.medusa.gruul.common.system.model.model.Systems;

/**
 * 客户端ip地址上下文
 * @author 张治保
 * date 2021/12/15
 */
public class SystemContextHolder {

    private static final ThreadLocal<Systems> LOCAL = new TransmittableThreadLocal<>();

    public static void set(Systems systems){
        LOCAL.set(systems);
    }

    public static Systems get() {
        return LOCAL.get();
    }


    public static void clear(){
        LOCAL.remove();
    }
}

```

# com.medusa.gruul.common.system.model.filter.AbstractServletFilter

**文件路径**: `model\filter\AbstractServletFilter.java`

## 代码文档
///
简单包装一下filter
ServletRequest 强转 HttpServletRequest
ServletResponse 强转 HttpServletResponse
@author 张治保
date 2021/12/3
 
///

///
子类实现
@param request request
@param response response
@param filterChain chain
@throws IOException IO流异常
@throws ServletException servlet异常
     
///


## 文件结构
```
项目根目录
└── model\filter
    └── AbstractServletFilter.java
```

## 完整代码
```java
package com.medusa.gruul.common.system.model.filter;

import org.springframework.web.filter.GenericFilterBean;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * 简单包装一下filter
 * ServletRequest 强转 HttpServletRequest
 * ServletResponse 强转 HttpServletResponse
 * @author 张治保
 * date 2021/12/3
 */
public abstract class AbstractServletFilter extends GenericFilterBean {
    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        HttpServletResponse response = (HttpServletResponse) servletResponse;
        this.doFilter(request, response, filterChain);
    }

    /**
     * 子类实现
     * @param request request
     * @param response response
     * @param filterChain chain
     * @throws IOException IO流异常
     * @throws ServletException servlet异常
     */
    public abstract void doFilter( HttpServletRequest request,HttpServletResponse response ,FilterChain filterChain) throws IOException, ServletException;
}

```

# com.medusa.gruul.common.system.model.filter.SystemFilter

**文件路径**: `model\filter\SystemFilter.java`

## 代码文档
///
当前app请求的版本号 与客户端ip

@author 张治保
date 2021/12/3
 
///


## 文件结构
```
项目根目录
└── model\filter
    └── SystemFilter.java
```

## 完整代码
```java
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

```

# com.medusa.gruul.common.system.model.remote.HeaderRender

**文件路径**: `model\remote\HeaderRender.java`

## 代码文档
///
@author 张治保
date 2022/4/19
 
///


## 文件结构
```
项目根目录
└── model\remote
    └── HeaderRender.java
```

## 完整代码
```java
package com.medusa.gruul.common.system.model.remote;

import cn.hutool.core.util.StrUtil;
import com.medusa.gruul.common.system.model.ISystem;
import com.medusa.gruul.common.system.model.constant.SystemHeaders;
import com.medusa.gruul.common.system.model.model.ClientType;
import com.medusa.gruul.common.system.model.model.Platform;
import com.medusa.gruul.common.system.model.model.Systems;
import io.vavr.control.Option;

import java.util.HashMap;
import java.util.Map;

/**
 * @author 张治保
 * date 2022/4/19
 */
public class HeaderRender {
	private HeaderRender() {
	}

	public static Map<String, String> requestHeaders() {
		Option<Systems> systemsOpt = ISystem.systemOpt();
		Map<String, String> headers = new HashMap<>(16);
		if (systemsOpt.isEmpty()) {
			return headers;
		}
		Systems systems = systemsOpt.get();
		/*
		 * ip地址
		 */
		String ip = systems.getIp();
		if (StrUtil.isNotBlank(ip)) {
			headers.put(SystemHeaders.IP, ip);
		}
		/*
		 * 设备id
		 */
		String deviceId = systems.getDeviceId();
		if (StrUtil.isNotBlank(deviceId)) {
			headers.put(SystemHeaders.DEVICE_ID, deviceId);
		}
		/*
		 * 客户端类型
		 */
		ClientType clientType = systems.getClientType();
		if (clientType != null) {
			headers.put(SystemHeaders.CLIENT_TYPE, clientType.name());
		}
		/*
		 * 客户端类型
		 */
		Platform platform = systems.getPlatform();
		if (platform != null) {
			headers.put(SystemHeaders.PLATFORM, platform.name());
		}
		/*
		 * api版本号
		 */
		String version = systems.getVersion();
		if (StrUtil.isNotBlank(version)) {
			headers.put(SystemHeaders.VERSION, version);
		}
		/*
		 * 服务商id
		 */
		Long platformId = systems.getPlatformId();
		if (platformId != null) {
			headers.put(SystemHeaders.PROVIDER_ID, String.valueOf(platformId));
		}
		/*
		 * 店铺id
		 */
		Long shopId = systems.getShopId();
		if (shopId != null) {
			headers.put(SystemHeaders.SHOP_ID, String.valueOf(shopId));
		}

		return headers;
	}
}

```

# com.medusa.gruul.common.system.model.remote.SystemDubboConsumerSpreadConfig

**文件路径**: `model\remote\SystemDubboConsumerSpreadConfig.java`

## 代码文档
///
dubbo rpc消费者端传递token信息

@author 张治保
date 2022/7/5
 
///


## 文件结构
```
项目根目录
└── model\remote
    └── SystemDubboConsumerSpreadConfig.java
```

## 完整代码
```java
package com.medusa.gruul.common.system.model.remote;

import cn.hutool.core.collection.CollUtil;
import org.apache.dubbo.common.constants.CommonConstants;
import org.apache.dubbo.common.extension.Activate;
import org.apache.dubbo.rpc.*;

import java.util.Map;


/**
 * dubbo rpc消费者端传递token信息
 *
 * @author 张治保
 * date 2022/7/5
 */
@Activate(group = CommonConstants.CONSUMER)
public class SystemDubboConsumerSpreadConfig implements Filter {

    @Override
    public Result invoke(Invoker<?> invoker, Invocation invocation) throws RpcException {
        Map<String, String> headers = HeaderRender.requestHeaders();
        RpcContextAttachment clientAttachment = RpcContext.getClientAttachment();
        if (CollUtil.isNotEmpty(headers)) {
            headers.forEach(clientAttachment::setAttachment);
        }
        return invoker.invoke(invocation);

    }
}

```

# com.medusa.gruul.common.system.model.remote.SystemDubboProviderSpreadConfig

**文件路径**: `model\remote\SystemDubboProviderSpreadConfig.java`

## 代码文档
///
dubbo rpc服务提供者 解析token

@author 张治保
date 2022/7/5
 
///


## 文件结构
```
项目根目录
└── model\remote
    └── SystemDubboProviderSpreadConfig.java
```

## 完整代码
```java
package com.medusa.gruul.common.system.model.remote;

import com.medusa.gruul.common.system.model.constant.SystemHeaders;
import com.medusa.gruul.common.system.model.filter.SystemFilter;
import io.vavr.control.Option;
import org.apache.dubbo.common.constants.CommonConstants;
import org.apache.dubbo.common.extension.Activate;
import org.apache.dubbo.rpc.*;

/**
 * dubbo rpc服务提供者 解析token
 *
 * @author 张治保
 * date 2022/7/5
 */
@Activate(group = CommonConstants.PROVIDER)
public class SystemDubboProviderSpreadConfig implements Filter {


    private String getAttachment(RpcContext rpcContext, String key) {
        return Option.of(rpcContext.getAttachment(key.toLowerCase()))
                .getOrElse(() -> rpcContext.getAttachment(key));
    }

    @Override
    public Result invoke(Invoker<?> invoker, Invocation invocation) throws RpcException {
        RpcContextAttachment serverAttachment = RpcContext.getServerAttachment();
        SystemFilter.setContextHolder(
                "[dubbo]>>>" + invocation.getTargetServiceUniqueName() + "#" + invocation.getMethodName(),
                this.getAttachment(serverAttachment, SystemHeaders.IP),
                this.getAttachment(serverAttachment, SystemHeaders.DEVICE_ID),
                this.getAttachment(serverAttachment, SystemHeaders.CLIENT_TYPE),
                this.getAttachment(serverAttachment, SystemHeaders.VERSION),
                this.getAttachment(serverAttachment, SystemHeaders.PROVIDER_ID),
                this.getAttachment(serverAttachment, SystemHeaders.SHOP_ID),
                this.getAttachment(serverAttachment, SystemHeaders.PLATFORM)

        );
        try {
            return invoker.invoke(invocation);
        } finally {
            SystemFilter.clearAll();
        }
    }
}

```

# com.medusa.gruul.common.system.model.remote.SystemFeignRequestInterceptor

**文件路径**: `model\remote\SystemFeignRequestInterceptor.java`

## 代码文档
///
@author 张治保
date 2022/2/17
 
///


## 文件结构
```
项目根目录
└── model\remote
    └── SystemFeignRequestInterceptor.java
```

## 完整代码
```java
package com.medusa.gruul.common.system.model.remote;

import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.context.annotation.Bean;

import java.util.Map;

/**
 * @author 张治保
 * date 2022/2/17
 */
@ConditionalOnClass(name = "feign.RequestInterceptor")
public class SystemFeignRequestInterceptor {

    @Bean("systemRequestInterceptor")
    public feign.RequestInterceptor requestInterceptor() {
        return template -> {
            Map<String, String> requestHeaders = HeaderRender.requestHeaders();
            requestHeaders.forEach(template::header);
        };
    }
}

```

# com.medusa.gruul.common.system.model.remote.SystemRestTemplateRequestInterceptor

**文件路径**: `model\remote\SystemRestTemplateRequestInterceptor.java`

## 代码文档
///
@author 张治保
date 2022/3/24
 
///


## 文件结构
```
项目根目录
└── model\remote
    └── SystemRestTemplateRequestInterceptor.java
```

## 完整代码
```java
package com.medusa.gruul.common.system.model.remote;

import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpHeaders;

import java.util.Map;

/**
 * @author 张治保
 * date 2022/3/24
 */
@ConditionalOnClass(name = "org.springframework.http.client.ClientHttpRequestInterceptor")
public class SystemRestTemplateRequestInterceptor {

    @Bean("systemClientHttpRequestInterceptor")
    public org.springframework.http.client.ClientHttpRequestInterceptor clientHttpRequestInterceptor() {
        return (request, body, execution) -> {

            HttpHeaders headers = request.getHeaders();
            Map<String, String> requestHeaders = HeaderRender.requestHeaders();
            requestHeaders.forEach(headers::add);
            return execution.execute(request, body);
        };
    }
}

```

# com.medusa.gruul.common.system.model.util.IpUtils

**文件路径**: `model\util\IpUtils.java`

## 代码文档
///
@author sombody
 
///

///
获取客户端IP

@param request 请求对象
@return IP地址
     
///

///
从多级反向代理中获得第一个非unknown IP地址

@param ip 获得的IP地址
@return 第一个非unknown IP地址
     
///

///
检测给定字符串是否为未知，多用于检测HTTP请求相关

@param checkString 被检测的字符串
@return 是否未知
     
///


## 文件结构
```
项目根目录
└── model\util
    └── IpUtils.java
```

## 完整代码
```java
package com.medusa.gruul.common.system.model.util;

import cn.hutool.core.text.StrPool;
import com.medusa.gruul.common.system.model.constant.SystemHeaders;
import org.springframework.util.StringUtils;

import jakarta.servlet.http.HttpServletRequest;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Optional;

/**
 * @author sombody
 */
public class IpUtils {

    private static final String LOCAL_HOST_IP_V4 = "127.0.0.1";
    private static final String LOCAL_HOST_IP_V6 = "0:0:0:0:0:0:0:1";
    private static final String UNKNOWN = "unknown";

    private static final String[] POSSIBLE_IP_HEADERS = {
        SystemHeaders.IP,
        "x-forwarded-for",
        "X-Forwarded-For",
        "Proxy-Client-IP",
        "WL-Proxy-Client-IP",
        "HTTP_CLIENT_IP",
        "HTTP_X_FORWARDED_FOR"
    };

    /**
     * 获取客户端IP
     *
     * @param request 请求对象
     * @return IP地址
     */
    public static String getIpAddr(HttpServletRequest request) {
        if (request == null) {
            return UNKNOWN;
        }
        String ip = null;
        for (String possibleIpHeader : POSSIBLE_IP_HEADERS) {
            ip = request.getHeader(possibleIpHeader);
            if (!isUnknown(ip)) {
                break;
            }
        }
        if (isUnknown(ip)) {
            ip = Optional.ofNullable(request.getRemoteAddr()).orElse("");
            if (LOCAL_HOST_IP_V4.equals(ip) || LOCAL_HOST_IP_V6.equals(ip)) {
                // 根据网卡取本机配置的IP
                try {
                    InetAddress inet = InetAddress.getLocalHost();
                    ip = inet.getHostAddress();
                } catch (UnknownHostException ignore) {
                }
            }
        }

        return LOCAL_HOST_IP_V6.equals(ip) ? LOCAL_HOST_IP_V4 : getMultistageReverseProxyIp(ip);
    }

    /**
     * 从多级反向代理中获得第一个非unknown IP地址
     *
     * @param ip 获得的IP地址
     * @return 第一个非unknown IP地址
     */
    public static String getMultistageReverseProxyIp(String ip) {
        // 多级反向代理检测
        if (ip != null && ip.indexOf(StrPool.COMMA) > 0) {
            final String[] ips = ip.trim().split(StrPool.COMMA);
            for (String subIp : ips) {
                if (!isUnknown(subIp)) {
                    ip = subIp;
                    break;
                }
            }
        }
        return ip;
    }

    /**
     * 检测给定字符串是否为未知，多用于检测HTTP请求相关
     *
     * @param checkString 被检测的字符串
     * @return 是否未知
     */
    public static boolean isUnknown(String checkString) {
        return !StringUtils.hasText(checkString) || UNKNOWN.equalsIgnoreCase(checkString);
    }
}
```

