# com.medusa.gruul.common.xxl.job.CronHelper

**文件路径**: `xxl\job\CronHelper.java`

## 代码文档
///
cron表达式帮助类

@author 张治保
date 2023/3/18
 
///

///
将LocalDateTime转换为cron表达式

@param workTime 任务执行时间
@return cron表达式
     
///

///
将Duration转换为cron表达式
例如：Duration.ofMinutes(30) 返回30分钟后执行一次的cron表达式

@param duration 任务执行时间
                例如：Duration.ofMinutes(1) 1分钟后执行
                Duration.ofHours(1) 1小时后执行
@return cron表达式
     
///


## 文件结构
```
项目根目录
└── xxl\job
    └── CronHelper.java
```

## 完整代码
```java
package com.medusa.gruul.common.xxl.job;

import cn.hutool.cron.pattern.CronPatternBuilder;
import cn.hutool.cron.pattern.Part;

import java.time.Duration;
import java.time.LocalDateTime;

/**
 * cron表达式帮助类
 *
 * @author 张治保
 * date 2023/3/18
 */
public interface CronHelper {

    /**
     * 将LocalDateTime转换为cron表达式
     *
     * @param workTime 任务执行时间
     * @return cron表达式
     */
    static String toCron(LocalDateTime workTime) {
        return CronPatternBuilder.of()
                .set(Part.DAY_OF_MONTH, String.valueOf(workTime.getDayOfMonth()))
                .set(Part.DAY_OF_WEEK, "?")
                .set(Part.MONTH, String.valueOf(workTime.getMonthValue()))
                .set(Part.YEAR, String.valueOf(workTime.getYear()))
                .set(Part.HOUR, String.valueOf(workTime.getHour()))
                .set(Part.MINUTE, String.valueOf(workTime.getMinute()))
                .set(Part.SECOND, String.valueOf(workTime.getSecond()))
                .build();
    }

    /**
     * 将Duration转换为cron表达式
     * 例如：Duration.ofMinutes(30) 返回30分钟后执行一次的cron表达式
     *
     * @param duration 任务执行时间
     *                 例如：Duration.ofMinutes(1) 1分钟后执行
     *                 Duration.ofHours(1) 1小时后执行
     * @return cron表达式
     */
    static String toCron(Duration duration) {
        return toCron(LocalDateTime.now().plus(duration));
    }
}

```

# com.medusa.gruul.common.xxl.job.JobParent

**文件路径**: `xxl\job\JobParent.java`

## 代码文档
///
@author 张治保
@since 2024/6/17
 
///

///
任务描述

@return 任务描述
     
///

///
任务处理器

@return 任务处理器
     
///

///
任务执行cron生成器

@param t 参数
@return cron表达式
     
///

///
任务作者

@return 作者
     
///

///
任务调度类型

@return 调度类型
     
///

///
转换为xxl-job info

@param t 参数
@return xxl-job info
     
///

///
转换为xxl-job info

@param t             参数
@param executorParam 执行参数
@return xxl-job info
     
///


## 文件结构
```
项目根目录
└── xxl\job
    └── JobParent.java
```

## 完整代码
```java
package com.medusa.gruul.common.xxl.job;

import com.medusa.gruul.common.xxl.job.model.XxlJobInfo;

/**
 * @author 张治保
 * @since 2024/6/17
 */
public interface JobParent<T> {

    String DEFAULT_AUTHOR = "system";
    String DEFAULT_SCHEDULE_TYPE = "CRON";


    /**
     * 任务描述
     *
     * @return 任务描述
     */
    String desc();

    /**
     * 任务处理器
     *
     * @return 任务处理器
     */
    String handler();

    /**
     * 任务执行cron生成器
     *
     * @param t 参数
     * @return cron表达式
     */
    String cronGenerator(T t);

    /**
     * 任务作者
     *
     * @return 作者
     */
    default String author() {
        return DEFAULT_AUTHOR;
    }

    /**
     * 任务调度类型
     *
     * @return 调度类型
     */
    default String scheduleType() {
        return DEFAULT_SCHEDULE_TYPE;
    }

    /**
     * 转换为xxl-job info
     *
     * @param t 参数
     * @return xxl-job info
     */
    default XxlJobInfo toXxlJobInfo(T t) {
        return new XxlJobInfo()
                .setJobDesc(desc())
                .setAuthor(author())
                .setScheduleType(scheduleType())
                .setScheduleConf(cronGenerator(t))
                .setExecutorHandler(handler());
    }


    /**
     * 转换为xxl-job info
     *
     * @param t             参数
     * @param executorParam 执行参数
     * @return xxl-job info
     */
    default XxlJobInfo toXxlJobInfo(T t, String executorParam) {
        return new XxlJobInfo()
                .setJobDesc(desc())
                .setAuthor(author())
                .setScheduleType(scheduleType())
                .setScheduleConf(cronGenerator(t))
                .setExecutorHandler(handler())
                .setExecutorParam(executorParam);
    }
}

```

# com.medusa.gruul.common.xxl.job.XxlJobAutoconfigure

**文件路径**: `xxl\job\XxlJobAutoconfigure.java`

## 代码文档
///
xxl job 自动装配

@author 张治保
date 2023/3/17
 
///

///
获取执行器 端口

@return 端口
     
///

///
获取执行器名称

@param appName     配置名称
@param serviceName 服务名称
@return 执行器名称
     
///

///
获取执行器调用地址

@param routerAddress 路由地址
@param serviceName   服务名称
@return 执行器调用地址
     
///


## 文件结构
```
项目根目录
└── xxl\job
    └── XxlJobAutoconfigure.java
```

## 完整代码
```java
package com.medusa.gruul.common.xxl.job;

import cn.hutool.core.lang.Assert;
import cn.hutool.core.util.ArrayUtil;
import cn.hutool.core.util.StrUtil;
import com.medusa.gruul.common.model.constant.Xxl;
import com.medusa.gruul.common.xxl.job.service.impl.JobServiceImpl;
import com.xxl.job.core.executor.impl.XxlJobSpringExecutor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.core.env.Environment;

/**
 * xxl job 自动装配
 *
 * @author 张治保
 * date 2023/3/17
 */
@Slf4j
@Import(JobServiceImpl.class)
@RequiredArgsConstructor
@EnableConfigurationProperties(XxlJobProperties.class)
public class XxlJobAutoconfigure {

    private final Environment environment;

    @Bean
    @ConditionalOnProperty(prefix = "gruul.xxl-job.executor", name = "enabled", havingValue = "true", matchIfMissing = true)
    public XxlJobSpringExecutor xxlJobSpringExecutor(XxlJobProperties prop) {
        String serviceName = environment.getProperty("spring.application.name");
        XxlJobSpringExecutor executor = new XxlJobSpringExecutor();
        executor.setAdminAddresses(prop.getAdmin().getAdminAddresses());
        XxlJobProperties.ExecutorProperties executorProp = prop.getExecutor();
        executor.setAppname(this.getAppName(executorProp.getAppName(), serviceName));
        executor.setIp(Xxl.GATEWAY_XXL_EXEC_PATH);
        executor.setPort(this.getPort());
        executor.setAccessToken(executorProp.getAccessToken());
        executor.setLogPath(executorProp.getLogPath());
        executor.setLogRetentionDays(executorProp.getLogRetentionDays());
        executor.setAddress(this.getAddress(executorProp.getRouterAddress(), serviceName));
        return executor;
    }

    /**
     * 获取执行器 端口
     *
     * @return 端口
     */
    private int getPort() {
        Integer serverPort = environment.getProperty("server.port", Integer.class);
        serverPort = serverPort == null ? 8080 : serverPort;
        return serverPort + Xxl.GATEWAY_XXL_EXEC_PORT_OFFSET;
    }

    /**
     * 获取执行器名称
     *
     * @param appName     配置名称
     * @param serviceName 服务名称
     * @return 执行器名称
     */
    private String getAppName(String appName, String serviceName) {
        if (StrUtil.isEmpty(appName)) {
            String[] activeProfiles = environment.getActiveProfiles();
            String activeProfile = ArrayUtil.isEmpty(activeProfiles) ? "dev" : activeProfiles[0];
            appName = activeProfile + "-" + serviceName;
        }
        return appName;
    }

    /**
     * 获取执行器调用地址
     *
     * @param routerAddress 路由地址
     * @param serviceName   服务名称
     * @return 执行器调用地址
     */
    private String getAddress(String routerAddress, String serviceName) {
        Assert.isFalse(StrUtil.isBlank(routerAddress), "xxl-job executor address is null");
        routerAddress = routerAddress.trim();
        String property = environment.getProperty("gruul.single");
        boolean isSingle = StrUtil.isNotEmpty(property) && Boolean.parseBoolean(property);
        if (isSingle) {
            return routerAddress;
        }
        return (routerAddress.endsWith("/") ? routerAddress : (routerAddress + "/")) + serviceName + Xxl.GATEWAY_XXL_EXEC_PATH;
    }
}

```

# com.medusa.gruul.common.xxl.job.XxlJobProperties

**文件路径**: `xxl\job\XxlJobProperties.java`

## 代码文档
///
xxl-job 配置.

@author 张治保
 
///

///
admin 配置
     
///

///
executor 配置.
     
///

///
xxl job admin 地址.
         
///

///
是否启用该执行器
         
///

///
xxl job executor id
         
///

///
app name
         
///

///
xxl 注册到 xxl job 服务端的路由地址.
         
///

///
xxl job admin registry access token.
         
///

///
xxl job log files path.
         
///

///
xxl job log files retention days.
         
///


## 文件结构
```
项目根目录
└── xxl\job
    └── XxlJobProperties.java
```

## 完整代码
```java
package com.medusa.gruul.common.xxl.job;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * xxl-job 配置.
 *
 * @author 张治保
 */
@Data
@ConfigurationProperties(prefix = "gruul.xxl-job")
public class XxlJobProperties {

    /**
     * admin 配置
     */
    private AdminProperties admin = new AdminProperties();

    /**
     * executor 配置.
     */
    private ExecutorProperties executor = new ExecutorProperties();


    @Data
    public static class AdminProperties {
        /**
         * xxl job admin 地址.
         */
        private String adminAddresses = "http://127.0.0.1:8080/xxl-job-admin";
    }


    @Data
    public static class ExecutorProperties {

        /**
         * 是否启用该执行器
         */
        private boolean enabled = true;

        /**
         * xxl job executor id
         */
        private Integer id = 1;
        /**
         * app name
         */
        private String appName = "";

        /**
         * xxl 注册到 xxl job 服务端的路由地址.
         */
        private String routerAddress;
        /**
         * xxl job admin registry access token.
         */
        private String accessToken;
        /**
         * xxl job log files path.
         */
        private String logPath = "logs/applogs/xxl-job/jobhandler";
        /**
         * xxl job log files retention days.
         */
        private Integer logRetentionDays = 30;


    }
}

```

# com.medusa.gruul.common.xxl.job.model.ExecutorBlockStrategyEnum

**文件路径**: `job\model\ExecutorBlockStrategyEnum.java`

## 代码文档
///
Created by xuxueli on 17/5/9.
 
///

///
串行
     
///

///
丢弃后续调度
     
///

///
覆盖之前调度
     
///


## 文件结构
```
项目根目录
└── job\model
    └── ExecutorBlockStrategyEnum.java
```

## 完整代码
```java
package com.medusa.gruul.common.xxl.job.model;

/**
 * Created by xuxueli on 17/5/9.
 */
public enum ExecutorBlockStrategyEnum {

    /**
     * 串行
     */
    SERIAL_EXECUTION,
    /*CONCURRENT_EXECUTION("并行"),*/
    /**
     * 丢弃后续调度
     */
    DISCARD_LATER,

    /**
     * 覆盖之前调度
     */
    COVER_EARLY
}

```

# com.medusa.gruul.common.xxl.job.model.ExecutorRouteStrategyEnum

**文件路径**: `job\model\ExecutorRouteStrategyEnum.java`

## 代码文档
///
<p></p>

@author 张治保
date 2023/3/17
 
///

///
第一个
     
///

///
最后一个
     
///

///
轮询
     
///

///
随机
     
///

///
一致性HASH
     
///

///
最不经常使用
     
///

///
最久未使用
     
///

///
忙碌转移
     
///

///
分片广播
     
///


## 文件结构
```
项目根目录
└── job\model
    └── ExecutorRouteStrategyEnum.java
```

## 完整代码
```java
package com.medusa.gruul.common.xxl.job.model;

/**
 * <p></p>
 *
 * @author 张治保
 * date 2023/3/17
 */
public enum ExecutorRouteStrategyEnum {
    /**
     * 第一个
     */
    FIRST,
    /**
     * 最后一个
     */
    LAST,
    /**
     * 轮询
     */
    ROUND,
    /**
     * 随机
     */
    RANDOM,
    /**
     * 一致性HASH
     */
    CONSISTENT_HASH,
    /**
     * 最不经常使用
     */
    LEAST_FREQUENTLY_USED,
    /**
     * 最久未使用
     */
    LEAST_RECENTLY_USED,
    /**
     * 忙碌转移
     */
    BUSYOVER,
    /**
     * 分片广播
     */
    SHARDING_BROADCAST
}

```

# com.medusa.gruul.common.xxl.job.model.GlueTypeEnum

**文件路径**: `job\model\GlueTypeEnum.java`

## 代码文档
///
Created by xuxueli on 17/4/26.
 
///

///
BEAN
     
///

///
GLUE(Java)
     
///

///
GLUE(Script)
     
///

///
GLUE(Python)
     
///

///
GLUE(PHP)
     
///

///
GLUE(Nodejs)
     
///

///
GLUE(Powershell)
     
///


## 文件结构
```
项目根目录
└── job\model
    └── GlueTypeEnum.java
```

## 完整代码
```java
package com.medusa.gruul.common.xxl.job.model;

/**
 * Created by xuxueli on 17/4/26.
 */
public enum GlueTypeEnum {

    /**
     * BEAN
     */
    BEAN,
    /**
     * GLUE(Java)
     */
    GLUE_GROOVY,
    /**
     * GLUE(Script)
     */
    GLUE_SHELL,
    /**
     * GLUE(Python)
     */
    GLUE_PYTHON,
    /**
     * GLUE(PHP)
     */
    GLUE_PHP,
    /**
     * GLUE(Nodejs)
     */
    GLUE_NODEJS,
    /**
     * GLUE(Powershell)
     */
    GLUE_POWERSHELL

}

```

# com.medusa.gruul.common.xxl.job.model.MisfireStrategyEnum

**文件路径**: `job\model\MisfireStrategyEnum.java`

## 代码文档
///
@author xuxueli 2020-10-29 21:11:23
 
///

///
do nothing
     
///

///
fire once now
     
///


## 文件结构
```
项目根目录
└── job\model
    └── MisfireStrategyEnum.java
```

## 完整代码
```java
package com.medusa.gruul.common.xxl.job.model;

/**
 * @author xuxueli 2020-10-29 21:11:23
 */
public enum MisfireStrategyEnum {

    /**
     * do nothing
     */
    DO_NOTHING,

    /**
     * fire once now
     */
    FIRE_ONCE_NOW

}

```

# com.medusa.gruul.common.xxl.job.model.ScheduleTypeEnum

**文件路径**: `job\model\ScheduleTypeEnum.java`

## 代码文档
///
@author xuxueli 2020-10-29 21:11:23
 
///

///
none
     
///

///
schedule by cron
     
///

///
schedule by fixed rate (in seconds)
     
///


## 文件结构
```
项目根目录
└── job\model
    └── ScheduleTypeEnum.java
```

## 完整代码
```java
package com.medusa.gruul.common.xxl.job.model;


/**
 * @author xuxueli 2020-10-29 21:11:23
 */
public enum ScheduleTypeEnum {

    /**
     * none
     */
    NONE,

    /**
     * schedule by cron
     */
    CRON,

    /**
     * schedule by fixed rate (in seconds)
     */
    FIX_RATE,


}

```

# com.medusa.gruul.common.xxl.job.model.TriggerStatusEnum

**文件路径**: `job\model\TriggerStatusEnum.java`

## 代码文档
///
<p></p>

@author 张治保
date 2023/3/20
 
///

///
停止
     
///

///
启动
     
///


## 文件结构
```
项目根目录
└── job\model
    └── TriggerStatusEnum.java
```

## 完整代码
```java
package com.medusa.gruul.common.xxl.job.model;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * <p></p>
 *
 * @author 张治保
 * date 2023/3/20
 */
@Getter
@RequiredArgsConstructor
public enum TriggerStatusEnum {

    /**
     * 停止
     */
    STOP(0),
    
    /**
     * 启动
     */
    START(1);

    private final int code;
}

```

# com.medusa.gruul.common.xxl.job.model.XxlJobInfo

**文件路径**: `job\model\XxlJobInfo.java`

## 代码文档
///
xxl-job info

@author xuxueli  2016-1-12 18:25:49
 
///

///
任务主键
     
///

///
执行器主键ID 必填
     
///

///
任务描述 必填
     
///

///
负责人 必填
     
///

///
报警邮件
     
///

///
调度类型 ScheduleTypeEnum 必填
     
///

///
调度配置，值含义取决于调度类型
     
///

///
执行器路由策略  ExecutorRouteStrategyEnum 必填
     
///

///
执行器，任务Handler名称
     
///

///
执行器，任务参数
     
///

///
调度过期策略 MisfireStrategyEnum 必填
     
///

///
阻塞处理策略 ExecutorBlockStrategyEnum 必填
     
///

///
任务执行超时时间，单位秒
     
///

///
失败重试次数
     
///

///
GLUE类型 GlueTypeEnum 必填
     
///

///
GLUE源代码
     
///

///
GLUE备注
     
///

///
子任务ID，多个逗号分隔
     
///

///
调度状态：0-停止，1-运行 默认 运行
     
///


## 文件结构
```
项目根目录
└── job\model
    └── XxlJobInfo.java
```

## 完整代码
```java
package com.medusa.gruul.common.xxl.job.model;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

/**
 * xxl-job info
 *
 * @author xuxueli  2016-1-12 18:25:49
 */
@Getter
@Setter
@Accessors(chain = true)
public class XxlJobInfo {

    /**
     * 任务主键
     */
    private int id;
    /*------------------- 基础配置 ------------------------*/
    /**
     * 执行器主键ID 必填
     */
    private int jobGroup;

    /**
     * 任务描述 必填
     */
    private String jobDesc;

    /**
     * 负责人 必填
     */
    private String author;

    /**
     * 报警邮件
     */
    private String alarmEmail;

    /*------------------- 调度配置 ------------------------*/
    /**
     * 调度类型 ScheduleTypeEnum 必填
     */
    private String scheduleType = ScheduleTypeEnum.CRON.name();

    /**
     * 调度配置，值含义取决于调度类型
     */
    private String scheduleConf;


    /*------------------- 任务配置 ------------------------*/
    /**
     * 执行器路由策略  ExecutorRouteStrategyEnum 必填
     */
    private String executorRouteStrategy = ExecutorRouteStrategyEnum.RANDOM.name();

    /**
     * 执行器，任务Handler名称
     */
    private String executorHandler;

    /**
     * 执行器，任务参数
     */
    private String executorParam;


    /*------------------- 基础配置 ------------------------*/
    /**
     * 调度过期策略 MisfireStrategyEnum 必填
     */
    private String misfireStrategy = MisfireStrategyEnum.DO_NOTHING.name();

    /**
     * 阻塞处理策略 ExecutorBlockStrategyEnum 必填
     */
    private String executorBlockStrategy = ExecutorBlockStrategyEnum.SERIAL_EXECUTION.name();

    /**
     * 任务执行超时时间，单位秒
     */
    private int executorTimeout = 0;

    /**
     * 失败重试次数
     */
    private int executorFailRetryCount;

    /**
     * GLUE类型 GlueTypeEnum 必填
     */
    private String glueType = GlueTypeEnum.BEAN.name();

    /**
     * GLUE源代码
     */
    private String glueSource;

    /**
     * GLUE备注
     */
    private String glueRemark;

    /**
     * 子任务ID，多个逗号分隔
     */
    private String childJobId;

    /**
     * 调度状态：0-停止，1-运行 默认 运行
     */
    private int triggerStatus = TriggerStatusEnum.START.getCode();

}

```

# com.medusa.gruul.common.xxl.job.service.JobService

**文件路径**: `job\service\JobService.java`

## 代码文档
///
@author 张治保
date 2023/3/17
 
///

///
编辑xxl任务请求路径
     
///

///
删除任务
     
///

///
启动xxl任务请求路径
     
///

///
停止xxl任务请求路径
     
///

///
新增任务

@param jobParam 任务参数
@return 任务id
     
///

///
更新任务

@param jobParam 任务参数
     
///

///
删除任务

@param jobId 任务id
     
///

///
启动任务

@param jobId 任务id
     
///

///
停止任务

@param jobId 任务id
     
///


## 文件结构
```
项目根目录
└── job\service
    └── JobService.java
```

## 完整代码
```java
package com.medusa.gruul.common.xxl.job.service;


import com.medusa.gruul.common.xxl.job.model.XxlJobInfo;

/**
 * @author 张治保
 * date 2023/3/17
 */
public interface JobService {

    String URL_PREFIX = "/jobinfo/2b";
    String ADD_TASK_URL = URL_PREFIX + "/add";
    /**
     * 编辑xxl任务请求路径
     */
    String UPDATE_TASK_URL = URL_PREFIX + "/update";
    /**
     * 删除任务
     */
    String REMOVE_TASK_URL = URL_PREFIX + "/remove";
    /**
     * 启动xxl任务请求路径
     */
    String START_TASK_URL = URL_PREFIX + "/start";
    /**
     * 停止xxl任务请求路径
     */
    String STOP_TASK_URL = URL_PREFIX + "/stop";

    /**
     * 新增任务
     *
     * @param jobParam 任务参数
     * @return 任务id
     */
    int add(XxlJobInfo jobParam);

    /**
     * 更新任务
     *
     * @param jobParam 任务参数
     */
    void update(XxlJobInfo jobParam);

    /**
     * 删除任务
     *
     * @param jobId 任务id
     */
    void remove(int jobId);

    /**
     * 启动任务
     *
     * @param jobId 任务id
     */
    void start(int jobId);

    /**
     * 停止任务
     *
     * @param jobId 任务id
     */
    void stop(int jobId);

}

```

# com.medusa.gruul.common.xxl.job.service.impl.JobServiceImpl

**文件路径**: `service\impl\JobServiceImpl.java`

## 代码文档
///
@author 张治保
date 2023/3/17
 
///


## 文件结构
```
项目根目录
└── service\impl
    └── JobServiceImpl.java
```

## 完整代码
```java
package com.medusa.gruul.common.xxl.job.service.impl;

import com.medusa.gruul.common.xxl.job.XxlJobProperties;
import com.medusa.gruul.common.xxl.job.model.XxlJobInfo;
import com.medusa.gruul.common.xxl.job.service.JobService;
import com.xxl.job.core.biz.model.ReturnT;
import com.xxl.job.core.util.XxlJobRemotingUtil;

import java.util.Map;

/**
 * @author 张治保
 * date 2023/3/17
 */
public class JobServiceImpl implements JobService {

    private final String adminAddress;
    private final String accessToken;
    private final int groupId;


    public JobServiceImpl(XxlJobProperties xxlJobProperties) {
        this.adminAddress = xxlJobProperties.getAdmin().getAdminAddresses();
        this.accessToken = xxlJobProperties.getExecutor().getAccessToken();
        this.groupId = xxlJobProperties.getExecutor().getId();
    }


    @Override
    @SuppressWarnings("unchecked")
    public int add(XxlJobInfo jobParam) {
        if (jobParam.getJobGroup() == 0) {
            jobParam.setJobGroup(groupId);
        }
        ReturnT<String> result = XxlJobRemotingUtil.postBody(adminAddress + ADD_TASK_URL, accessToken, 6000, jobParam, String.class);
        if (result.getCode() != ReturnT.SUCCESS_CODE) {
            throw new RuntimeException(result.getMsg());
        }
        return Integer.parseInt(result.getContent());
    }

    @Override
    @SuppressWarnings("unchecked")
    public void update(XxlJobInfo jobParam) {
        if (jobParam.getJobGroup() == 0) {
            jobParam.setJobGroup(groupId);
        }
        ReturnT<String> result = XxlJobRemotingUtil.postBody(adminAddress + UPDATE_TASK_URL, accessToken, 6000, jobParam, String.class);
        if (result.getCode() != ReturnT.SUCCESS_CODE) {
            throw new RuntimeException(result.getMsg());
        }
    }

    @Override
    @SuppressWarnings("unchecked")
    public void remove(int jobId) {
        ReturnT<String> result = XxlJobRemotingUtil.postBody(adminAddress + REMOVE_TASK_URL, accessToken, 6000, Map.of("id", jobId), String.class);
        if (result.getCode() != ReturnT.SUCCESS_CODE) {
            throw new RuntimeException(result.getMsg());
        }

    }

    @Override
    @SuppressWarnings("unchecked")
    public void start(int jobId) {
        ReturnT<String> result = XxlJobRemotingUtil.postBody(adminAddress + START_TASK_URL, accessToken, 6000, Map.of("id", jobId), String.class);
        if (result.getCode() != ReturnT.SUCCESS_CODE) {
            throw new RuntimeException(result.getMsg());
        }

    }

    @Override
    @SuppressWarnings("unchecked")
    public void stop(int jobId) {
        ReturnT<String> result = XxlJobRemotingUtil.postBody(adminAddress + STOP_TASK_URL, accessToken, 6000, Map.of("id", jobId), String.class);
        if (result.getCode() != ReturnT.SUCCESS_CODE) {
            throw new RuntimeException(result.getMsg());
        }
    }
}

```

