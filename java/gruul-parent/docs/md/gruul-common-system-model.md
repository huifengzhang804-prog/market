# com.medusa.gruul.common.system.model.constant.SystemHeaders

**文件路径**: `model\constant\SystemHeaders.java`

## 代码文档
///
@author 张治保
date 2022/2/11
 
///

///
设备id
     
///

///
客户端IP地址
     
///

///
客户端类型
     
///

///
请求版本
     
///

///
租户ID请求头
     
///

///
店铺ID请求头
     
///

///
平台
     
///


## 文件结构
```
项目根目录
└── model\constant
    └── SystemHeaders.java
```

## 完整代码
```java
package com.medusa.gruul.common.system.model.constant;

/**
 * @author 张治保
 * date 2022/2/11
 */
public interface SystemHeaders {
    /**
     * 设备id
     */
    String DEVICE_ID = "Device-Id";
    /**
     * 客户端IP地址
     */
    String IP = "X-Real-IP";
    /**
     * 客户端类型
     */
    String CLIENT_TYPE = "Client-Type";

    /**
     * 请求版本
     */
    String VERSION = "Version";

    /**
     * 租户ID请求头
     */
    String PROVIDER_ID = "Provider-Id";

    /**
     * 店铺ID请求头
     */
    String SHOP_ID = "Shop-Id";

    /**
     * 平台
     */
    String PLATFORM = "Platform";


}

```

# com.medusa.gruul.common.system.model.model.ClientType

**文件路径**: `model\model\ClientType.java`

## 代码文档
///
客户端类型

@author 张治保
date 2022/4/12
 
///

///
开发者端
	 
///

///
平台端
	 
///

///
商家端
	 
///

///
消费端
	 
///

///
门店端
	 
///

///
供应商端
	 
///

///
是否在登录时校验店铺是否可用
	 
///

///
当前客户端是否可切换店铺登录
	 
///

///
商铺id校验 (headerShopId,userShopId)-> boolean
headerShopId 请求头的 shopId，
userShopId  用户所属的 shopId
return 是否校验通过
	 
///


## 文件结构
```
项目根目录
└── model\model
    └── ClientType.java
```

## 完整代码
```java
package com.medusa.gruul.common.system.model.model;

import com.baomidou.mybatisplus.annotation.EnumValue;
import com.medusa.gruul.global.model.constant.SecurityConst;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.function.BiFunction;

/**
 * 客户端类型
 *
 * @author 张治保
 * date 2022/4/12
 */
@Getter
@RequiredArgsConstructor
public enum ClientType {

	/**
	 * 开发者端
	 */
	DEVELOPER_CONSOLE(-1, false, false, (headerShopId, userShopId) -> headerShopId.equals(userShopId) && headerShopId.equals(SecurityConst.NO_SHOP_ID_CLIENT_DEFAULT_SHOP_ID)),

	/**
	 * 平台端
	 */
	PLATFORM_CONSOLE(0, false, false, DEVELOPER_CONSOLE.shopIdCheck),

	/**
	 * 商家端
	 */
	SHOP_CONSOLE(1, true, true, Long::equals),

	/**
	 * 消费端
	 */
	CONSUMER(2, false, false, (headerShopId, userShopId) -> Boolean.TRUE),

	/**
	 * 门店端
	 */
	STORE(3, true, false, CONSUMER.shopIdCheck),

	/**
	 * 供应商端
	 */
	SUPPLIER_CONSOLE(4, true, true, SHOP_CONSOLE.shopIdCheck),
	;

	@EnumValue
	private final Integer value;

	/**
	 * 是否在登录时校验店铺是否可用
	 */
	private final boolean checkShop;

	/**
	 * 当前客户端是否可切换店铺登录
	 */
	private final boolean switchShop;

	/**
	 * 商铺id校验 (headerShopId,userShopId)-> boolean
	 * headerShopId 请求头的 shopId，
	 * userShopId  用户所属的 shopId
	 * return 是否校验通过
	 */
	private final BiFunction<Long, Long, Boolean> shopIdCheck;
}

```

# com.medusa.gruul.common.system.model.model.Platform

**文件路径**: `model\model\Platform.java`

## 代码文档
///
APP平台类型
@author 张治保
date 2021/11/30
 
///

///
微信小程序
     
///

///
公众号
     
///

///
PC端
     
///

///
移动端端h5
     
///

///
IOS
     
///

///
ANDROID
     
///

///
HARMONY
     
///


## 文件结构
```
项目根目录
└── model\model
    └── Platform.java
```

## 完整代码
```java
package com.medusa.gruul.common.system.model.model;

import com.baomidou.mybatisplus.annotation.EnumValue;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * APP平台类型
 * @author 张治保
 * date 2021/11/30
 */
@Getter
@RequiredArgsConstructor
public enum Platform {
    /**
     * 微信小程序
     */
    WECHAT_MINI_APP(0),
    /**
     * 公众号
     */
    WECHAT_MP(1),
    /**
     * PC端
     */
    PC(2),
    /**
     * 移动端端h5
     */
    H5(3),
    /**
     * IOS
     */
    IOS(4),
    /**
     * ANDROID
     */
    ANDROID(5),
    /**
     * HARMONY
     */
    HARMONY(6);


    @EnumValue
    private final Integer value;
}

```

# com.medusa.gruul.common.system.model.model.Systems

**文件路径**: `model\model\Systems.java`

## 代码文档
///
@author 张治保
date 2022/2/16
 
///

///
客户端ip地址
     
///

///
客户端设备id
     
///

///
客户端类型
     
///

///
客户端运行平台
     
///

///
请求版本号
     
///

///
平台服务商id
     
///

///
店铺id
     
///


## 文件结构
```
项目根目录
└── model\model
    └── Systems.java
```

## 完整代码
```java
package com.medusa.gruul.common.system.model.model;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

/**
 * @author 张治保
 * date 2022/2/16
 */
@Getter
@Setter
@Accessors(chain = true)
public class Systems {

    /**
     * 客户端ip地址
     */
    private String ip;
    /**
     * 客户端设备id
     */
    private String deviceId;
    /**
     * 客户端类型
     */
    private ClientType clientType;
    /**
     * 客户端运行平台
     */
    private Platform platform;
    /**
     * 请求版本号
     */
    private String version;
    /**
     * 平台服务商id
     */
    private Long platformId;
    /**
     * 店铺id
     */
    private Long shopId;


}

```

