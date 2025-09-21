## 权限与认证

### 自定义登录方式

- 继承`com.medusa.gruul.common.security.server.provider.IAuthenticationProvider`接口，实现
  `SecureUser<?> authenticate(T authentication)`方法。
    - 方法参数为`<T extends AuthenticationRequest>`类型，用于接收前端传递的参数（request body）。
    - 方法返回值为`SecureUser`，具体返回数据可查看`com.medusa.gruul.common.security.model.bean.SecureUser`。
    - 如果认证不通过，可以抛出`AuthenticationException`异常
- 使用 `@GrantType`注解标明自定义的登录方式的类型，建议类型具有语义化。 如`@GrantType("password")` 表示使用密码的方式登录。
    - 该类型必须是唯一的。
    - 前端通过parameter 方式的`grant_type`字段传递该类型。
- 使用 `@Component`注解将自定义的登录方式注册到Spring容器中。
  通过以上 3 个步骤，即可完成自定义的登录方式。
- 例子

```java
/**
 * 用户名密码方式登录
 */
@Component
@GrantType("password")
public class UsernamePasswordAuthenticationProvider implements IAuthenticationProvider<UsernamePasswordAuthenticationProvider.UsernamePassword> {

    @Override
    public SecureUser<?> authenticate(UsernamePassword authentication) throws AuthenticationException {
        //判断用户名密码是否正确
        if ("admin".equals(authentication.getUsername()) && "admin".equals(authentication.getPassword())) {
            return new SecureUser<>()
                    //登录的客户端类型
                    .setClientType(ClientType.PLATFORM_CONSOLE)
                    //登录所属店铺id
                    .setShopId(0L)
                    //登录用户id
                    .setId(1L)
                    //登录的用户昵称
                    .setNickname("张三")
                    //主角色 
                    .setRoles(Set.of(Roles.ADMIN))
                    //子角色
                    .setSubRoles(Set.of())
                    //所拥有的权限
                    .setPerms(Set.of());
        }
        // 认证失败
        throw SecurityException.of(SecureCodes.USERNAME_PASSWORD_INVALID);
    }

    @Getter
    @Setter
    @ToString
    public static class UsernamePassword extends AuthenticationRequest {
        /**
         * 用户名
         */
        @NotBlank
        private String username;

        /**
         * 密码
         */
        @NotBlank
        private String password;
    }
}
```

### 校验用户权限

- 接口方法上使用`@PreAuthorize` 注解前置校验用户权限。(也可在类上使用该注解，方法上的优先级高于类上的)

    - 该注解的值支持SpEL表达式。
    - 可以使用已自动注册到SpringIOC容器的名称为`S`类型`com.medusa.gruul.common.security.resource.helper.Security`
      工具进行权限校验 `Security.matcher()` 返回了一个
      `com.medusa.gruul.common.security.resource.extension.IUserRolePermMatcher`类型的实例
    - IUserRolePermMatcher 可以通过`[角色、子角色、权限]` 和 `[与、或、非]`进行交叉组合的方式校验用户权限。
    - 例子

```java
    // 用户有平台管理员角色 或 用户拥有平台自定义管理员角色且拥有order权限 才能继续访问
@PreAuthorize("""
                @S.matcher()
                .role(@S.PLATFORM_ADMIN)
                .or(@S.consumer().role(@S.PLATFORM_CUSTOM_ADMIN).perm('order'))
                .match()
        """)
```

```java
    //  用户拥有平台自定义管理员角色且拥有order权限 才能继续访问
@PreAuthorize("""
                @S.matcher()
                .role(@S.PLATFORM_CUSTOM_ADMIN)
                .perm('order')
                .match()
        """)
```

```java
// 用户拥有平台管理员角色 平台自定义管理员中的一种 或 用户拥有店铺管理员角色 店铺自定义管理员角色中的一种 才能继续访问
@PreAuthorize("""
                @S.matcher()
                .anyRole(@S.PLATFORM_ADMIN,@S.PLATFORM_CUSTOM_ADMIN)
                .or(@S.consumer().anyRole(@S.SHOP_ADMIN,@S.SHOP_CUSTOM_ADMIN))
                .match()
        """)
//当然和下面的写法是等价的 即拥有平台管理员角色、平台自定义管理员、店铺管理员角色、店铺自定义管理员角色中的一种 才能继续访问
@PreAuthorize("""
                @S.matcher()
               .anyRole(@S.PLATFORM_ADMIN,@S.PLATFORM_CUSTOM_ADMIN,@S.SHOP_ADMIN,@S.SHOP_CUSTOM_ADMIN)
               .match()
        """)
```

```java
// 这里演示"非"的用法,这种方式不常用，一般"与"、"或"即能满足需求
// 用户拥有平台自定义管理员角色 且 用户不拥有order权限 才能继续访问
@PreAuthorize("""
                @S.matcher()
                .role(@S.PLATFORM_CUSTOM_ADMIN)
                .neqPerm('order')
                .match()
        """)
```

- 不使用注解如何校验用户权限？
    - 可以使用工具类`com.medusa.gruul.common.security.resource.helper.ISecurity`进行权限校验
    - 使用 `ISecurity.matcher()` 触发权限校验，此方法返回了一个
      `com.medusa.gruul.common.security.resource.extension.IUserRolePermMatcher`类型的实例
    - 和注解上的@S用法一致
    - 例子

```java
// 用户拥有平台自定义管理员角色 且 用户拥有order权限
// 返回 boolean 用于判定用户是否拥有权限 
//@formatter:off
ISecurity.matcher()
        .role(Roles.SUPER_CUSTOM_ADMIN)
        .perm("order")
        .match()
//@formatter:on
```

### 常见问题？FAQ

- 我想跳过接口的权限校验怎么办？
    - 默认情况下不进行权限校验（允许匿名用户和所有的非匿名用户访问）只有当接口方法上或类上使用了`@PreAuthorize`
      注解时才会进行权限校验。
    - 如果类上使用了`@PreAuthorize` 注解，那么类中的所有方法都会进行权限校验。
    - 如果类上使用了`@PreAuthorize` 注解，该类里的某个接口方法不想进行权限校验怎么办？
        - 可以在该接口方法上使用`@PreAuthorize("permitAll()")` 注解来跳过权限校验。
        - `@PreAuthorize("permitAll()")`等价于`@PreAuthorize("true")`
- 我想在登录时额外返回其它数据怎么办？
    - 可以在登录方式中返回`SecureUser`时设置`open`字段。
    - open字段是一个`Map`类型，可以存放任意数据。
    - 比如往open里设置用户了id：userId = 1，将返回以下数据结构

```json

{
  "expireAt": "token过期时间",
  "expiresIn": 7200,
  // 额外返回的数据
  "open": {
    "userId": 1
  },
  "refreshToken": {
    "expireAt": "refresh_token过期时间",
    "expiresIn": 604800,
    "value": "refresh_token..."
  },
  "value": "token....."
}

```

- 我想修改token的密钥对，该怎么生成新的密钥对
    - 系统使用ES256算法签发token
    - 推荐使用 jwt工具 生成新的密钥对 `KeyPair esKeyPair = Jwts.SIG.ES256.keyPair().build()`
    - 具体用法参考 `com.medusa.gruul.common.security.server.test.GenerateKeysTest.jwtKey`
