# com.medusa.gruul.common.security.model.SecurityModelI18NLoader

**文件路径**: `security\model\SecurityModelI18NLoader.java`

## 代码文档
///
@author 张治保
date 2023/6/16
 
///


## 文件结构
```
项目根目录
└── security\model
    └── SecurityModelI18NLoader.java
```

## 完整代码
```java
package com.medusa.gruul.common.security.model;

import com.medusa.gruul.global.i18n.I18NPropertiesLoader;
import org.springframework.lang.NonNull;

import java.util.Set;

/**
 * @author 张治保
 * date 2023/6/16
 */
public class SecurityModelI18NLoader implements I18NPropertiesLoader {

	@Override
	@NonNull
	public Set<String> paths() {
		return Set.of("i18n/security");
	}
}

```

# com.medusa.gruul.common.security.model.bean.CacheToken

**文件路径**: `model\bean\CacheToken.java`

## 代码文档
///
@author 张治保
@since 2023/11/23
 
///

///
token
     
///

///
异常状态提示消息
     
///


## 文件结构
```
项目根目录
└── model\bean
    └── CacheToken.java
```

## 完整代码
```java
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

```

# com.medusa.gruul.common.security.model.bean.DecodeToken

**文件路径**: `model\bean\DecodeToken.java`

## 代码文档
///
@author 张治保
@since 2023/11/3
 
///

///
jti
     
///

///
token 类型
     
///

///
是否过期
     
///

///
用户信息
     
///

///
签名时间
     
///

///
过期时间
     
///


## 文件结构
```
项目根目录
└── model\bean
    └── DecodeToken.java
```

## 完整代码
```java
package com.medusa.gruul.common.security.model.bean;

import com.medusa.gruul.common.security.model.enums.TokenType;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * @author 张治保
 * @since 2023/11/3
 */
@Getter
@Setter
@ToString
@Accessors(chain = true)
public class DecodeToken implements Serializable {

    /**
     * jti
     */
    private String tokenId;

    /**
     * token 类型
     */
    private TokenType tokenType;

    /**
     * 是否过期
     */
    private boolean expired;

    /**
     * 用户信息
     */
    private SecureUser<?> secureUser;

    /**
     * 签名时间
     */
    private LocalDateTime issuedAt;

    /**
     * 过期时间
     */
    private LocalDateTime expireAt;
}

```

# com.medusa.gruul.common.security.model.bean.EncodeToken

**文件路径**: `model\bean\EncodeToken.java`

## 代码文档
///
@author 张治保
@since 2023/11/2
 
///

///
token
     
///

///
过期时间
     
///

///
过期时长 单位：秒
     
///


## 文件结构
```
项目根目录
└── model\bean
    └── EncodeToken.java
```

## 完整代码
```java
package com.medusa.gruul.common.security.model.bean;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * @author 张治保
 * @since 2023/11/2
 */
@Getter
@Setter
@Accessors(chain = true)
@ToString
public class EncodeToken implements Serializable {

    /**
     * token
     */
    private String value;

    /**
     * 过期时间
     */
    private LocalDateTime expireAt;

    /**
     * 过期时长 单位：秒
     */
    private Long expiresIn;

}

```

# com.medusa.gruul.common.security.model.bean.SecureUser

**文件路径**: `model\bean\SecureUser.java`

## 代码文档
///
安全用户 匿名用户 id为空

@author 张治保
date 2022/2/26
 
///

///
客户端类型
     
///

///
shopId
     
///

///
用户id 匿名用户id为空
     
///

///
昵称
     
///

///
用户名
     
///

///
密码
     
///

///
邮件
     
///

///
微信 open id
     
///

///
用户角色集合
     
///

///
副角色
     
///

///
用户权限集合
     
///

///
额外信息
     
///

///
开放的信息 可用于展示给前端
     
///

///
是否是匿名用户

@return true 是 false 不是
     
///

///
是否是黑名单用户
     
///

///
获取开放数据 并清空

@return 开放数据
     
///


## 文件结构
```
项目根目录
└── model\bean
    └── SecureUser.java
```

## 完整代码
```java
package com.medusa.gruul.common.security.model.bean;

import com.medusa.gruul.common.security.model.enums.Roles;
import com.medusa.gruul.common.system.model.model.ClientType;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.Map;
import java.util.Set;

/**
 * 安全用户 匿名用户 id为空
 *
 * @author 张治保
 * date 2022/2/26
 */
@Getter
@Setter
@Accessors(chain = true)
public final class SecureUser<T> implements Serializable {

    /**
     * 客户端类型
     */
    private ClientType clientType;

    /**
     * shopId
     */
    private Long shopId;

    /**
     * 用户id 匿名用户id为空
     */
    private Long id;

    /**
     * 昵称
     */
    private String nickname;

    /**
     * 用户名
     */
    private String username;

    /**
     * 密码
     */
    private String mobile;

    /**
     * 邮件
     */
    private String email;

    /**
     * 微信 open id
     */
    private String openid;

    /**
     * 用户角色集合
     */
    private Set<Roles> roles = Set.of();

    /**
     * 副角色
     */
    private Set<Roles> subRoles = Set.of();

    /**
     * 用户权限集合
     */
    private Set<String> perms = Set.of();

    /**
     * 额外信息
     */
    private T extra;

    /**
     * 开放的信息 可用于展示给前端
     */
    private Map<String, Object> open = Map.of();

    /**
     * 是否是匿名用户
     *
     * @return true 是 false 不是
     */
    public boolean isAnonymous() {
        return getId() == null;
    }

    /**
     * 是否是黑名单用户
     */
    public boolean isBlack() {
        return getSubRoles().contains(Roles.BLACK_LIST);
    }

    /**
     * 获取开放数据 并清空
     *
     * @return 开放数据
     */
    public Map<String, Object> open() {
        try {
            return open;
        } finally {
            this.setOpen(null);
        }
    }
}

```

# com.medusa.gruul.common.security.model.bean.TokenKey

**文件路径**: `model\bean\TokenKey.java`

## 代码文档
///
@author 张治保
@since 2024/5/22
 
///

///
客户端类型
     
///

///
店铺id
     
///

///
用户id
     
///

///
token id
     
///


## 文件结构
```
项目根目录
└── model\bean
    └── TokenKey.java
```

## 完整代码
```java
package com.medusa.gruul.common.security.model.bean;

import com.medusa.gruul.common.system.model.model.ClientType;
import lombok.*;

/**
 * @author 张治保
 * @since 2024/5/22
 */
@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
public class TokenKey implements java.io.Serializable {
    /**
     * 客户端类型
     */
    private ClientType clientType;

    /**
     * 店铺id
     */
    private Long shopId;

    /**
     * 用户id
     */
    private Long userId;

    /**
     * token id
     */
    private String tokenId;

}

```

# com.medusa.gruul.common.security.model.bean.UserMatch

**文件路径**: `model\bean\UserMatch.java`

## 代码文档
///
user match

@author 张治保
date 2023/4/11
 
///

///
是否权限匹配成功
	 
///

///
当前用户信息
	 
///


## 文件结构
```
项目根目录
└── model\bean
    └── UserMatch.java
```

## 完整代码
```java
package com.medusa.gruul.common.security.model.bean;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

/**
 * user match
 *
 * @author 张治保
 * date 2023/4/11
 */
@Getter
@Setter
@Builder
public class UserMatch implements java.io.Serializable {

	/**
	 * 是否权限匹配成功
	 */
	private boolean success;

	/**
	 * 当前用户信息
	 */
	private SecureUser secureUser;
}

```

# com.medusa.gruul.common.security.model.constant.SecureCode

**文件路径**: `model\constant\SecureCode.java`

## 代码文档
///
异常代码

@author 张治保
date 2022/2/28
 
///

///
需要登录
     
///

///
token不可用
     
///

///
token已过期
     
///

///
refresh token不可用
     
///

///
refresh token 已过期
     
///

///
权限不足
     
///

///
账号已过期
     
///

///
密码有误
     
///

///
clientId invalid;
     
///

///
scope invalid
     
///

///
REQUEST_INVALID
     
///

///
UNSUPPORTED_RESPONSE_TYPE
     
///

///
拒绝访问
     
///

///
重定向地址不可用
     
///

///
授权不可用
     
///

///
账号不可用
     
///

///
服务不可用
     
///

///
票据已过期
     
///

///
无效的验证码
     
///

///
用户拒绝授权
     
///

///
登录数据校验失败
     
///

///
未分配权限
     
///


## 文件结构
```
项目根目录
└── model\constant
    └── SecureCode.java
```

## 完整代码
```java
package com.medusa.gruul.common.security.model.constant;

import com.medusa.gruul.global.model.constant.GlobalCode;

/**
 * 异常代码
 *
 * @author 张治保
 * date 2022/2/28
 */
public interface SecureCode {

    /**
     * 需要登录
     */
    int NEED_LOGIN = 2;
    /**
     * token不可用
     */
    int TOKEN_INVALID = 3;
    /**
     * token已过期
     */
    int TOKEN_EXPIRED = 4;
    /**
     * refresh token不可用
     */
    int REFRESH_TOKEN_INVALID = 5;
    /**
     * refresh token 已过期
     */
    int REFRESH_TOKEN_EXPIRED = 6;
    /**
     * 权限不足
     */
    int PERMISSION_DENIED = 7;

    /**
     * 账号已过期
     */
    int ACCOUNT_EXPIRED = 8;
    /**
     * 密码有误
     */
    int USERNAME_PASSWORD_INVALID = 9;

    /**
     * clientId invalid;
     */
    int TOKEN_CHANGED = 10;
    /**
     * scope invalid
     */
    int SCOPE_INVALID = 11;
    /**
     * REQUEST_INVALID
     */
    int REQUEST_INVALID = GlobalCode.REQUEST_INVALID;
    /**
     * UNSUPPORTED_RESPONSE_TYPE
     */
    int RESPONSE_TYPE_INVALID = 13;
    /**
     * 拒绝访问
     */
    int ACCESS_DENIED = 14;
    /**
     * 重定向地址不可用
     */
    int REDIRECT_URI_INVALID = 15;
    /**
     * 授权不可用
     */
    int GRANT_INVALID = 16;
    /**
     * 账号不可用
     */
    int ACCOUNT_INVALID = 17;
    /**
     * 服务不可用
     */
    int AUTH_SERVER_ERROR = 18;
    /**
     * 票据已过期
     */
    int CREDENTIALS_EXPIRED = 19;

    /**
     * 无效的验证码
     */
    int SMS_CAPTCHA_INVALID = 20;

    /**
     * 用户拒绝授权
     */
    int USER_DENIED_AUTHORIZATION = 21;

    /**
     * 登录数据校验失败
     */
    int DATA_VALID_ERROR = 22;

    /**
     * 未分配权限
     */
    int UNALLOCATED_PERMISSION = 23;


}

```

# com.medusa.gruul.common.security.model.constant.SecureConst

**文件路径**: `model\constant\SecureConst.java`

## 代码文档
///
常量

@author 张治保
date 2022/4/19
 
///

///
默认的用具昵称前缀
     
///

///
用户登录检查 最后一个参数为shopId 店铺id
     
///

///
用户消息模板 最后一个参数为客户端占位符
     
///

///
指定店铺的用户的消息模板
第一个参数为客户端占位符 第二个参数为店铺id占位符
     
///

///
指定用户的消息模板
第一个参数为客户端占位符 第二个参数为店铺id占位符 第三个参数为用户id占位符
     
///

///
所有的消息模板
     
///

///
消息 destination 渲染

@param template   destination模板
@param clientType 客户端类型
@param shopId     店铺id
@param userId     用户id
@return destination
     
///


## 文件结构
```
项目根目录
└── model\constant
    └── SecureConst.java
```

## 完整代码
```java
package com.medusa.gruul.common.security.model.constant;

import cn.hutool.core.util.StrUtil;
import com.medusa.gruul.common.system.model.model.ClientType;

import java.util.Set;

/**
 * 常量
 *
 * @author 张治保
 * date 2022/4/19
 */
public interface SecureConst {

    String TOKEN_CACHE_KEY = "gruul:mall:uaa:token:";

    /**
     * 默认的用具昵称前缀
     */
    String DEFAULT_USER_NICKNAME_PREFIX = "U";

    /**
     * 用户登录检查 最后一个参数为shopId 店铺id
     */
    String USER_LOGIN_STATE_CHECK = "gruul:mall:uaa:login:user";

    /**
     * 用户消息模板 最后一个参数为客户端占位符
     */
    String DESTINATION_ALL_USER = "/topic/pigeon-to-{}";

    /**
     * 指定店铺的用户的消息模板
     * 第一个参数为客户端占位符 第二个参数为店铺id占位符
     */
    String DESTINATION_SHOP_USER = DESTINATION_ALL_USER + "-{}";

    /**
     * 指定用户的消息模板
     * 第一个参数为客户端占位符 第二个参数为店铺id占位符 第三个参数为用户id占位符
     */
    String DESTINATION_SHOP_MARKED_USER = DESTINATION_SHOP_USER + "-{}";


    /**
     * 所有的消息模板
     */
    Set<String> ALL_DESTINATION_TEMPLATE = Set.of(
            SecureConst.DESTINATION_ALL_USER,
            SecureConst.DESTINATION_SHOP_USER,
            SecureConst.DESTINATION_SHOP_MARKED_USER
    );

    /**
     * 消息 destination 渲染
     *
     * @param template   destination模板
     * @param clientType 客户端类型
     * @param shopId     店铺id
     * @param userId     用户id
     * @return destination
     */
    static String destination(String template, ClientType clientType, Long shopId, Long userId) {
        return StrUtil.format(template, clientType.name(), shopId, userId);
    }

}

```

# com.medusa.gruul.common.security.model.enums.MenuType

**文件路径**: `model\enums\MenuType.java`

## 代码文档
///
@author 张治保
date 2022/2/23
 
///

///
目录
     
///

///
菜单
     
///

///
接口
     
///

///
按钮
     
///

///
mysql 值映射
     
///

///
是否有子节点
     
///


## 文件结构
```
项目根目录
└── model\enums
    └── MenuType.java
```

## 完整代码
```java
package com.medusa.gruul.common.security.model.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * @author 张治保
 * date 2022/2/23
 */
@Getter
@RequiredArgsConstructor
public enum MenuType {
    /**
     * 目录
     */
    CATALOG(0, true),

    /**
     * 菜单
     */
    MENU(1, true),

    /**
     * 接口
     */
    API(2, false),

    /**
     * 按钮
     */
    BUTTON(3, false);

    /**
     * mysql 值映射
     */
    @EnumValue
    private final Integer value;

    /**
     * 是否有子节点
     */
    private final boolean hasChildren;
}

```

# com.medusa.gruul.common.security.model.enums.Roles

**文件路径**: `model\enums\Roles.java`

## 代码文档
///
@author 张治保
date 2022/2/23
 
///

///
开发者
     
///

///
超级管理员 最大权限
     
///

///
普通用户 （消费者）
     
///

///
禁止评论 用户
     
///

///
禁止下单用户
     
///

///
黑名单
     
///

///
自定义超级管理员
     
///

///
管理员 当前商铺的最大权限
     
///

///
自定义管理员 管理员自定义权限管理员
     
///

///
店铺门店
     
///

///
主播
     
///

///
供应商管理员
     
///

///
供应商子管理员
     
///

///
数据库 值
     
///

///
对应的角色id null为动态角色
     
///

///
名称描述
     
///

///
是否需要权限
     
///

///
客户端类型
     
///

///
用于判断是否是主要角色 值最小的是主要角色
当一个用户拥有多个角色时 以主要角色为准 其他角色为子角色
     
///

///
websocket destination
     
///

///
主要角色排序比较器 用于判断是否是主要角色 值最小的是主要角色

@param clientType 客户端类型
@return 比较器
     
///

///
获取角色对应的websocket destination

@param shopId 店铺id
@param userId 用户id
@return destinations 集合
     
///


## 文件结构
```
项目根目录
└── model\enums
    └── Roles.java
```

## 完整代码
```java
package com.medusa.gruul.common.security.model.enums;

import cn.hutool.core.collection.CollUtil;
import com.baomidou.mybatisplus.annotation.EnumValue;
import com.medusa.gruul.common.security.model.constant.SecureConst;
import com.medusa.gruul.common.system.model.model.ClientType;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Comparator;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @author 张治保
 * date 2022/2/23
 */
@Getter
@RequiredArgsConstructor
public enum Roles {


    /**
     * 开发者
     */
    DEVELOPER(1, 1L, "开发者", false, ClientType.DEVELOPER_CONSOLE, 0, Set.of()),

    /**
     * 超级管理员 最大权限
     */
    SUPER_ADMIN(2, 2L, "超级管理员", false, ClientType.PLATFORM_CONSOLE, 0, SecureConst.ALL_DESTINATION_TEMPLATE),
    /**
     * 普通用户 （消费者）
     */
    USER(3, 3L, "用户", false, ClientType.CONSUMER, 0, SecureConst.ALL_DESTINATION_TEMPLATE),
    /**
     * 禁止评论 用户
     */
    FORBIDDEN_COMMENT(4, 4L, "禁止评论", false, ClientType.CONSUMER, 1, Set.of()),
    /**
     * 禁止下单用户
     */
    FORBIDDEN_ORDER(5, 5L, "禁止下单", false, ClientType.CONSUMER, 1, Set.of()),
    /**
     * 黑名单
     */
    BLACK_LIST(6, 6L, "黑名单", false, ClientType.CONSUMER, 1, Set.of()),

    /**
     * 自定义超级管理员
     */
    SUPER_CUSTOM_ADMIN(7, null, "子超级管理员", true, ClientType.PLATFORM_CONSOLE, 1, SecureConst.ALL_DESTINATION_TEMPLATE),

    /**
     * 管理员 当前商铺的最大权限
     */
    ADMIN(8, null, "管理员", false, ClientType.SHOP_CONSOLE, 0, SecureConst.ALL_DESTINATION_TEMPLATE),

    /**
     * 自定义管理员 管理员自定义权限管理员
     */
    CUSTOM_ADMIN(9, null, "子管理员", true, ClientType.SHOP_CONSOLE, 1, SecureConst.ALL_DESTINATION_TEMPLATE),

    /**
     * 店铺门店
     */
    SHOP_STORE(10, null, "店铺门店", false, ClientType.STORE, 0, SecureConst.ALL_DESTINATION_TEMPLATE),

    /**
     * 主播
     */
    ANCHOR(11, null, "主播", false, ClientType.CONSUMER, 1, Set.of()),

    /**
     * 供应商管理员
     */
    SUPPLIER_ADMIN(12, null, "供应商管理员", false, ClientType.SUPPLIER_CONSOLE, 0, SecureConst.ALL_DESTINATION_TEMPLATE),

    /**
     * 供应商子管理员
     */
    SUPPLIER_CUSTOM_ADMIN(13, null, "供应商子管理员", false, ClientType.SUPPLIER_CONSOLE, 1, SecureConst.ALL_DESTINATION_TEMPLATE),

    ;

    /**
     * 数据库 值
     */
    @EnumValue
    private final Integer value;

    /**
     * 对应的角色id null为动态角色
     */
    private final Long roleId;

    /**
     * 名称描述
     */
    private final String desc;

    /**
     * 是否需要权限
     */
    private final boolean permNeed;

    /**
     * 客户端类型
     */
    private final ClientType clientType;

    /**
     * 用于判断是否是主要角色 值最小的是主要角色
     * 当一个用户拥有多个角色时 以主要角色为准 其他角色为子角色
     */
    private final int mainSort;

    /**
     * websocket destination
     */
    private final Set<String> destination;

    /**
     * 主要角色排序比较器 用于判断是否是主要角色 值最小的是主要角色
     *
     * @param clientType 客户端类型
     * @return 比较器
     */
    public static Comparator<Roles> mainSortComparator(ClientType clientType) {
        return Comparator.comparing((Roles roles) -> roles.getClientType() != clientType)
                .thenComparing(Roles::getMainSort);
    }

    /**
     * 获取角色对应的websocket destination
     *
     * @param shopId 店铺id
     * @param userId 用户id
     * @return destinations 集合
     */
    public Set<String> destinations(Long shopId, Long userId) {
        Set<String> destinations = getDestination();
        if (CollUtil.isEmpty(destinations)) {
            return Set.of();
        }
        return destinations.stream()
                .map(destination -> SecureConst.destination(destination, this.getClientType(), shopId, userId))
                .collect(Collectors.toSet());
    }

}

```

# com.medusa.gruul.common.security.model.enums.SecureCodes

**文件路径**: `model\enums\SecureCodes.java`

## 代码文档
///
@author 张治保
date 2022/4/20
 
///

///
需要登录
     
///

///
token不可用
     
///

///
token已过期
     
///

///
refresh token不可用
     
///

///
refresh token 已过期
     
///

///
权限不足
     
///

///
账号已过期（账号数据变更） 尝试重新登录
     
///

///
密码有误
     
///

///
TOKEN 发生变化 已在其它地方登录
     
///

///
scope invalid
     
///

///
REQUEST_INVALID
     
///

///
UNSUPPORTED_RESPONSE_TYPE
     
///

///
无访问权限
     
///

///
重定向地址不可用
     
///

///
授权不可用
     
///

///
账号不可用
     
///

///
服务不可用
     
///

///
票据已过期
     
///

///
数据校验失败
     
///

///
未分配权限
     
///


## 文件结构
```
项目根目录
└── model\enums
    └── SecureCodes.java
```

## 完整代码
```java
package com.medusa.gruul.common.security.model.enums;

import com.medusa.gruul.common.security.model.constant.SecureCode;
import com.medusa.gruul.global.i18n.I18N;
import com.medusa.gruul.global.model.exception.Error;
import lombok.RequiredArgsConstructor;

/**
 * @author 张治保
 * date 2022/4/20
 */
@RequiredArgsConstructor
public enum SecureCodes implements Error {
    /**
     * 需要登录
     */
    NEED_LOGIN(SecureCode.NEED_LOGIN, "auth.need.login"),
    /**
     * token不可用
     */
    TOKEN_INVALID(SecureCode.TOKEN_INVALID, "auth.token.invalid"),
    /**
     * token已过期
     */
    TOKEN_EXPIRED(SecureCode.TOKEN_EXPIRED, "auth.token.expired"),
    /**
     * refresh token不可用
     */
    REFRESH_TOKEN_INVALID(SecureCode.REFRESH_TOKEN_INVALID, "auth.refresh.token.invalid"),
    /**
     * refresh token 已过期
     */
    REFRESH_TOKEN_EXPIRED(SecureCode.REFRESH_TOKEN_EXPIRED, "auth.refresh.token.expired"),
    /**
     * 权限不足
     */
    PERMISSION_DENIED(SecureCode.PERMISSION_DENIED, "auth.access.denied"),

    /**
     * 账号已过期（账号数据变更） 尝试重新登录
     */
    ACCOUNT_EXPIRED(SecureCode.ACCOUNT_EXPIRED, "auth.account.expired"),
    /**
     * 密码有误
     */
    USERNAME_PASSWORD_INVALID(SecureCode.USERNAME_PASSWORD_INVALID, "auth.username.password.invalid"),
    /**
     * TOKEN 发生变化 已在其它地方登录
     */
    TOKEN_CHANGED(SecureCode.TOKEN_CHANGED, "auth.token.changed"),
    /**
     * scope invalid
     */
    SCOPE_INVALID(SecureCode.SCOPE_INVALID, "auth.scope.invalid"),
    /**
     * REQUEST_INVALID
     */
    REQUEST_INVALID(SecureCode.REQUEST_INVALID, "auth.request.invalid"),
    /**
     * UNSUPPORTED_RESPONSE_TYPE
     */
    RESPONSE_TYPE_INVALID(SecureCode.RESPONSE_TYPE_INVALID, "auth.response.type.invalid"),
    /**
     * 无访问权限
     */
    ACCESS_DENIED(SecureCode.ACCESS_DENIED, "auth.access.denied"),
    /**
     * 重定向地址不可用
     */
    REDIRECT_URI_INVALID(SecureCode.REDIRECT_URI_INVALID, "auth.redirect.uri.invalid"),
    /**
     * 授权不可用
     */
    GRANT_INVALID(SecureCode.GRANT_INVALID, "auth.grant.invalid"),
    /**
     * 账号不可用
     */
    ACCOUNT_INVALID(SecureCode.ACCOUNT_INVALID, "auth.account.invalid"),
    /**
     * 服务不可用
     */
    AUTH_SERVER_ERROR(SecureCode.AUTH_SERVER_ERROR, "auth.server.error"),
    /**
     * 票据已过期
     */
    CREDENTIALS_EXPIRED(SecureCode.CREDENTIALS_EXPIRED, "auth.credentials.expired"),
    /**
     * 数据校验失败
     */
    DATA_VALID_ERROR(SecureCode.DATA_VALID_ERROR, "auth.data.valid.error"),
    /**
     * 未分配权限
     */
    UNALLOCATED_PERMISSION(SecureCode.UNALLOCATED_PERMISSION, "auth.unallocated.permission"),
    ;

    private final int code;
    private final String msg;

    @Override
    public int code() {
        return code;
    }

    @Override
    public String msg() {
        return I18N.msg(msg);
    }
}

```

# com.medusa.gruul.common.security.model.enums.TokenState

**文件路径**: `model\enums\TokenState.java`

## 代码文档
///
@author 张治保
@since 2023/11/23
 
///

///
有效
     
///

///
被主动下线
     
///

///
重置用户资料
     
///


## 文件结构
```
项目根目录
└── model\enums
    └── TokenState.java
```

## 完整代码
```java
package com.medusa.gruul.common.security.model.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * @author 张治保
 * @since 2023/11/23
 */
@RequiredArgsConstructor
@Getter
public enum TokenState {

    /**
     * 有效
     */
    ONLINE,

    /**
     * 被主动下线
     */
    KICK,

    /**
     * 重置用户资料
     */
    REFRESH

}

```

# com.medusa.gruul.common.security.model.enums.TokenType

**文件路径**: `model\enums\TokenType.java`

## 代码文档
///
@author 张治保
@since 2023/11/3
 
///

///
token
     
///

///
刷新token
     
///

///
claim type key
     
///

///
claim payload key
     
///

///
过期code异常
     
///

///
token 失效 code 异常
     
///


## 文件结构
```
项目根目录
└── model\enums
    └── TokenType.java
```

## 完整代码
```java
package com.medusa.gruul.common.security.model.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * @author 张治保
 * @since 2023/11/3
 */

@Getter
@RequiredArgsConstructor
public enum TokenType {
    /**
     * token
     */
    T(SecureCodes.TOKEN_EXPIRED, SecureCodes.TOKEN_INVALID),

    /**
     * 刷新token
     */
    RT(SecureCodes.REFRESH_TOKEN_EXPIRED, SecureCodes.REFRESH_TOKEN_INVALID);
    /**
     * claim type key
     */
    public static final String TYPE = "T";
    /**
     * claim payload key
     */
    public static final String KEY = "K";

    /**
     * 过期code异常
     */
    private final SecureCodes expired;

    /**
     * token 失效 code 异常
     */
    private final SecureCodes invalid;


}

```

# com.medusa.gruul.common.security.model.enums.UserStatus

**文件路径**: `model\enums\UserStatus.java`

## 代码文档
///
@author 张治保
date 2022/2/24
 
///

///
正常
     
///

///
已过期
     
///

///
已锁定
     
///

///
凭证已过期
     
///

///
不可用
     
///


## 文件结构
```
项目根目录
└── model\enums
    └── UserStatus.java
```

## 完整代码
```java
package com.medusa.gruul.common.security.model.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * @author 张治保
 * date 2022/2/24
 */
@Getter
@RequiredArgsConstructor
public enum  UserStatus {
    /**
     * 正常
     */
    NORMAL(0),
    /**
     * 已过期
     */
    EXPIRED(1),
    /**
     * 已锁定
     */
    LOCKED(2),
    /**
     * 凭证已过期
     */
    CREDENTIALS_EXPIRED(3),
    /**
     * 不可用
     */
    UNAVAILABLE(4);

    @EnumValue
    private final Integer value;
}

```

