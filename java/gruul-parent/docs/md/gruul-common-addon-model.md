# com.medusa.gruul.common.addon.model.bean.AddonDefinition

**文件路径**: `model\bean\AddonDefinition.java`

## 代码文档
///
@author 张治保
date 2022/9/14
 
///

///
是否是异步

///

///
通讯类型
	 
///

///
bean 名称
	 
///

///
dubbo接口全类名
	 
///

///
方法名
	 
///

///
参数类型
	 
///

///
条件满足时才会调用插件
	 
///

///
执行顺序
	 
///


## 文件结构
```
项目根目录
└── model\bean
    └── AddonDefinition.java
```

## 完整代码
```java
package com.medusa.gruul.common.addon.model.bean;

import com.medusa.gruul.common.addon.model.enums.AddonFuncType;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * @author 张治保
 * date 2022/9/14
 */
@Getter
@Setter
@ToString
@Accessors(chain = true)
public class AddonDefinition implements Serializable {


	/**
	 * 是否是异步
	 **/
	private boolean async;

	/**
	 * 通讯类型
	 */
	private AddonFuncType funcType;

	/**
	 * bean 名称
	 */
	private String beanName;

	/**
	 * dubbo接口全类名
	 */
	private String interfaceName;

	/**
	 * 方法名
	 */
	private String methodName;

	/**
	 * 参数类型
	 */
	private String[] parameterTypes;

	/**
	 * 条件满足时才会调用插件
	 */
	private String filter;

	/**
	 * 执行顺序
	 */
	private int order;
}

```

# com.medusa.gruul.common.addon.model.constant.AddonConst

**文件路径**: `model\constant\AddonConst.java`

## 代码文档
///
@author 张治保
date 2022/2/21
 
///

///
插件日志模板
     
///

///
插件provider 注册次数前缀 后面会拼上服务名
     
///

///
插件支持器前缀
     
///

///
插件 提供器前缀
     
///

///
插件 试图前缀
     
///


## 文件结构
```
项目根目录
└── model\constant
    └── AddonConst.java
```

## 完整代码
```java
package com.medusa.gruul.common.addon.model.constant;

/**
 * @author 张治保
 * date 2022/2/21
 */
public interface AddonConst {

    /**
     * 插件日志模板
     */
    String ADDON_LOG_TEMPLATE = "<<{}>>> ：「「{}」」";

    /**
     * 插件provider 注册次数前缀 后面会拼上服务名
     */
    String ADDON_PROVIDER_REGISTER = "addon:register:provider";

    /**
     * 插件支持器前缀
     */
    String REDIS_ADDON_SUPPORTER = "addon:supporter";

    /**
     * 插件 提供器前缀
     */
    String REDIS_ADDON_PROVIDER = "addon:provider";

    /**
     * 插件 试图前缀
     */
    String REDIS_ADDON_VIEW = "addon:view";
}

```

# com.medusa.gruul.common.addon.model.enums.AddonFuncType

**文件路径**: `model\enums\AddonFuncType.java`

## 代码文档
///
插件通讯类型

@author 张治保
date 2022/9/14
 
///

///
dubbo
     
///

///
http
     
///


## 文件结构
```
项目根目录
└── model\enums
    └── AddonFuncType.java
```

## 完整代码
```java
package com.medusa.gruul.common.addon.model.enums;

/**
 * 插件通讯类型
 *
 * @author 张治保
 * date 2022/9/14
 */
public enum AddonFuncType {
    /**
     * dubbo
     */
    DUBBO,
    /**
     * http
     */
    HTTP
}

```

# com.medusa.gruul.common.addon.model.handler.AddonHook

**文件路径**: `model\handler\AddonHook.java`

## 代码文档
///
视图处理器抽象
插件声明周期

@author 张治保
date 2022/3/2
 
///

///
插件安装
     
///

///
插件卸载
     
///

///
初始化
     
///

///
容器销毁
     
///


## 文件结构
```
项目根目录
└── model\handler
    └── AddonHook.java
```

## 完整代码
```java
package com.medusa.gruul.common.addon.model.handler;


import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;

/**
 * 视图处理器抽象
 * 插件声明周期
 *
 * @author 张治保
 * date 2022/3/2
 */
public interface AddonHook extends InitializingBean, DisposableBean {
    /**
     * 插件安装
     */
    void install();

    /**
     * 插件卸载
     */
    void uninstall();


    /**
     * 初始化
     */
    @Override
    default void afterPropertiesSet() {
        this.install();
    }

    /**
     * 容器销毁
     */
    @Override
    default void destroy() {
        this.uninstall();
    }
}

```

