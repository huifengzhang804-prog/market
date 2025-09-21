# com.medusa.gruul.common.spring.listener.GruulAutoConfigureSpringListener

**文件路径**: `spring\listener\GruulAutoConfigureSpringListener.java`

## 代码文档
///
@author 张治保
date 2022/2/15
 
///


## 文件结构
```
项目根目录
└── spring\listener
    └── GruulAutoConfigureSpringListener.java
```

## 完整代码
```java
package com.medusa.gruul.common.spring.listener;

import cn.hutool.core.collection.CollUtil;
import com.medusa.gruul.common.spring.listener.functions.GruulSpringListener;
import org.springframework.boot.ConfigurableBootstrapContext;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.SpringApplicationRunListener;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.env.ConfigurableEnvironment;

import java.time.Duration;
import java.util.ServiceLoader;

/**
 * @author 张治保
 * date 2022/2/15
 */
public class GruulAutoConfigureSpringListener implements SpringApplicationRunListener {

    private final ServiceLoader<GruulSpringListener> listeners;

    public GruulAutoConfigureSpringListener(SpringApplication application, String[] args) {
        listeners = ServiceLoader.load(GruulSpringListener.class);
        this.setArgs(application, args);
    }

    @Override
    public void starting(ConfigurableBootstrapContext bootstrapContext) {
        listeners.forEach(listener -> listener.starting(bootstrapContext));
    }

    @Override
    public void environmentPrepared(ConfigurableBootstrapContext bootstrapContext, ConfigurableEnvironment environment) {
        listeners.forEach(listener -> listener.environmentPrepared(bootstrapContext, environment));
    }

    @Override
    public void contextPrepared(ConfigurableApplicationContext context) {
        listeners.forEach(listener -> listener.contextPrepared(context));
    }

    @Override
    public void contextLoaded(ConfigurableApplicationContext context) {
        listeners.forEach(listener -> listener.contextLoaded(context));
    }

    @Override
    public void started(ConfigurableApplicationContext context, Duration timeTaken) {
        listeners.forEach(listener -> listener.started(context,timeTaken));
    }

    @Override
    public void ready(ConfigurableApplicationContext context, Duration timeTaken) {
        listeners.forEach(listener -> listener.ready(context,timeTaken));
    }

    @Override
    public void failed(ConfigurableApplicationContext context, Throwable exception) {
        listeners.forEach(listener -> listener.failed(context,exception));
    }

    private void setArgs(SpringApplication application, String[] args) {
        if (CollUtil.isEmpty(listeners)) {
            return;
        }
        listeners.forEach(
            listener->{
                listener.setApplication(application);
                listener.setArgs(args);
            }
        );
    }
}

```

# com.medusa.gruul.common.spring.listener.functions.GruulSpringListener

**文件路径**: `listener\functions\GruulSpringListener.java`

## 代码文档
///
@author 张治保
date 2022/2/15
 
///


## 文件结构
```
项目根目录
└── listener\functions
    └── GruulSpringListener.java
```

## 完整代码
```java
package com.medusa.gruul.common.spring.listener.functions;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.SpringApplicationRunListener;

/**
 * @author 张治保
 * date 2022/2/15
 */
@Getter
@Setter
public class GruulSpringListener implements SpringApplicationRunListener {
    private SpringApplication application;
    private String[] args;
}

```

