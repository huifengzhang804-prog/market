# com.medusa.gruul.common.dubbo.rpc.IDynamicDubbo

**文件路径**: `dubbo\rpc\IDynamicDubbo.java`

## 代码文档
///
@author 张治保
duubo3 配置文件 {@link DubboConfigurationProperties}
date 2022/9/13
 
///

///
获取泛化服务

@param interfaceName 接口名称
@return 泛化服务
     
///


## 文件结构
```
项目根目录
└── dubbo\rpc
    └── IDynamicDubbo.java
```

## 完整代码
```java
package com.medusa.gruul.common.dubbo.rpc;

import io.vavr.control.Option;
import lombok.RequiredArgsConstructor;
import org.apache.dubbo.config.ConsumerConfig;
import org.apache.dubbo.config.ReferenceConfig;
import org.apache.dubbo.config.utils.SimpleReferenceCache;
import org.apache.dubbo.rpc.service.GenericService;
import org.apache.dubbo.spring.boot.autoconfigure.DubboConfigurationProperties;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @author 张治保
 * duubo3 配置文件 {@link DubboConfigurationProperties}
 * date 2022/9/13
 */
@RequiredArgsConstructor
public class IDynamicDubbo {

    private static final String GENERIC = "true";
    private static ConsumerConfig consumerConfig;

    public static GenericService genericService(String interfaceName) {
        SimpleReferenceCache cache = SimpleReferenceCache.getCache();
        GenericService genericService = cache.get(interfaceName);
        return Option.of(genericService)
                .getOrElse(
                        () -> {
                            GenericService service = cache.get(interfaceName);
                            if (service != null) {
                                return service;
                            }
                            synchronized (interfaceName.intern()) {
                                service = cache.get(interfaceName);
                                if (service != null) {
                                    return service;
                                }
                                ReferenceConfig<GenericService> referenceConfig = getGenericServiceReferenceConfig(interfaceName);
                                return cache.get(referenceConfig);
                            }
                        }
                );
    }

    /**
     * 获取泛化服务
     *
     * @param interfaceName 接口名称
     * @return 泛化服务
     */
    @NotNull
    private static ReferenceConfig<GenericService> getGenericServiceReferenceConfig(String interfaceName) {
        ReferenceConfig<GenericService> referenceConfig = new ReferenceConfig<>();
        referenceConfig.setId(interfaceName);
        referenceConfig.setInterface(interfaceName);
        referenceConfig.setConsumer(IDynamicDubbo.consumerConfig);
        referenceConfig.setTimeout(IDynamicDubbo.consumerConfig.getTimeout());
        referenceConfig.setGeneric(GENERIC);
        referenceConfig.setAsync(Boolean.FALSE);
        return referenceConfig;
    }

    @Autowired
    public void setConsumerConfig(DubboConfigurationProperties dubboConfigurationProperties) {
        IDynamicDubbo.consumerConfig = dubboConfigurationProperties.getConsumer();
    }
}

```

# com.medusa.gruul.common.dubbo.rpc.filter.DubboExceptionFilter

**文件路径**: `rpc\filter\DubboExceptionFilter.java`

## 代码文档
///
自定义dubbo provider异常处理
<p>
参考dubbo的异常过滤器

@author 张治保
date 2022/7/15
@see ExceptionFilter
 
///


## 文件结构
```
项目根目录
└── rpc\filter
    └── DubboExceptionFilter.java
```

## 完整代码
```java
package com.medusa.gruul.common.dubbo.rpc.filter;

import com.medusa.gruul.global.model.exception.GlobalException;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.common.constants.CommonConstants;
import org.apache.dubbo.common.extension.Activate;
import org.apache.dubbo.rpc.Invocation;
import org.apache.dubbo.rpc.Invoker;
import org.apache.dubbo.rpc.Result;
import org.apache.dubbo.rpc.filter.ExceptionFilter;


/**
 * 自定义dubbo provider异常处理
 * <p>
 * 参考dubbo的异常过滤器
 *
 * @author 张治保
 * date 2022/7/15
 * @see ExceptionFilter
 */
@Slf4j
@Activate(group = CommonConstants.PROVIDER)
public class DubboExceptionFilter extends ExceptionFilter {


    @Override
    public void onResponse(Result appResponse, Invoker<?> invoker, Invocation invocation) {
        if (!appResponse.hasException()) {
            return;
        }
        Throwable exception = appResponse.getException();
        super.onError(exception, invoker, invocation);
        if (exception instanceof GlobalException) {
            return;
        }
        super.onResponse(appResponse, invoker, invocation);
    }

}

```

# com.medusa.gruul.common.dubbo.rpc.filter.DubboGenericFilter

**文件路径**: `rpc\filter\DubboGenericFilter.java`

## 代码文档
///
GenericInvokerFilter.

@author 张治保
@see GenericFilter
 
///

///
判断是否需要跳过父类的方法

@return 是否需要跳过父类的方法
     
///

///
获取泛化类型

@param inv Invocation
@return 泛化类型
     
///


## 文件结构
```
项目根目录
└── rpc\filter
    └── DubboGenericFilter.java
```

## 完整代码
```java
/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.medusa.gruul.common.dubbo.rpc.filter;

import com.medusa.gruul.global.model.exception.GlobalException;
import org.apache.dubbo.common.constants.CommonConstants;
import org.apache.dubbo.common.extension.Activate;
import org.apache.dubbo.common.utils.StringUtils;
import org.apache.dubbo.rpc.Invocation;
import org.apache.dubbo.rpc.Invoker;
import org.apache.dubbo.rpc.Result;
import org.apache.dubbo.rpc.RpcContext;
import org.apache.dubbo.rpc.filter.GenericFilter;
import org.apache.dubbo.rpc.service.GenericService;
import org.apache.dubbo.rpc.support.ProtocolUtils;

import static org.apache.dubbo.common.constants.CommonConstants.$INVOKE;
import static org.apache.dubbo.common.constants.CommonConstants.$INVOKE_ASYNC;
import static org.apache.dubbo.rpc.Constants.GENERIC_KEY;

/**
 * GenericInvokerFilter.
 *
 * @author 张治保
 * @see GenericFilter
 */
@Activate(group = CommonConstants.PROVIDER, order = -20000)
public class DubboGenericFilter extends GenericFilter {

    @Override
    public void onResponse(Result appResponse, Invoker<?> invoker, Invocation inv) {
        if (breakSuper(appResponse, invoker, inv)) {
            return;
        }
        super.onResponse(appResponse, invoker, inv);

    }

    /**
     * 判断是否需要跳过父类的方法
     *
     * @return 是否需要跳过父类的方法
     */
    private boolean breakSuper(Result appResponse, Invoker<?> invoker, Invocation inv) {
        //是否是泛化调用
        boolean isGenericInvoke = inv.getMethodName().equals($INVOKE) || inv.getMethodName().equals($INVOKE_ASYNC);
        if (!isGenericInvoke || inv.getArguments() == null || inv.getArguments().length != 3 || GenericService.class.isAssignableFrom(invoker.getInterface())) {
            return true;
        }
        //是否是 GlobalException异常
        if (appResponse.hasException() && appResponse.getException() instanceof GlobalException) {
            return true;
        }
        //是否是 其它序列化方式
        String generic = getGenericValue(inv);
        return !ProtocolUtils.isJavaGenericSerialization(generic) && !ProtocolUtils.isBeanGenericSerialization(generic)
                && !ProtocolUtils.isProtobufGenericSerialization(generic) && !ProtocolUtils.isGenericReturnRawResult(generic);
    }

    /**
     * 获取泛化类型
     *
     * @param inv Invocation
     * @return 泛化类型
     */
    private String getGenericValue(Invocation inv) {
        String generic = inv.getAttachment(GENERIC_KEY);
        if (StringUtils.isNotBlank(generic)) {
            return generic;
        }
        generic = RpcContext.getServerAttachment().getAttachment(GENERIC_KEY);
        if (StringUtils.isBlank(generic)) {
            generic = RpcContext.getClientAttachment().getAttachment(GENERIC_KEY);
        }
        return generic;
    }

}

```

