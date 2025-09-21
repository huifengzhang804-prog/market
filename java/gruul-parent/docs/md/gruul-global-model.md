# com.medusa.gruul.global.model.constant.AspectOrder

**文件路径**: `model\constant\AspectOrder.java`

## 代码文档
///
定义 切面执行顺序
同一切点  越小越先执行 越大越后执行

@author 张治保
date 2022/3/10
 
///

///
日志打印切面
     
///

///
idem 幂等校验 切面
     
///

///
transaction @Transactional
     
///

///
数据源切换切面
     
///

///
cache 缓存 @Cacheable @CacheEvict ..
     
///

///
Redisson分布式锁 切面
     
///


## 文件结构
```
项目根目录
└── model\constant
    └── AspectOrder.java
```

## 完整代码
```java
package com.medusa.gruul.global.model.constant;


/**
 * 定义 切面执行顺序
 * 同一切点  越小越先执行 越大越后执行
 *
 * @author 张治保
 * date 2022/3/10
 */
public interface AspectOrder {

    /* 从小到大   */

    /**
     * 日志打印切面
     */
    int LOG_ASPECT = Integer.MIN_VALUE;

    /**
     * idem 幂等校验 切面
     */
    int IDEM_ASPECT = AspectOrder.LOG_ASPECT + 100;


    /* 从大到小   */
    /**
     * transaction @Transactional
     */
    int TRANSACTIONAL_ASPECT = Integer.MAX_VALUE;
    
    /**
     * 数据源切换切面
     */
    int DATASOURCE_ASPECT = AspectOrder.TRANSACTIONAL_ASPECT - 100;

    /**
     * cache 缓存 @Cacheable @CacheEvict ..
     */
    int CACHE_ASPECT = AspectOrder.DATASOURCE_ASPECT - 100;

    /**
     * Redisson分布式锁 切面
     */
    int REDISSON_ASPECT = AspectOrder.CACHE_ASPECT - 100;


}

```

# com.medusa.gruul.global.model.constant.GlobalCode

**文件路径**: `model\constant\GlobalCode.java`

## 代码文档
///
@author 张治保
date 2022/3/25
 
///

///
无效请求
	 
///


## 文件结构
```
项目根目录
└── model\constant
    └── GlobalCode.java
```

## 完整代码
```java
package com.medusa.gruul.global.model.constant;

/**
 * @author 张治保
 * date 2022/3/25
 */
public interface GlobalCode {


	/**
	 * 无效请求
	 */
	int REQUEST_INVALID = 12;
}

```

# com.medusa.gruul.global.model.constant.RegexPools

**文件路径**: `model\constant\RegexPools.java`

## 代码文档
///
@author 张治保
date 2022/4/15
 
///

///
任意字符
     
///

///
非空字符
     
///

///
日期 -
     
///

///
时间：
     
///

///
日期时间
     
///

///
用户昵称 只允许中英文与数字
     
///

///
密码包含数字、小写字母、大写字母 至少两种 6-20位
     
///

///
跟密码相同
     
///

///
手机号|座机号正则
     
///

///
不能包含空字符（换行 回车 空格 等）
     
///

///
只能为汉字
     
///


## 文件结构
```
项目根目录
└── model\constant
    └── RegexPools.java
```

## 完整代码
```java
package com.medusa.gruul.global.model.constant;

import cn.hutool.core.lang.RegexPool;

/**
 * @author 张治保
 * date 2022/4/15
 */
public interface RegexPools extends RegexPool {

    /**
     * 任意字符
     */
    String ANY = "[\\s\\S]*";

    /**
     * 非空字符
     */
    String NOT_BLANK = ANY + "\\S" + ANY;


    /**
     * 日期 -
     */
    String DATE = "((\\d{3}[1-9]|\\d{2}[1-9]\\d{1}|\\d{1}[1-9]\\d{2}|[1-9]\\d{3})-(((0[13578]|1[02])-(0[1-9]|[12]\\d|3[01]))|((0[469]|11)-(0[1-9]|[12]\\d|30))|(02-(0[1-9]|[1]\\d|2[0-8]))))|(((\\d{2})(0[48]|[2468][048]|[13579][26])|((0[48]|[2468][048]|[3579][26])00))-02-29)((\\d{3}[1-9]|\\d{2}[1-9]\\d{1}|\\d{1}[1-9]\\d{2}|[1-9]\\d{3})-(((0[13578]|1[02])-(0[1-9]|[12]\\d|3[01]))|((0[469]|11)-(0[1-9]|[12]\\d|30))|(02-(0[1-9]|[1]\\d|2[0-8]))))|(((\\d{2})(0[48]|[2468][048]|[13579][26])|((0[48]|[2468][048]|[3579][26])00))-02-29)";

    /**
     * 时间：
     */
    String TIME = RegexPool.TIME;

    /**
     * 日期时间
     */
    String DATETIME = "(" + DATE + ")" + "\\s" + "(" + TIME + ")";

    /**
     * 用户昵称 只允许中英文与数字
     */
    String NICKNAME = "^[a-z0-9A-Z\\u4e00-\\u9fa5]+$";
    /**
     * 密码包含数字、小写字母、大写字母 至少两种 6-20位
     */
    String PASSWORD = "^(?![A-Z]+$)(?![a-z]+$)(?!\\d+$)(?![\\W_]+$)\\S{6,20}$";
    /**
     * 跟密码相同
     */
    String USERNAME = RegexPools.PASSWORD;
    /**
     * 手机号|座机号正则
     */
    String MOBILE_TEL = "((?:0|86|\\+86)?1[3-9]\\d{9})|((010|02\\d|0[3-9]\\d{2})-?(\\d{6,8}))";

    /**
     * 不能包含空字符（换行 回车 空格 等）
     */
    String NOT_CONTAIN_BLANK = "^$|^(?!.*\\s).+$";

    /**
     * 只能为汉字
     */
    String ONLY_CHINESE = "^[\\u4e00-\\u9fa5]+$";

}

```

# com.medusa.gruul.global.model.constant.SecurityConst

**文件路径**: `model\constant\SecurityConst.java`

## 代码文档
///
@author 张治保
date 2022/2/24
 
///

///
没有shop id的客户端的shop id
     
///

///
客户端id
     
///

///
作用域scope
     
///

///
用户id
     
///

///
email
     
///

///
mobile
     
///


## 文件结构
```
项目根目录
└── model\constant
    └── SecurityConst.java
```

## 完整代码
```java
package com.medusa.gruul.global.model.constant;

/**
 * @author 张治保
 * date 2022/2/24
 */
public interface SecurityConst {

    /**
     * 没有shop id的客户端的shop id
     */
    long NO_SHOP_ID_CLIENT_DEFAULT_SHOP_ID = 0;

    /**
     * 客户端id
     */
    String CLIENT_ID = "clientId";
    /**
     * 作用域scope
     */
    String SCOPE = "scope";
    /**
     * 用户id
     */
    String USER_ID = "userId";
    /**
     * email
     */
    String EMAIL = "email";
    /**
     * mobile
     */
    String MOBILE = "mobile";

}

```

# com.medusa.gruul.global.model.enums.Mode

**文件路径**: `model\enums\Mode.java`

## 代码文档
///
运行业务模式
@author 张治保
date 2022/4/18
 
///

///
B2C
     
///

///
B2B2C
     
///

///
B2B
     
///

///
S2B2C
     
///

///
O2O
     
///


## 文件结构
```
项目根目录
└── model\enums
    └── Mode.java
```

## 完整代码
```java
package com.medusa.gruul.global.model.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * 运行业务模式
 * @author 张治保
 * date 2022/4/18
 */
@Getter
@RequiredArgsConstructor
public enum  Mode {
    /**
     * B2C
     */
    B2C(0),
    /**
     * B2B2C
     */
    B2B2C(1),
    /**
     * B2B
     */
    B2B(2),
    /**
     * S2B2C
     */
    S2B2C(3),

    /**
     * O2O
     */
    O2O(4);


    private final Integer value;
}

```

# com.medusa.gruul.global.model.exception.Error

**文件路径**: `model\exception\Error.java`

## 代码文档
///
错误异常抽象类

@author 张治保
date 2022/4/20
 
///

///
错误代码

@return 错误代码
     
///

///
错误提示

@return 错误提示
     
///

///
返回的异常数据

@return 异常数据
     
///

///
exception msg with arguments

@param args 参数
@return 错误提示
     
///

///
获取异常

@return 渲染异常
     
///

///
获取异常

@param args 模板参数
@return 渲染异常
     
///

///
条件为false抛出异常

@param success 条件
     
///

///
条件为true抛出异常

@param success 条件
     
///

///
指定使用消息

@param msg 替换的消息
@return 渲染异常
     
///

///
获取异常

@param data 异常提示数据
@return 渲染异常
     
///

///
获取异常

@param data 异常提示数据
@param args 模板参数
@return 渲染异常
     
///


## 文件结构
```
项目根目录
└── model\exception
    └── Error.java
```

## 完整代码
```java
package com.medusa.gruul.global.model.exception;

import java.io.Serializable;

/**
 * 错误异常抽象类
 *
 * @author 张治保
 * date 2022/4/20
 */
public interface Error extends Serializable {
    /**
     * 错误代码
     *
     * @return 错误代码
     */
    int code();

    /**
     * 错误提示
     *
     * @return 错误提示
     */
    String msg();

    /**
     * 返回的异常数据
     *
     * @return 异常数据
     */
    default Object data() {
        return null;
    }

    /**
     * exception msg with arguments
     *
     * @param args 参数
     * @return 错误提示
     */
    default String msg(Object... args) {
        return msg();
    }

    /**
     * 获取异常
     *
     * @return 渲染异常
     */
    default GlobalException exception() {
        if (this instanceof GlobalException globalException) {
            return globalException;
        }
        return new GlobalException(code(), msg());
    }

    /**
     * 获取异常
     *
     * @param args 模板参数
     * @return 渲染异常
     */
    default GlobalException exception(Object... args) {
        return new GlobalException(code(), msg(args));
    }

    /**
     * 条件为false抛出异常
     *
     * @param success 条件
     */
    default void falseThrow(boolean success) {
        if (success) {
            return;
        }
        throw this.exception();
    }

    /**
     * 条件为true抛出异常
     *
     * @param success 条件
     */
    default void trueThrow(boolean success) {
        if (success) {
            throw this.exception();
        }
    }

    /**
     * 指定使用消息
     *
     * @param msg 替换的消息
     * @return 渲染异常
     */
    default GlobalException msgEx(String msg) {
        return new GlobalException(code(), msg);
    }

    /**
     * 获取异常
     *
     * @param data 异常提示数据
     * @return 渲染异常
     */
    default GlobalException dataEx(Object data) {
        GlobalException globalException = new GlobalException(msg());
        globalException.setCode(code());
        globalException.setData(data);
        return globalException;
    }


    /**
     * 获取异常
     *
     * @param data 异常提示数据
     * @param args 模板参数
     * @return 渲染异常
     */
    default GlobalException dataEx(Object data, Object... args) {
        GlobalException globalException = new GlobalException(msg(args));
        globalException.setCode(code());
        globalException.setData(data);
        return globalException;
    }
}

```

# com.medusa.gruul.global.model.exception.GlobalException

**文件路径**: `model\exception\GlobalException.java`

## 代码文档
///
@author 张治保
date 2022/3/25
 
///

///
自定义状态码
     
///

///
异常数据提示
     
///


## 文件结构
```
项目根目录
└── model\exception
    └── GlobalException.java
```

## 完整代码
```java
package com.medusa.gruul.global.model.exception;

import lombok.Getter;
import lombok.Setter;

/**
 * @author 张治保
 * date 2022/3/25
 */
@Getter
@Setter
public class GlobalException extends RuntimeException implements Error {


    /**
     * 自定义状态码
     */
    private int code;

    /**
     * 异常数据提示
     */
    private Object data;

    public GlobalException(String message, int code, Object data) {
        this(code, message);
        this.data = data;
    }

    public GlobalException(Error code) {
        super(code.msg());
        this.code = code.code();
    }

    public GlobalException() {
        super();
        this.code = 500;
    }

    public GlobalException(int code, String message) {
        super(message);
        this.code = code;
    }

    public GlobalException(String message) {
        super(message);
        this.code = 500;
    }

    public GlobalException(int code, String message, Throwable cause) {
        super(message, cause);
        this.code = code;
    }


    public GlobalException(String message, Throwable cause) {
        super(message, cause);
        this.code = 500;
    }

    @Override
    public int code() {
        return code;
    }

    @Override
    public String msg() {
        return getMessage();
    }
}

```

# com.medusa.gruul.global.model.filter.FilterChain

**文件路径**: `model\filter\FilterChain.java`

## 代码文档
///
@author 张治保
date 2022/8/15
 
///

///
当前节点的处理
     
///

///
下一个节点
     
///


## 文件结构
```
项目根目录
└── model\filter
    └── FilterChain.java
```

## 完整代码
```java
package com.medusa.gruul.global.model.filter;

import lombok.RequiredArgsConstructor;
import lombok.Setter;

/**
 * @author 张治保
 * date 2022/8/15
 */
@RequiredArgsConstructor
public class FilterChain<Context> implements IFilterChain<Context> {

    /**
     * 当前节点的处理
     */
    private final IFilter<Context> filter;

    /**
     * 下一个节点
     */
    @Setter
    private IFilterChain<Context> next;

    @Override
    public void handle(FilterContext<Context> context) {
        if (filter == null) {
            return;
        }
        filter.doFilter(context, this);
    }

    @Override
    public void chain(FilterContext<Context> context) {
        if (next == null || context.isBreakIt()) {
            return;
        }
        next.handle(context);
    }
}

```

# com.medusa.gruul.global.model.filter.FilterChainComparable

**文件路径**: `model\filter\FilterChainComparable.java`

## 代码文档
///
@author 张治保
date 2022/8/16
 
///


## 文件结构
```
项目根目录
└── model\filter
    └── FilterChainComparable.java
```

## 完整代码
```java
package com.medusa.gruul.global.model.filter;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * @author 张治保
 * date 2022/8/16
 */
@EqualsAndHashCode
@RequiredArgsConstructor
public class FilterChainComparable<Context> implements Comparable<FilterChainComparable<Context>> {
    private final int order;
    @Getter
    private final FilterChain<Context> filterChain;

    @Override
    public int compareTo(FilterChainComparable<Context> next) {
        return order > next.order ? -1 : 1;
    }
}

```

# com.medusa.gruul.global.model.filter.FilterContext

**文件路径**: `model\filter\FilterContext.java`

## 代码文档
///
执行链上下文

@author 张治保
date 2022/8/15
 
///

///
shopId
     
///

///
是否跳出当前执行链
     
///

///
自定义数据载体
     
///


## 文件结构
```
项目根目录
└── model\filter
    └── FilterContext.java
```

## 完整代码
```java
package com.medusa.gruul.global.model.filter;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;

/**
 * 执行链上下文
 *
 * @author 张治保
 * date 2022/8/15
 */
@Getter
@Setter
@ToString
public class FilterContext<Context> implements Serializable {

    /**
     * shopId
     */
    private Long shopId;

    /**
     * 是否跳出当前执行链
     */
    private boolean breakIt;

    /**
     * 自定义数据载体
     */
    private Context data;

}

```

# com.medusa.gruul.global.model.filter.FilterPipeline

**文件路径**: `model\filter\FilterPipeline.java`

## 代码文档
///
@author 张治保
date 2022/8/15
 
///


## 文件结构
```
项目根目录
└── model\filter
    └── FilterPipeline.java
```

## 完整代码
```java
package com.medusa.gruul.global.model.filter;


import java.util.TreeSet;

/**
 * @author 张治保
 * date 2022/8/15
 */
public class FilterPipeline<Context> implements IFilterPipeline<Context> {

    FilterPipeline(FilterContext<Context> filterContext) {
        this.filterContext = filterContext;
    }

    private final FilterContext<Context> filterContext;
    private final TreeSet<FilterChainComparable<Context>> sortedSet = new TreeSet<>();

    @Override
    public IFilterPipeline<Context> addFilter(IFilter<Context> filter) {
        return this.addFilterChain(new FilterChain<>(filter));
    }

    @Override
    public IFilterPipeline<Context> addFilter(int order, IFilter<Context> filter) {
        return this.addFilterChain(order, new FilterChain<>(filter));
    }

    @Override
    public IFilterPipeline<Context> addFilter(IAutomaticFilter<Context> filter) {
        return this.addFilter((IFilter<Context>) filter);
    }

    @Override
    public IFilterPipeline<Context> addFilter(int order, IAutomaticFilter<Context> filter) {
        return this.addFilter(order, (IFilter<Context>) filter);
    }

    @Override
    public IFilterPipeline<Context> addFilterChain(FilterChain<Context> chain) {
        return this.addFilterChain(0, chain);
    }

    @Override
    public IFilterPipeline<Context> addFilterChain(int order, FilterChain<Context> chain) {
        sortedSet.add(
                new FilterChainComparable<>(order, chain)
        );
        FilterChain<Context> preChain = null;
        for (FilterChainComparable<Context> current : sortedSet) {
            FilterChain<Context> currentFilterChain = current.getFilterChain();
            if (preChain == null) {
                preChain = currentFilterChain;
                continue;
            }
            preChain.setNext(currentFilterChain);
            preChain = currentFilterChain;
        }
        return this;
    }

    @Override
    public void flush() {
        sortedSet.first().getFilterChain().handle(filterContext);
    }
}

```

# com.medusa.gruul.global.model.filter.IAutomaticFilter

**文件路径**: `model\filter\IAutomaticFilter.java`

## 代码文档
///
自动 chain的filter

@author 张治保
date 2022/8/22
 
///

///
处理上下文逻辑抽象

@param context 上下文
     
///

///
处理上下文逻辑抽象

@param context 上下文
@param chain   处理链
     
///


## 文件结构
```
项目根目录
└── model\filter
    └── IAutomaticFilter.java
```

## 完整代码
```java
package com.medusa.gruul.global.model.filter;

/**
 * 自动 chain的filter
 *
 * @author 张治保
 * date 2022/8/22
 */
@FunctionalInterface
public interface IAutomaticFilter<Context> extends IFilter<Context> {
    /**
     * 处理上下文逻辑抽象
     *
     * @param context 上下文
     */
    void doFilter(FilterContext<Context> context);


    /**
     * 处理上下文逻辑抽象
     *
     * @param context 上下文
     * @param chain   处理链
     */
    @Override
    default void doFilter(FilterContext<Context> context, IFilterChain<Context> chain) {
        this.doFilter(context);
        chain.chain(context);
    }
}

```

# com.medusa.gruul.global.model.filter.IFilter

**文件路径**: `model\filter\IFilter.java`

## 代码文档
///
处理节点 处理流程  责任链模式抽象

@param <Context> 上下文
@author 张治保
date 2022/8/15
 
///

///
处理上下文逻辑抽象

@param context 上下文
@param chain   处理链
     
///


## 文件结构
```
项目根目录
└── model\filter
    └── IFilter.java
```

## 完整代码
```java
package com.medusa.gruul.global.model.filter;


/**
 * 处理节点 处理流程  责任链模式抽象
 *
 * @param <Context> 上下文
 * @author 张治保
 * date 2022/8/15
 */
@FunctionalInterface
public interface IFilter<Context> {

    /**
     * 处理上下文逻辑抽象
     *
     * @param context 上下文
     * @param chain   处理链
     */
    void doFilter(FilterContext<Context> context, IFilterChain<Context> chain);
}

```

# com.medusa.gruul.global.model.filter.IFilterChain

**文件路径**: `model\filter\IFilterChain.java`

## 代码文档
///
处理节点抽象 责任链模式

@param <Context> 上下文
@author 张治保
date 2022/8/15
 
///

///
处理流程

@param context 上下文
     
///

///
执行任务交给下一个链

@param context 上下文
     
///


## 文件结构
```
项目根目录
└── model\filter
    └── IFilterChain.java
```

## 完整代码
```java
package com.medusa.gruul.global.model.filter;


/**
 * 处理节点抽象 责任链模式
 *
 * @param <Context> 上下文
 * @author 张治保
 * date 2022/8/15
 */
public interface IFilterChain<Context> {


    /**
     * 处理流程
     *
     * @param context 上下文
     */
    void handle(FilterContext<Context> context);


    /**
     * 执行任务交给下一个链
     *
     * @param context 上下文
     */
    void chain(FilterContext<Context> context);

}

```

# com.medusa.gruul.global.model.filter.IFilterPipeline

**文件路径**: `model\filter\IFilterPipeline.java`

## 代码文档
///
责任链

@author 张治保
date 2022/8/15
 
///

///
构造pipeline

@param dataOption 可选上下文
@param <Context>  上下文类型
@return FilterPipeline
     
///

///
增加filter处理器

@param filter 处理器 任意位置
@return self
     
///

///
增加filter处理器

@param order  顺序 从小到大 排序 挨个执行
@param filter 处理器
@return self
     
///

///
增加filter处理器

@param filter 处理器 任意位置
@return self
     
///

///
增加filter处理器

@param order  顺序 从小到大 排序 挨个执行
@param filter 处理器
@return self
     
///

///
增加filter处理器

@param filter 处理器 任意位置
@return self
     
///

///
增加filter处理器

@param order  顺序 从小到大 排序 挨个执行
@param filter 处理器
@return self
     
///

///
开始执行
     
///


## 文件结构
```
项目根目录
└── model\filter
    └── IFilterPipeline.java
```

## 完整代码
```java
package com.medusa.gruul.global.model.filter;

import io.vavr.control.Option;

/**
 * 责任链
 *
 * @author 张治保
 * date 2022/8/15
 */
public interface IFilterPipeline<Context> {

    /**
     * 构造pipeline
     *
     * @param dataOption 可选上下文
     * @param <Context>  上下文类型
     * @return FilterPipeline
     */
    static <Context> IFilterPipeline<Context> build(Option<Context> dataOption) {
        FilterContext<Context> filterContext = new FilterContext<>();
        filterContext.setData(dataOption.getOrNull());
        filterContext.setBreakIt(Boolean.FALSE);
        return new FilterPipeline<>(filterContext);
    }

    /**
     * 增加filter处理器
     *
     * @param filter 处理器 任意位置
     * @return self
     */
    IFilterPipeline<Context> addFilter(IFilter<Context> filter);


    /**
     * 增加filter处理器
     *
     * @param order  顺序 从小到大 排序 挨个执行
     * @param filter 处理器
     * @return self
     */
    IFilterPipeline<Context> addFilter(int order, IFilter<Context> filter);

    /**
     * 增加filter处理器
     *
     * @param filter 处理器 任意位置
     * @return self
     */
    IFilterPipeline<Context> addFilter(IAutomaticFilter<Context> filter);


    /**
     * 增加filter处理器
     *
     * @param order  顺序 从小到大 排序 挨个执行
     * @param filter 处理器
     * @return self
     */
    IFilterPipeline<Context> addFilter(int order, IAutomaticFilter<Context> filter);

    /**
     * 增加filter处理器
     *
     * @param filter 处理器 任意位置
     * @return self
     */
    IFilterPipeline<Context> addFilterChain(FilterChain<Context> filter);


    /**
     * 增加filter处理器
     *
     * @param order  顺序 从小到大 排序 挨个执行
     * @param filter 处理器
     * @return self
     */
    IFilterPipeline<Context> addFilterChain(int order, FilterChain<Context> filter);

    /**
     * 开始执行
     */
    void flush();


}

```

# com.medusa.gruul.global.model.helper.Address

**文件路径**: `model\helper\Address.java`

## 代码文档
///
完整地址抽象模型

@author 张治保
@since 2024/10/26
 
///

///
获取完整地址

@param area    省市区
@param address 详细地址
@return 完整地址
     
///

///
获取省市区

@return 省市区
     
///

///
获取详细地址

@return 详细地址
     
///

///
获取省
     
///

///
获取市
     
///

///
获取区
     
///

///
获取完整地址

@return 完整地址
     
///


## 文件结构
```
项目根目录
└── model\helper
    └── Address.java
```

## 完整代码
```java
package com.medusa.gruul.global.model.helper;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;

import java.io.Serializable;
import java.util.List;

/**
 * 完整地址抽象模型
 *
 * @author 张治保
 * @since 2024/10/26
 */
public interface Address extends Serializable {

    /**
     * 获取完整地址
     *
     * @param area    省市区
     * @param address 详细地址
     * @return 完整地址
     */
    static String full(List<String> area, String address) {
        if (CollUtil.isEmpty(area)) {
            return address == null ? StrUtil.EMPTY : address;
        }
        String join = String.join(StrUtil.EMPTY, area);
        if (address == null) {
            return join;
        }
        return join + StrUtil.SPACE + address;
    }

    /**
     * 获取省市区
     *
     * @return 省市区
     */
    List<String> getArea();

    /**
     * 获取详细地址
     *
     * @return 详细地址
     */
    String getAddress();

    /**
     * 获取省
     */
    default String province() {
        List<String> area = getArea();
        return CollUtil.isEmpty(area) ? StrUtil.EMPTY : area.get(0);
    }

    /**
     * 获取市
     */
    default String city() {
        List<String> area = getArea();
        if (CollUtil.isEmpty(area)) {
            return StrUtil.EMPTY;
        }
        //兼容直辖市 如[上海市，静安区]
        if (area.size() <= 2) {
            return area.get(0);
        }
        return area.get(1);
    }

    /**
     * 获取区
     */
    default String county() {
        List<String> area = getArea();
        if (CollUtil.isEmpty(area)) {
            return StrUtil.EMPTY;
        }
        return area.get(area.size() - 1);
    }

    /**
     * 获取完整地址
     *
     * @return 完整地址
     */
    default String fullAddress() {
        return full(getArea(), getAddress());
    }


}

```

# com.medusa.gruul.global.model.helper.Classes

**文件路径**: `model\helper\Classes.java`

## 代码文档
///
@author 张治保
@since 2023/11/14
 
///

///
判断类是否存在

@param className 全类名
@return 是否存在
     
///

///
加载类

@param className 全类名
@param <T>       类型
@return 类
     
///


## 文件结构
```
项目根目录
└── model\helper
    └── Classes.java
```

## 完整代码
```java
package com.medusa.gruul.global.model.helper;

/**
 * @author 张治保
 * @since 2023/11/14
 */
public interface Classes {

    /**
     * 判断类是否存在
     *
     * @param className 全类名
     * @return 是否存在
     */
    static boolean exists(String className) {
        return load(className) != null;
    }

    /**
     * 加载类
     *
     * @param className 全类名
     * @param <T>       类型
     * @return 类
     */
    @SuppressWarnings("unchecked")
    static <T> Class<T> load(String className) {
        try {
            return (Class<T>) Class.forName(className);
        } catch (ClassNotFoundException e) {
            return null;
        }
    }
}

```

# com.medusa.gruul.global.model.helper.CompletableTask

**文件路径**: `model\helper\CompletableTask.java`

## 代码文档
///
@author 张治保
date 2022/6/13
 
///

///
获取异步执行结果 或 抛出异常

@param completableFuture 异步执行任务
@param <T>               执行结果泛型
     
///

///
渲染所有的异步任务至一个任务

@param executor 异步任务执行器
@param tasks    任务列表
@return 合并后的任务
     
///

///
all of

@param tasks 任务列表
@return CompletableFuture
     
///


## 文件结构
```
项目根目录
└── model\helper
    └── CompletableTask.java
```

## 完整代码
```java
package com.medusa.gruul.global.model.helper;


import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executor;

/**
 * @author 张治保
 * date 2022/6/13
 */
@Slf4j
public class CompletableTask {

    private CompletableTask() {
    }

    /**
     * 获取异步执行结果 或 抛出异常
     *
     * @param completableFuture 异步执行任务
     * @param <T>               执行结果泛型
     */
    public static <T> T getOrThrowException(CompletableFuture<T> completableFuture) {
        try {
            return completableFuture.get();
        } catch (ExecutionException e) {
            Throwable cause = e.getCause();
            if (cause instanceof RuntimeException) {
                throw (RuntimeException) cause;
            }
            throw new RuntimeException("asynchronous execution error", e);
        } catch (InterruptedException e) {
            log.error("interrupted exception", e);
            throw new RuntimeException("asynchronous execution error InterruptedException", e);
        }
    }

    /**
     * 渲染所有的异步任务至一个任务
     *
     * @param executor 异步任务执行器
     * @param tasks    任务列表
     * @return 合并后的任务
     */

    public static CompletableFuture<Void> allOf(Executor executor, Runnable... tasks) {
        int length = tasks.length;
        CompletableFuture<?>[] futures = new CompletableFuture[length];
        for (int i = 0; i < length; i++) {
            futures[i] = CompletableFuture.runAsync(tasks[i], executor)
                    .whenComplete(
                            (value, throwable) -> {
                                if (throwable == null) {
                                    return;
                                }
                                if (log.isErrorEnabled()) {
                                    log.error("CompletableFuture Exception", throwable);
                                }
                            }
                    );
        }
        return CompletableFuture.allOf(futures);
    }

    /**
     * all of
     *
     * @param tasks 任务列表
     * @return CompletableFuture
     */
    public static CompletableFuture<Void> allOf(CompletableFuture<?>... tasks) {
        for (CompletableFuture<?> task : tasks) {
            task.whenComplete(
                    (value, throwable) -> {
                        if (throwable == null) {
                            return;
                        }
                        if (log.isErrorEnabled()) {
                            log.error("CompletableFuture Exception", throwable);
                        }
                    }
            );
        }
        return CompletableFuture.allOf(tasks);
    }


}

```

# com.medusa.gruul.global.model.helper.IRangeMap

**文件路径**: `model\helper\IRangeMap.java`

## 代码文档
///
区间MAP

@author 张治保
date 2023/2/10
 
///

///
把集合数据put 到区间map里

@param items     集合数据
@param keyFunc   获取集合里每个item的 key 函数
@param valueFunc 获取集合里每个item的 value 函数
@param <T>       集合里每个item的类型
     
///

///
获取可能为空的值

@param key key
@return 可能为null的值
     
///


## 文件结构
```
项目根目录
└── model\helper
    └── IRangeMap.java
```

## 完整代码
```java
package com.medusa.gruul.global.model.helper;

import io.vavr.control.Option;

import java.util.Collection;
import java.util.Map;
import java.util.function.Function;

/**
 * 区间MAP
 *
 * @author 张治保
 * date 2023/2/10
 */
public interface IRangeMap<K, V> extends Map<K, V> {

    /**
     * 把集合数据put 到区间map里
     *
     * @param items     集合数据
     * @param keyFunc   获取集合里每个item的 key 函数
     * @param valueFunc 获取集合里每个item的 value 函数
     * @param <T>       集合里每个item的类型
     */
    <T> void putAll(Collection<T> items, Function<T, K> keyFunc, Function<T, V> valueFunc);


    /**
     * 获取可能为空的值
     *
     * @param key key
     * @return 可能为null的值
     */
    Option<V> optGet(K key);
}

```

# com.medusa.gruul.global.model.helper.RangeMap

**文件路径**: `model\helper\RangeMap.java`

## 代码文档
///
区间MAP 实现类

@author 张治保
date 2023/2/10
 
///


## 文件结构
```
项目根目录
└── model\helper
    └── RangeMap.java
```

## 完整代码
```java
package com.medusa.gruul.global.model.helper;

import io.vavr.control.Option;

import java.math.BigDecimal;
import java.util.*;
import java.util.function.Function;

/**
 * 区间MAP 实现类
 *
 * @author 张治保
 * date 2023/2/10
 */
public class RangeMap<K extends Number, V> implements IRangeMap<K, V> {

    private final TreeMap<K, V> delegate;

    public RangeMap() {
        this.delegate = new TreeMap<>(Comparator.comparing(v -> new BigDecimal(v.toString())));
    }


    @Override
    public int size() {
        return delegate.size();
    }

    @Override
    public boolean isEmpty() {
        return delegate.isEmpty();
    }

    @Override
    @SuppressWarnings("unchecked")
    public boolean containsKey(Object key) {
        return delegate.floorKey((K) key) != null;
    }

    @Override
    public boolean containsValue(Object value) {
        return delegate.containsValue(value);
    }

    @Override
    @SuppressWarnings("unchecked")
    public V get(Object key) {
        K floorKey = delegate.floorKey((K) key);
        return floorKey == null ? null : delegate.get(floorKey);
    }

    @Override
    public Option<V> optGet(K key) {
        return Option.of(get(key));
    }

    @Override
    public V put(K key, V value) {
        return delegate.put(key, value);
    }

    @Override
    public V remove(Object key) {
        return delegate.remove(key);
    }

    @Override
    public <T> void putAll(Collection<T> items, Function<T, K> keyFunc, Function<T, V> valueFunc) {
        items.forEach(item -> delegate.put(keyFunc.apply(item), valueFunc.apply(item)));
    }


    @Override
    public void putAll(Map<? extends K, ? extends V> map) {
        delegate.putAll(map);
    }

    @Override
    public void clear() {
        delegate.clear();
    }

    @Override
    public Set<K> keySet() {
        return delegate.keySet();
    }

    @Override
    public Collection<V> values() {
        return delegate.values();
    }

    @Override
    public Set<Entry<K, V>> entrySet() {
        return delegate.entrySet();
    }


}

```

# com.medusa.gruul.global.model.helper.SecureHelper

**文件路径**: `model\helper\SecureHelper.java`

## 代码文档
///
@author 张治保
@since 2023/10/31
 
///

///
aes 加密

@param base64Key base64Key
@return AES
     
///

///
生成 aes key

@return base64Key
     
///

///
生成公钥，仅用于非对称加密<br>
<a href="https://docs.oracle.com/javase/7/docs/technotes/guides/security/StandardNames.html#KeyFactory">
算法见
</a>

@param algorithm 算法
@param base64Key base64公钥密钥
@return 公钥 {@link PublicKey}
     
///

///
生成私钥，仅用于非对称加密<br>
算法见：
<a href="https://docs.oracle.com/javase/7/docs/technotes/guides/security/StandardNames.html#KeyFactory">
算法
</a>

@param algorithm 算法
@param base64Key base64私钥密钥
@return 私钥 {@link PrivateKey}
     
///

///
生成密钥对，仅用于非对称加密<br>

@param algorithm        算法
@param base64PublicKey  base64公钥密钥
@param base64PrivateKey base64私钥密钥
@return 密钥对 {@link KeyPair}
     
///

///
生成密钥，仅用于对称加密<br>

@param algorithm 算法
@param base64Key base64密钥
@return 密钥 {@link SecretKey}
     
///

///
生成公钥，仅用于非对称加密<br>

@param base64Key base64公钥密钥
@return 公钥 {@link PublicKey}
     
///

///
生成私钥，仅用于非对称加密<br>

@param base64Key base64私钥密钥
@return 私钥 {@link PrivateKey}
     
///

///
base64 密钥 转 SecretKey，仅用于对称加密<br>

@param base64Key base64密钥
@return 密钥 {@link SecretKey}
     
///


## 文件结构
```
项目根目录
└── model\helper
    └── SecureHelper.java
```

## 完整代码
```java
package com.medusa.gruul.global.model.helper;

import cn.hutool.crypto.SecureUtil;
import cn.hutool.crypto.symmetric.AES;
import cn.hutool.crypto.symmetric.SymmetricAlgorithm;

import javax.crypto.SecretKey;
import java.security.KeyPair;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.Base64;

/**
 * @author 张治保
 * @since 2023/10/31
 */
public interface SecureHelper {

    /**
     * aes 加密
     *
     * @param base64Key base64Key
     * @return AES
     */
    static AES aesFromKey(String base64Key) {
        byte[] key = Base64.getDecoder().decode(base64Key);
        return SecureUtil.aes(key);
    }

    /**
     * 生成 aes key
     *
     * @return base64Key
     */
    static String aesBase64Key() {
        SecretKey secretKey = SecureUtil.generateKey(SymmetricAlgorithm.AES.getValue(), 128);
        //生成 aes
        return Base64.getEncoder().encodeToString(secretKey.getEncoded());
    }

    /**
     * 生成公钥，仅用于非对称加密<br>
     * <a href="https://docs.oracle.com/javase/7/docs/technotes/guides/security/StandardNames.html#KeyFactory">
     * 算法见
     * </a>
     *
     * @param algorithm 算法
     * @param base64Key base64公钥密钥
     * @return 公钥 {@link PublicKey}
     */
    static PublicKey publicKey(String algorithm, String base64Key) {
        byte[] key = Base64.getDecoder().decode(base64Key);
        return SecureUtil.generatePublicKey(algorithm, key);
    }

    /**
     * 生成私钥，仅用于非对称加密<br>
     * 算法见：
     * <a href="https://docs.oracle.com/javase/7/docs/technotes/guides/security/StandardNames.html#KeyFactory">
     * 算法
     * </a>
     *
     * @param algorithm 算法
     * @param base64Key base64私钥密钥
     * @return 私钥 {@link PrivateKey}
     */
    static PrivateKey privateKey(String algorithm, String base64Key) {
        byte[] key = Base64.getDecoder().decode(base64Key);
        return SecureUtil.generatePrivateKey(algorithm, key);
    }


    /**
     * 生成密钥对，仅用于非对称加密<br>
     *
     * @param algorithm        算法
     * @param base64PublicKey  base64公钥密钥
     * @param base64PrivateKey base64私钥密钥
     * @return 密钥对 {@link KeyPair}
     */
    static KeyPair keyPair(String algorithm, String base64PublicKey, String base64PrivateKey) {
        return new KeyPair(publicKey(algorithm, base64PublicKey), privateKey(algorithm, base64PrivateKey));
    }

    /**
     * 生成密钥，仅用于对称加密<br>
     *
     * @param algorithm 算法
     * @param base64Key base64密钥
     * @return 密钥 {@link SecretKey}
     */
    static SecretKey secretKey(String algorithm, String base64Key) {
        byte[] key = Base64.getDecoder().decode(base64Key);
        return SecureUtil.generateKey(algorithm, key);
    }

    /**
     * 生成公钥，仅用于非对称加密<br>
     *
     * @param base64Key base64公钥密钥
     * @return 公钥 {@link PublicKey}
     */
    static PublicKey esPublicKey(String base64Key) {
        return publicKey("EC", base64Key);
    }

    /**
     * 生成私钥，仅用于非对称加密<br>
     *
     * @param base64Key base64私钥密钥
     * @return 私钥 {@link PrivateKey}
     */
    static PrivateKey esPrivateKey(String base64Key) {
        return privateKey("EC", base64Key);
    }

    /**
     * base64 密钥 转 SecretKey，仅用于对称加密<br>
     *
     * @param base64Key base64密钥
     * @return 密钥 {@link SecretKey}
     */
    static SecretKey aesKey(String base64Key) {
        return secretKey("AES", base64Key);
    }

}

```

# com.medusa.gruul.global.model.helper.ShutdownHook

**文件路径**: `model\helper\ShutdownHook.java`

## 代码文档
///
@author 张治保
@since 2023/12/7
 
///

///
用于存储shutdown 回调钩子集合
     
///

///
addShutdownHook
     
///

///
添加钩子 谨慎使用 确保任务可以快速结束 避免阻塞关闭操作

@param hook 钩子
     
///

///
移除钩子

@param hook 钩子
     
///


## 文件结构
```
项目根目录
└── model\helper
    └── ShutdownHook.java
```

## 完整代码
```java
package com.medusa.gruul.global.model.helper;

import java.util.Set;

/**
 * @author 张治保
 * @since 2023/12/7
 */
public class ShutdownHook {

    /**
     * 用于存储shutdown 回调钩子集合
     */
    private static final Set<Runnable> HOOKS = new java.util.concurrent.CopyOnWriteArraySet<>();

    /**
     * addShutdownHook
     */
    static {
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            for (Runnable hook : HOOKS) {
                hook.run();
            }
        }));
    }

    /**
     * 添加钩子 谨慎使用 确保任务可以快速结束 避免阻塞关闭操作
     *
     * @param hook 钩子
     */
    public static void add(Runnable hook) {
        HOOKS.add(hook);
    }

    /**
     * 移除钩子
     *
     * @param hook 钩子
     */
    static void remove(Runnable hook) {
        HOOKS.remove(hook);
    }

}

```

# com.medusa.gruul.global.model.o.BaseDTO

**文件路径**: `model\o\BaseDTO.java`

## 代码文档
///
基础的数据传输对象

@author 张治保
date 2022/5/25
 
///

///
检查校验参数
     
///

///
模糊查询字符%拼接

@param value 原始字符
@return 拼接后的字符
     
///


## 文件结构
```
项目根目录
└── model\o
    └── BaseDTO.java
```

## 完整代码
```java
package com.medusa.gruul.global.model.o;

import java.io.Serializable;

/**
 * 基础的数据传输对象
 *
 * @author 张治保
 * date 2022/5/25
 */
public interface BaseDTO extends Serializable {

    /**
     * 检查校验参数
     */
    default void validParam() {
    }


    /**
     * 模糊查询字符%拼接
     *
     * @param value 原始字符
     * @return 拼接后的字符
     */
    default String like(String value) {
        return "'%" + value + "%'";
    }

}

```

# com.medusa.gruul.global.model.o.ExceptionData

**文件路径**: `model\o\ExceptionData.java`

## 代码文档
///
@author 张治保
date 2023/7/20
 
///

///
引发错误的 key 如 店铺 id 或店铺 id 与商品 id 组合行程的 key
	 
///

///
引发错误的状态数据  比如店铺状态异常 可以传递店铺状态
	 
///


## 文件结构
```
项目根目录
└── model\o
    └── ExceptionData.java
```

## 完整代码
```java
package com.medusa.gruul.global.model.o;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;

/**
 * @author 张治保
 * date 2023/7/20
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ExceptionData implements Serializable {

	/**
	 * 引发错误的 key 如 店铺 id 或店铺 id 与商品 id 组合行程的 key
	 */
	private Serializable key;

	/**
	 * 引发错误的状态数据  比如店铺状态异常 可以传递店铺状态
	 */
	private Serializable data;

	public static ExceptionData of(Serializable key) {
		return ExceptionData.of(key, null);
	}

	public static ExceptionData of(Serializable key, Serializable data) {
		return new ExceptionData(key, data);
	}

}

```

# com.medusa.gruul.global.model.o.Final

**文件路径**: `model\o\Final.java`

## 代码文档
///
用于在 函数式编程里使用 局部变量 需要注意的是 不是线程安全的
(函数编程里要求 final 变量 如果使用AtomicReference有点太重了)

@author 张治保
date 2023/4/13
 
///

///
数据
	 
///

///
获取数据

@return T
	 
///

///
设置数据

@param data 数据
	 
///


## 文件结构
```
项目根目录
└── model\o
    └── Final.java
```

## 完整代码
```java
package com.medusa.gruul.global.model.o;

import java.io.Serializable;

/**
 * 用于在 函数式编程里使用 局部变量 需要注意的是 不是线程安全的
 * (函数编程里要求 final 变量 如果使用AtomicReference有点太重了)
 *
 * @author 张治保
 * date 2023/4/13
 */
public final class Final<T> implements Serializable {

	/**
	 * 数据
	 */
	private T data;

	public Final() {
		this(null);
	}

	public Final(T data) {
		this.data = data;
	}

	/**
	 * 获取数据
	 *
	 * @return T
	 */
	public T get() {
		return data;
	}

	/**
	 * 设置数据
	 *
	 * @param data 数据
	 */
	public void set(T data) {
		this.data = data;
	}

}

```

# com.medusa.gruul.global.model.o.KeyValue

**文件路径**: `model\o\KeyValue.java`

## 代码文档
///
@author 张治保
date 2023/7/28
 
///

///
key
     
///

///
value
     
///

///
创建一个 key value 对象

@param key   key
@param value value
@param <K>   key
@param <V>   value
@return KeyValue
     
///

///
转成一个map对象

@param keyValues keyValues
@param <K>       key type
@param <V>       value type
@return Map
     
///

///
map 转成 key value 对象集合

@param keyValueMap map
@param <K>         key type
@param <V>         value type
@return Set of KeyValue
     
///


## 文件结构
```
项目根目录
└── model\o
    └── KeyValue.java
```

## 完整代码
```java
package com.medusa.gruul.global.model.o;

import lombok.*;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.*;

/**
 * @author 张治保
 * date 2023/7/28
 */
@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Accessors(chain = true)
@EqualsAndHashCode(of = "key")
public class KeyValue<K, V> implements Serializable {

    /**
     * key
     */
    private K key;

    /**
     * value
     */
    private V value;


    /**
     * 创建一个 key value 对象
     *
     * @param key   key
     * @param value value
     * @param <K>   key
     * @param <V>   value
     * @return KeyValue
     */
    public static <K, V> KeyValue<K, V> of(K key, V value) {
        return new KeyValue<>(key, value);
    }


    /**
     * 转成一个map对象
     *
     * @param keyValues keyValues
     * @param <K>       key type
     * @param <V>       value type
     * @return Map
     */
    public static <K, V> Map<K, V> toMap(Set<KeyValue<K, V>> keyValues) {
        if (keyValues == null) {
            return Collections.emptyMap();
        }
        Map<K, V> result = new HashMap<>(keyValues.size());
        for (KeyValue<K, V> keyValue : keyValues) {
            result.put(keyValue.getKey(), keyValue.getValue());
        }
        return result;
    }

    /**
     * map 转成 key value 对象集合
     *
     * @param keyValueMap map
     * @param <K>         key type
     * @param <V>         value type
     * @return Set of KeyValue
     */
    public static <K, V> Set<KeyValue<K, V>> of(Map<K, V> keyValueMap) {
        if (keyValueMap == null) {
            return Collections.emptySet();
        }
        Set<KeyValue<K, V>> keyValues = new HashSet<>(keyValueMap.size());
        keyValueMap.forEach(
                (key, value) -> keyValues.add(KeyValue.of(key, value))
        );
        return keyValues;
    }


}

```

# com.medusa.gruul.global.model.o.MessageKey

**文件路径**: `model\o\MessageKey.java`

## 代码文档
///
@author 张治保
date 2023/6/21
 
///

///
交换机
	 
///

///
路由key
	 
///


## 文件结构
```
项目根目录
└── model\o
    └── MessageKey.java
```

## 完整代码
```java
package com.medusa.gruul.global.model.o;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * @author 张治保
 * date 2023/6/21
 */
@Getter
@Setter
@Accessors(chain = true)
@ToString
@EqualsAndHashCode
public class MessageKey implements Serializable {

	/**
	 * 交换机
	 */
	private String exchange;

	/**
	 * 路由key
	 */
	private String routingKey;
}

```

# com.medusa.gruul.global.model.o.RangeDate

**文件路径**: `model\o\RangeDate.java`

## 代码文档
///
@author 张治保
@since 2023/11/10
 
///

///
开始时间
     
///

///
结束时间
     
///

///
开始时间结束时间转换成时间段

@return 时间段
     
///


## 文件结构
```
项目根目录
└── model\o
    └── RangeDate.java
```

## 完整代码
```java
package com.medusa.gruul.global.model.o;

import lombok.NonNull;
import lombok.ToString;
import lombok.experimental.Accessors;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

/**
 * @author 张治保
 * @since 2023/11/10
 */
@ToString
@Accessors(chain = true)
public class RangeDate extends RangeTemporal<RangeDate, LocalDate> {


    /**
     * 开始时间
     */
    private LocalDateTime startTime;

    /**
     * 结束时间
     */
    private LocalDateTime endTime;

    public LocalDateTime getStartTime() {
        if (startTime == null && start != null) {
            startTime = _startTime(start);
        }
        return startTime;
    }

    public LocalDateTime getEndTime() {
        if (endTime == null && end != null) {
            endTime = _endTime(end);
        }
        return endTime;
    }

    private LocalDateTime _startTime(LocalDate start) {
        return start.atStartOfDay();
    }

    private LocalDateTime _endTime(LocalDate end) {
        return end.atTime(LocalTime.MAX);
    }

    /**
     * 开始时间结束时间转换成时间段
     *
     * @return 时间段
     */
    @Override
    public Duration toDuration() {
        LocalDateTime curStartTime;
        LocalDateTime curEndTime;
        if ((curStartTime = getStartTime()) == null || (curEndTime = getEndTime()) == null) {
            return null;
        }
        return Duration.between(curStartTime, curEndTime);
    }

    @Override
    protected boolean isAfter(@NonNull LocalDate start, @NonNull LocalDate end) {
        return Duration.between(_startTime(start), _endTime(end)).isNegative();
    }
}

```

# com.medusa.gruul.global.model.o.RangeDateTime

**文件路径**: `model\o\RangeDateTime.java`

## 代码文档
///
@author 张治保
@since 2024/5/30
 
///


## 文件结构
```
项目根目录
└── model\o
    └── RangeDateTime.java
```

## 完整代码
```java
package com.medusa.gruul.global.model.o;

import lombok.Getter;
import lombok.ToString;
import lombok.experimental.Accessors;

import java.time.LocalDateTime;

/**
 * @author 张治保
 * @since 2024/5/30
 */
@Getter
@Accessors(chain = true)
@ToString
public class RangeDateTime extends RangeTemporal<RangeDateTime, LocalDateTime> {

}

```

# com.medusa.gruul.global.model.o.RangeTemporal

**文件路径**: `model\o\RangeTemporal.java`

## 代码文档
///
时间范围对象顶级抽象类

@author 张治保
@since 2024/5/30
 
///

///
开始时间
     
///

///
结束时间
     
///

///
开始时间结束时间转换成时间段

@return 时间段
     
///

///
验证时间范围 设置是否正确

@param start 开始时间
@param end   结束时间
     
///

///
判断开始时间是否在结束时间之后

@param start 开始时间
@param end   结束时间
@return 是否在结束时间之后
     
///

///
时间范围异常
     
///


## 文件结构
```
项目根目录
└── model\o
    └── RangeTemporal.java
```

## 完整代码
```java
package com.medusa.gruul.global.model.o;

import jakarta.validation.constraints.NotNull;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NonNull;
import lombok.ToString;

import java.io.Serializable;
import java.time.Duration;
import java.time.temporal.Temporal;

/**
 * 时间范围对象顶级抽象类
 *
 * @author 张治保
 * @since 2024/5/30
 */
@Getter
@ToString
@EqualsAndHashCode
public abstract class RangeTemporal<S extends RangeTemporal<S, T>, T extends Temporal> implements Serializable {

    /**
     * 开始时间
     */
    @NotNull
    protected T start;

    /**
     * 结束时间
     */
    @NotNull
    protected T end;


    @SuppressWarnings("unchecked")
    public final S setStart(T start) {
        validateIt(start, end);
        this.start = start;
        return (S) this;
    }

    @SuppressWarnings("unchecked")
    public final S setEnd(T end) {
        validateIt(start, end);
        this.end = end;
        return (S) this;
    }

    /**
     * 开始时间结束时间转换成时间段
     *
     * @return 时间段
     */
    public Duration toDuration() {
        if (start == null || end == null) {
            return null;
        }
        return Duration.between(start, end);

    }

    /**
     * 验证时间范围 设置是否正确
     *
     * @param start 开始时间
     * @param end   结束时间
     */
    private void validateIt(T start, T end) {
        if (start == null || end == null) {
            return;
        }
        if (isAfter(start, end)) {
            throw new TimeRangeException();
        }
    }

    /**
     * 判断开始时间是否在结束时间之后
     *
     * @param start 开始时间
     * @param end   结束时间
     * @return 是否在结束时间之后
     */
    protected boolean isAfter(@NonNull T start, @NonNull T end) {
        return Duration.between(start, end).isNegative();
    }

    /**
     * 时间范围异常
     */
    public static final class TimeRangeException extends RuntimeException {
        public TimeRangeException() {
            super("start time cannot be greater than end time");
        }
    }
}

```

# com.medusa.gruul.global.model.o.RangeTime

**文件路径**: `model\o\RangeTime.java`

## 代码文档
///
@author 张治保
@since 2024/5/30
 
///


## 文件结构
```
项目根目录
└── model\o
    └── RangeTime.java
```

## 完整代码
```java
package com.medusa.gruul.global.model.o;

import java.time.LocalTime;

/**
 * @author 张治保
 * @since 2024/5/30
 */
public class RangeTime extends RangeTemporal<RangeTime, LocalTime> {
}

```

# com.medusa.gruul.global.model.o.ThreadPoolProperties

**文件路径**: `model\o\ThreadPoolProperties.java`

## 代码文档
///
线程池通用配置详情

@author 张治保
@since 2023/12/08
 
///

///
线程池线程名前缀
     
///

///
核心线程数
     
///

///
最大线程数
     
///

///
线程存活时间长度
     
///

///
任务队列长度
     
///


## 文件结构
```
项目根目录
└── model\o
    └── ThreadPoolProperties.java
```

## 完整代码
```java
package com.medusa.gruul.global.model.o;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * 线程池通用配置详情
 *
 * @author 张治保
 * @since 2023/12/08
 */
@Getter
@Setter
@ToString
@Accessors(chain = true)
public class ThreadPoolProperties implements Serializable {

    /**
     * 线程池线程名前缀
     */
    private String threadNamePrefix = "Future";

    /**
     * 核心线程数
     */
    private int corePoolSize = 1;

    /**
     * 最大线程数
     */
    private int maxPoolSize = 2;

    /**
     * 线程存活时间长度
     */
    private int keepAliveSeconds = 60;

    /**
     * 任务队列长度
     */
    private int queueCapacity = 20;

}
```

# com.medusa.gruul.global.model.strategy.AbstractStrategyFactory

**文件路径**: `model\strategy\AbstractStrategyFactory.java`

## 代码文档
///
策略处理器工厂

@param <T> 策略类型
@param <P> 参数
@param <R> 结果
@author 张治保
@since 2024/4/10
 
///

///
策略映射
     
///

///
注册策略映射

@param type             策略类型
@param strategySupplier 策略提供者
     
///

///
获取策略

@param type 策略类型
@return 策略
     
///

///
执行策略

@param type  策略类型
@param param 参数
@return 结果
     
///

///
执行策略

@param type 策略类型
@return 结果
     
///

///
执行策略

@param type  策略类型
@param param 参数
     
///

///
执行策略

@param type 策略类型
     
///


## 文件结构
```
项目根目录
└── model\strategy
    └── AbstractStrategyFactory.java
```

## 完整代码
```java
package com.medusa.gruul.global.model.strategy;

import cn.hutool.core.map.MapUtil;
import lombok.RequiredArgsConstructor;

import java.util.Map;
import java.util.function.Supplier;

/**
 * 策略处理器工厂
 *
 * @param <T> 策略类型
 * @param <P> 参数
 * @param <R> 结果
 * @author 张治保
 * @since 2024/4/10
 */
@RequiredArgsConstructor
public abstract class AbstractStrategyFactory<T, P, R> {

    /**
     * 策略映射
     */
    private final Map<T, StrategyHandle<T, P, R>> strategyMap = MapUtil.newHashMap();

    protected Map<T, StrategyHandle<T, P, R>> strategyMap() {
        if (MapUtil.isEmpty(strategyMap)) {
            getStrategyMap()
                    .forEach(
                            (type, supplier) -> strategyMap.put(
                                    type,
                                    new StrategyHandle<>(supplier)
                            )
                    );
        }
        return strategyMap;
    }

    /**
     * 注册策略映射
     *
     * @param type             策略类型
     * @param strategySupplier 策略提供者
     */
    public void register(T type, Supplier<? extends IStrategy<T, P, R>> strategySupplier) {
        strategyMap().put(
                type,
                new StrategyHandle<>(strategySupplier)
        );
    }

    public abstract Map<T, Supplier<? extends IStrategy<T, P, R>>> getStrategyMap();


    /**
     * 获取策略
     *
     * @param type 策略类型
     * @return 策略
     */
    public IStrategy<T, P, R> getStrategy(T type) {
        StrategyHandle<T, P, R> strategyHandle = strategyMap().get(type);
        if (strategyHandle == null) {
            throw new IllegalArgumentException("未找到对应的策略");
        }
        return strategyHandle.getStrategy();
    }

    /**
     * 执行策略
     *
     * @param type  策略类型
     * @param param 参数
     * @return 结果
     */
    public R execute(T type, P param) {
        return getStrategy(type).execute(type, param);
    }

    /**
     * 执行策略
     *
     * @param type 策略类型
     * @return 结果
     */
    public R execute(T type) {
        return execute(type, null);
    }


    /**
     * 执行策略
     *
     * @param type  策略类型
     * @param param 参数
     */
    public void exec(T type, P param) {
        execute(type, param);
    }

    /**
     * 执行策略
     *
     * @param type 策略类型
     */
    public void exec(T type) {
        exec(type, null);
    }

}

```

# com.medusa.gruul.global.model.strategy.IStrategy

**文件路径**: `model\strategy\IStrategy.java`

## 代码文档
///
策略接口

@author 张治保
@since 2024/4/10
 
///

///
return void helper
     
///

///
执行策略

@param type  策略类型
@param param 参数
@return R 返回值
     
///


## 文件结构
```
项目根目录
└── model\strategy
    └── IStrategy.java
```

## 完整代码
```java
package com.medusa.gruul.global.model.strategy;

/**
 * 策略接口
 *
 * @author 张治保
 * @since 2024/4/10
 */
@FunctionalInterface
public interface IStrategy<T, P, R> {

    /**
     * return void helper
     */
    Void VOID = null;

    /**
     * 执行策略
     *
     * @param type  策略类型
     * @param param 参数
     * @return R 返回值
     */
    R execute(T type, P param);

}

```

# com.medusa.gruul.global.model.strategy.IVStrategy

**文件路径**: `model\strategy\IVStrategy.java`

## 代码文档
///
没有返回值类型的策接口

@author 张治保
@since 2024/12/12
 
///


## 文件结构
```
项目根目录
└── model\strategy
    └── IVStrategy.java
```

## 完整代码
```java
package com.medusa.gruul.global.model.strategy;

/**
 * 没有返回值类型的策接口
 *
 * @author 张治保
 * @since 2024/12/12
 */
@FunctionalInterface
public interface IVStrategy<T, P> extends IStrategy<T, P, Void> {

    void exec(T type, P param);

    @Override
    default Void execute(T type, P param) {
        exec(type, param);
        return VOID;
    }
}

```

# com.medusa.gruul.global.model.strategy.StrategyHandle

**文件路径**: `model\strategy\StrategyHandle.java`

## 代码文档
///
策略处理器

@author 张治保
@since 2024/4/10
 
///

///
策略提供者
     
///

///
策略
     
///

///
获取策略
1. 如果策略为空，通过策略提供者创建策略
2. 返回策略

@return 策略
     
///


## 文件结构
```
项目根目录
└── model\strategy
    └── StrategyHandle.java
```

## 完整代码
```java
package com.medusa.gruul.global.model.strategy;

import java.util.function.Supplier;

/**
 * 策略处理器
 *
 * @author 张治保
 * @since 2024/4/10
 */
public class StrategyHandle<T, P, R> {

    /**
     * 策略提供者
     */
    private Supplier<? extends IStrategy<T, P, R>> supplier;
    /**
     * 策略
     */
    private volatile IStrategy<T, P, R> strategy;


    public StrategyHandle(Supplier<? extends IStrategy<T, P, R>> supplier) {
        this.supplier = supplier;
    }

    /**
     * 获取策略
     * 1. 如果策略为空，通过策略提供者创建策略
     * 2. 返回策略
     *
     * @return 策略
     */
    public IStrategy<T, P, R> getStrategy() {
        if (strategy == null) {
            synchronized (this) {
                if (strategy == null) {
                    strategy = supplier.get();
                    // help GC
                    supplier = null;
                }
            }
        }
        return strategy;
    }
}

```

# com.medusa.gruul.global.model.validate.ValidateHelper

**文件路径**: `model\validate\ValidateHelper.java`

## 代码文档
///
手动 validate工具

@author 张治保
@since 2023-10-25
 
///

///
校验器
     
///

///
通过分组来校验实体类
     
///

///
通过分组来校验实体类

@param t      实体类
@param groups 校验分组
@param <T>    实体类类型
@return 字段错误信息列表
     
///


## 文件结构
```
项目根目录
└── model\validate
    └── ValidateHelper.java
```

## 完整代码
```java
package com.medusa.gruul.global.model.validate;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;

import java.util.List;
import java.util.Set;

/**
 * 手动 validate工具
 *
 * @author 张治保
 * @since 2023-10-25
 */
public class ValidateHelper {

    /**
     * 校验器
     */
    private static final Validator VALIDATOR;

    static {
        try (ValidatorFactory factory = Validation.buildDefaultValidatorFactory()) {
            VALIDATOR = factory.getValidator();
        }
    }

    /**
     * 通过分组来校验实体类
     */
    public static <T> Set<ConstraintViolation<T>> validate(T t, Class<?>... groups) {
        if (t == null) {
            return Set.of();
        }
        return VALIDATOR.validate(t, groups);
    }

    /**
     * 通过分组来校验实体类
     *
     * @param t      实体类
     * @param groups 校验分组
     * @param <T>    实体类类型
     * @return 字段错误信息列表
     */
    public static <T> List<ValidFieldError> filedErrors(T t, Class<?>... groups) {
        return ValidFieldError.of(validate(t, groups));
    }
}
```

# com.medusa.gruul.global.model.validate.ValidFieldError

**文件路径**: `model\validate\ValidFieldError.java`

## 代码文档
///
@author 张治保
@since 2023/11/15
 
///

///
字段访问路径
     
///

///
错误信息
     
///


## 文件结构
```
项目根目录
└── model\validate
    └── ValidFieldError.java
```

## 完整代码
```java
package com.medusa.gruul.global.model.validate;

import jakarta.validation.ConstraintViolation;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.List;
import java.util.Set;

/**
 * @author 张治保
 * @since 2023/11/15
 */
@Getter
@Setter
@ToString
@Accessors(chain = true)
public class ValidFieldError implements Serializable {

    /**
     * 字段访问路径
     */
    private String filed;

    /**
     * 错误信息
     */
    private String message;


    public static <T> List<ValidFieldError> of(Set<ConstraintViolation<T>> violations) {
        if (violations == null || violations.isEmpty()) {
            return List.of();
        }
        return violations.stream()
                .map(
                        violation -> new ValidFieldError()
                                .setFiled(violation.getPropertyPath().toString())
                                .setMessage(violation.getMessage())
                ).toList();
    }
}

```

# com.medusa.gruul.global.model.helper.request.IRequest

**文件路径**: `helper\request\IRequest.java`

## 代码文档
///
@author 张治保
@since 2024/8/10
 
///


## 文件结构
```
项目根目录
└── helper\request
    └── IRequest.java
```

## 完整代码
```java
package com.medusa.gruul.global.model.helper.request;

import java.util.Map;

/**
 * @author 张治保
 * @since 2024/8/10
 */
public interface IRequest {

    String post(String url, Map<String, String> headers, String body);

    class INSTANCE {
        public static final IRequest DEFAULT = new Request(10 * 1000);
    }
}


```

# com.medusa.gruul.global.model.helper.request.IResponse

**文件路径**: `helper\request\IResponse.java`

## 代码文档
///
@author 张治保
@since 2024/8/10
 
///

///
是否响应成功

@return 是否响应成功
     
///


## 文件结构
```
项目根目录
└── helper\request
    └── IResponse.java
```

## 完整代码
```java
package com.medusa.gruul.global.model.helper.request;

import java.io.Serializable;

/**
 * @author 张治保
 * @since 2024/8/10
 */
public interface IResponse<T> extends Serializable {

    /**
     * 是否响应成功
     *
     * @return 是否响应成功
     */
    boolean isSuccess();

}

```

# com.medusa.gruul.global.model.helper.request.Request

**文件路径**: `helper\request\Request.java`

## 代码文档
///
@author 张治保
@since 2024/8/10
 
///


## 文件结构
```
项目根目录
└── helper\request
    └── Request.java
```

## 完整代码
```java
package com.medusa.gruul.global.model.helper.request;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import javax.net.ssl.*;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.security.cert.X509Certificate;
import java.util.Map;

/**
 * @author 张治保
 * @since 2024/8/10
 */
@Slf4j
@RequiredArgsConstructor
public class Request implements IRequest {

    private static final TrustManager[] trustAllCerts = new TrustManager[]{new X509TrustManager() {
        @Override
        public X509Certificate[] getAcceptedIssuers() {
            return new X509Certificate[]{};
        }

        @Override
        public void checkClientTrusted(X509Certificate[] chain, String authType) {
        }

        @Override
        public void checkServerTrusted(X509Certificate[] chain, String authType) {
        }
    }};
    private final int readTimeOut;

    private static void trustAllHosts(HttpsURLConnection connection) {
        try {
            SSLContext sc = SSLContext.getInstance("TLS");
            sc.init(null, trustAllCerts, new java.security.SecureRandom());
            SSLSocketFactory newFactory = sc.getSocketFactory();

            connection.setSSLSocketFactory(newFactory);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
        connection.setHostnameVerifier((hostname, session) -> true);
    }

    @Override
    public String post(String url, Map<String, String> headers, String body) {
        HttpURLConnection connection = null;
        try {
            // connection
            connection = (HttpURLConnection) new URL(url).openConnection();
            // trust-https
            if (url.startsWith("https")) {
                trustAllHosts((HttpsURLConnection) connection);
            }
            // connection setting
            connection.setRequestMethod("POST");
            connection.setDoOutput(true);
            connection.setDoInput(true);
            connection.setUseCaches(false);
            connection.setReadTimeout(readTimeOut);
            connection.setConnectTimeout(3 * 1000);
            if (headers != null && !headers.isEmpty()) {
                headers.forEach(connection::setRequestProperty);
            }
            // do connection
            connection.connect();

            // write requestBody
            try (OutputStream outputStream = connection.getOutputStream()) {
                outputStream.write(body.getBytes(StandardCharsets.UTF_8));
                outputStream.flush();
            }
            // valid StatusCode
            int statusCode = connection.getResponseCode();
            if (statusCode != HttpURLConnection.HTTP_OK) {
                throw new RuntimeException("request fail, StatusCode(" + statusCode + ") invalid. for url : " + url + "[" + connection.getResponseMessage() + "]");
            }
            // result
            try (BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(connection.getInputStream(), StandardCharsets.UTF_8))) {
                StringBuilder result = new StringBuilder();
                String line;
                while ((line = bufferedReader.readLine()) != null) {
                    result.append(line);
                }
                return result.toString();
            }

        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new RuntimeException("request error(" + e.getMessage() + "), for url : " + url, e);
        } finally {
            try {
                if (connection != null) {
                    connection.disconnect();
                }
            } catch (Exception e2) {
                log.error(e2.getMessage(), e2);
            }
        }
    }
}

```

# com.medusa.gruul.global.model.exception.collector.CurrentErrorHolder

**文件路径**: `exception\collector\CurrentErrorHolder.java`

## 代码文档
///
当前行的错误信息收集器 ThreadLocal 保存

@author 张治保
date 2021/12/15
 
///

///
当前行的错误信息收集器
     
///

///
设置当前行的错误信息收集器

@param currentErrorCollector 错误信息收集器
     
///

///
获取当前行的错误信息收集器

@return 错误信息收集器
     
///

///
清除当前行的错误信息收集器
     
///


## 文件结构
```
项目根目录
└── exception\collector
    └── CurrentErrorHolder.java
```

## 完整代码
```java
package com.medusa.gruul.global.model.exception.collector;


/**
 * 当前行的错误信息收集器 ThreadLocal 保存
 *
 * @author 张治保
 * date 2021/12/15
 */
class CurrentErrorHolder {

    /**
     * 当前行的错误信息收集器
     */
    private static final ThreadLocal<CurrentErrors> LOCAL = new ThreadLocal<>();

    /**
     * 设置当前行的错误信息收集器
     *
     * @param currentErrorCollector 错误信息收集器
     */
    static void set(CurrentErrors currentErrorCollector) {
        LOCAL.set(currentErrorCollector);
    }

    /**
     * 获取当前行的错误信息收集器
     *
     * @return 错误信息收集器
     */
    static CurrentErrors get() {
        return LOCAL.get();
    }


    /**
     * 清除当前行的错误信息收集器
     */
    static void clear() {
        LOCAL.remove();
    }
}

```

# com.medusa.gruul.global.model.exception.collector.CurrentErrors

**文件路径**: `exception\collector\CurrentErrors.java`

## 代码文档
///
@author 张治保
@since 2023/10/18
 
///

///
当前行的主键
     
///

///
当前行的错误数据
     
///

///
添加错误信息

@param desc 错误描述信息
     
///

///
添加错误信息 静态方法 方便调用 无需传递当前行的错误信息收集器

@param filed 字段信息
@param desc  错误描述信息
     
///

///
添加错误信息 静态方法 方便调用 无需传递当前行的错误信息收集器

@param fieldError 字段与描述信息
     
///

///
判断是否有错误

@return 是否有错误
     
///

///
errors getter

@return errors
     
///

///
id getter

@return id
     
///


## 文件结构
```
项目根目录
└── exception\collector
    └── CurrentErrors.java
```

## 完整代码
```java
package com.medusa.gruul.global.model.exception.collector;

import cn.hutool.core.collection.CollUtil;
import lombok.RequiredArgsConstructor;

import java.util.ArrayList;
import java.util.List;

/**
 * @author 张治保
 * @since 2023/10/18
 */
@RequiredArgsConstructor
public class CurrentErrors {

    /**
     * 当前行的主键
     */
    private final Comparable<?> id;
    /**
     * 当前行的错误数据
     */
    private final List<FieldError> errors = new ArrayList<>();

    /**
     * 添加错误信息
     *
     * @param desc 错误描述信息
     */
    public static void add(String desc) {
        add(null, desc);
    }

    /**
     * 添加错误信息 静态方法 方便调用 无需传递当前行的错误信息收集器
     *
     * @param filed 字段信息
     * @param desc  错误描述信息
     */
    public static void add(String filed, String desc) {
        add(new FieldError().setFiled(filed).setDesc(desc));
    }

    /**
     * 添加错误信息 静态方法 方便调用 无需传递当前行的错误信息收集器
     *
     * @param fieldError 字段与描述信息
     */
    public static void add(FieldError fieldError) {
        CurrentErrors currentErrors = CurrentErrorHolder.get();
        if (currentErrors == null) {
            throw new RuntimeException("当前行错误信息收集器未初始化,当前 task 必须使用 ErrorCollector.current 方法执行");
        }
        currentErrors.errors.add(fieldError);
    }

    /**
     * 判断是否有错误
     *
     * @return 是否有错误
     */
    public static boolean hasError() {
        CurrentErrors currentErrors = CurrentErrorHolder.get();
        if (currentErrors == null) {
            throw new RuntimeException("当前行错误信息收集器未初始化,当前 task 必须使用 ErrorCollector.current 方法执行");
        }
        return CollUtil.isNotEmpty(currentErrors.errors);
    }

    /**
     * errors getter
     *
     * @return errors
     */
    List<FieldError> errors() {
        return errors;
    }

    /**
     * id getter
     *
     * @return id
     */
    Comparable<?> id() {
        return id;
    }


}

```

# com.medusa.gruul.global.model.exception.collector.DistinctValidator

**文件路径**: `exception\collector\DistinctValidator.java`

## 代码文档
///
重复数据校验工具

@author 张治保
@since 2023/10/27
 
///

///
数据集合字段重复性校验

@param list      可迭代数据集合
@param field     获取对应的字段值
@param fieldDesc 字段描述
@param <T>       值类型
@return 异常结果收集
     
///

///
数据集合字段重复性校验

@param rowDataMap 数据集合 key 行号 value 数据
@param field      获取对应的字段值
@param fieldDesc  字段描述
@param <T>        值类型
@return 异常结果收集
     
///


## 文件结构
```
项目根目录
└── exception\collector
    └── DistinctValidator.java
```

## 完整代码
```java
package com.medusa.gruul.global.model.exception.collector;

import cn.hutool.core.collection.CollUtil;

import java.util.*;
import java.util.function.Function;

/**
 * 重复数据校验工具
 *
 * @author 张治保
 * @since 2023/10/27
 */
public interface DistinctValidator {

    String ROW_NUMBER_TEMPLATE = "第%s行";
    String DOT = "、";

    /**
     * 数据集合字段重复性校验
     *
     * @param list      可迭代数据集合
     * @param field     获取对应的字段值
     * @param fieldDesc 字段描述
     * @param <T>       值类型
     * @return 异常结果收集
     */
    static <T> ErrorCollector valid(List<T> list, Function<T, Comparable<?>> field, String fieldDesc) {
        if (list == null || list.isEmpty()) {
            return ErrorCollector.create();
        }
        Map<Number, T> rowDataMap = new HashMap<>(list.size());
        for (int i = 0; i < list.size(); i++) {
            rowDataMap.put(i + 1, list.get(i));
        }
        return DistinctValidator.valid(rowDataMap, field, fieldDesc);
    }

    /**
     * 数据集合字段重复性校验
     *
     * @param rowDataMap 数据集合 key 行号 value 数据
     * @param field      获取对应的字段值
     * @param fieldDesc  字段描述
     * @param <T>        值类型
     * @return 异常结果收集
     */
    static <T> ErrorCollector valid(Map<? extends Number, T> rowDataMap, Function<T, Comparable<?>> field, String fieldDesc) {
        if (CollUtil.isEmpty(rowDataMap)) {
            return ErrorCollector.create();
        }
        Map<Comparable<?>, Set<Long>> distinctData = new HashMap<>(rowDataMap.size());
        rowDataMap.forEach(
                (key, value) -> {
                    Comparable<?> filedValue = field.apply(value);
                    if (filedValue == null) {
                        return;
                    }
                    distinctData.computeIfAbsent(filedValue, k -> new HashSet<>())
                            .add(key.longValue());
                }
        );
        ErrorCollector collector = ErrorCollector.create()
                .keyMapper(id -> null)
                .valueMapper(fieldError -> fieldError.getFiled() + "：" + fieldError.getDesc());
        distinctData.forEach(
                (key, value) -> {
                    if (value.size() <= 1) {
                        return;
                    }
                    StringBuilder msgBuilder = new StringBuilder();
                    value.stream().sorted()
                            .forEach(id -> msgBuilder.append(String.format(ROW_NUMBER_TEMPLATE, id)).append(DOT));
                    msgBuilder.deleteCharAt(msgBuilder.length() - 1);
                    collector.current(
                            key,
                            () -> CurrentErrors.add(msgBuilder.toString(), fieldDesc + "【" + key + "】重复")
                    );
                }
        );
        return collector;
    }


}

```

# com.medusa.gruul.global.model.exception.collector.ErrorCollector

**文件路径**: `exception\collector\ErrorCollector.java`

## 代码文档
///
@author 张治保
@since 2023/10/18
 
///

///
消息模板
     
///

///
默认分隔符
     
///

///
默认的 key 转换器
     
///

///
默认的 value 转换器
     
///

///
错误信息 收集结果
     
///

///
keyMapper 键转换器
     
///

///
valueMapper 值转换器
     
///

///
创建一个错误信息收集器

@return 错误信息收集器
     
///

///
自定义主键转换器

@param keyMapper 主键转换器
@return this
     
///

///
自定义错误信息转换器

@param valueMapper 错误信息转换器
@return this
     
///

///
创建一个当前行的错误信息收集器

@param id   当前行的主键
@param task 业务逻辑
@param <T>  业务逻辑的返回值类型
@return 业务逻辑的返回值
     
///

///
创建一个当前行的错误信息收集器

@param id   当前行的主键
@param task 业务逻辑
     
///

///
添加当前行的错误信息

@param current 当前行的错误信息收集器
     
///

///
获取所有已收集的错误信息

@return 错误信息
     
///

///
获取所有已收集的错误信息 转 list

@return 错误信息
     
///

///
是否有错误

@return 是否有错误
     
///

///
获取所有已收集的错误信息 转 String

@return 错误信息
     
///

///
获取所有已收集的错误信息 转 String

@param separator 错误信息的分隔符
@return 错误信息
     
///

///
获取所有已收集的错误信息 转 list

@param keyMapper   主键转换器
@param valueMapper 错误信息转换器
@return 错误信息
     
///

///
合并错误信息

@param errors 错误信息
@return this
     
///

///
合并错误信息

@param errorCollector 错误信息收集器
@return this
     
///

///
如果有错误 则 抛出错误信息
     
///


## 文件结构
```
项目根目录
└── exception\collector
    └── ErrorCollector.java
```

## 完整代码
```java
package com.medusa.gruul.global.model.exception.collector;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import com.medusa.gruul.global.model.exception.GlobalException;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.util.*;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;

/**
 * @author 张治保
 * @since 2023/10/18
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ErrorCollector {

    /**
     * 消息模板
     */
    private static final String DEFAULT_MSG_TEMPLATE = "%s：%s";

    private static final CharSequence DEFAULT_VALUES_SEPARATOR = "；";
    /**
     * 默认分隔符
     */
    private static final String VALUE_SEPARATOR = "、";

    /**
     * 默认的 key 转换器
     */
    private static final Function<Comparable<?>, ?> DEFAULT_KEY_MAPPER = Function.identity();

    /**
     * 默认的 value 转换器
     */
    private static final Function<FieldError, String> DEFAULT_VALUE_MAPPER = FieldError::toString;


    /**
     * 错误信息 收集结果
     */
    private final Map<Comparable<?>, List<FieldError>> errors = new TreeMap<>();

    /**
     * keyMapper 键转换器
     */
    private Function<Comparable<?>, ?> keyMapper = DEFAULT_KEY_MAPPER;

    /**
     * valueMapper 值转换器
     */
    private Function<FieldError, String> valueMapper = DEFAULT_VALUE_MAPPER;

    /**
     * 创建一个错误信息收集器
     *
     * @return 错误信息收集器
     */
    public static ErrorCollector create() {
        return new ErrorCollector();
    }

    /**
     * 自定义主键转换器
     *
     * @param keyMapper 主键转换器
     * @return this
     */
    public ErrorCollector keyMapper(Function<Comparable<?>, ?> keyMapper) {
        this.keyMapper = keyMapper;
        return this;
    }

    /**
     * 自定义错误信息转换器
     *
     * @param valueMapper 错误信息转换器
     * @return this
     */
    public ErrorCollector valueMapper(Function<FieldError, String> valueMapper) {
        this.valueMapper = valueMapper;
        return this;
    }

    /**
     * 创建一个当前行的错误信息收集器
     *
     * @param id   当前行的主键
     * @param task 业务逻辑
     * @param <T>  业务逻辑的返回值类型
     * @return 业务逻辑的返回值
     */
    public <T> T current(Comparable<?> id, Supplier<T> task) {
        CurrentErrors preCollector = CurrentErrorHolder.get();
        CurrentErrors current = new CurrentErrors(id);
        try {
            CurrentErrorHolder.set(current);
            return task.get();
        } finally {
            if (preCollector != null) {
                CurrentErrorHolder.set(preCollector);
            } else {
                CurrentErrorHolder.clear();
            }
            add(current);
        }
    }

    /**
     * 创建一个当前行的错误信息收集器
     *
     * @param id   当前行的主键
     * @param task 业务逻辑
     */
    public void current(Comparable<?> id, Runnable task) {
        current(id, () -> {
            task.run();
            return null;
        });
    }

    /**
     * 添加当前行的错误信息
     *
     * @param current 当前行的错误信息收集器
     */
    void add(CurrentErrors current) {
        if (CollUtil.isEmpty(current.errors())) {
            return;
        }
        List<FieldError> fieldErrors = errors.computeIfAbsent(current.id(), k -> new ArrayList<>());
        fieldErrors.addAll(current.errors());
        Collections.sort(fieldErrors);
    }

    /**
     * 获取所有已收集的错误信息
     *
     * @return 错误信息
     */
    public Map<Comparable<?>, List<FieldError>> errors() {
        return errors;
    }

    /**
     * 获取所有已收集的错误信息 转 list
     *
     * @return 错误信息
     */
    public List<String> toList() {
        return this.toList(keyMapper, valueMapper);
    }

    /**
     * 是否有错误
     *
     * @return 是否有错误
     */
    public boolean hasError() {
        return CollUtil.isNotEmpty(errors);
    }

    /**
     * 获取所有已收集的错误信息 转 String
     *
     * @return 错误信息
     */
    @Override
    public String toString() {
        return toString(DEFAULT_VALUES_SEPARATOR);
    }

    /**
     * 获取所有已收集的错误信息 转 String
     *
     * @param separator 错误信息的分隔符
     * @return 错误信息
     */
    public String toString(CharSequence separator) {
        return StrUtil.join(separator, toList());
    }

    /**
     * 获取所有已收集的错误信息 转 list
     *
     * @param keyMapper   主键转换器
     * @param valueMapper 错误信息转换器
     * @return 错误信息
     */
    private List<String> toList(Function<Comparable<?>, ?> keyMapper, Function<FieldError, String> valueMapper) {
        return errors.entrySet()
                .stream()
                .map(
                        entry -> {
                            Object apply = keyMapper.apply(entry.getKey());
                            String valuesStr = entry.getValue().stream().map(valueMapper).collect(Collectors.joining(VALUE_SEPARATOR));
                            return apply == null ? valuesStr : String.format(DEFAULT_MSG_TEMPLATE, apply, valuesStr);
                        }
                ).toList();
    }


    /**
     * 合并错误信息
     *
     * @param errors 错误信息
     * @return this
     */
    public ErrorCollector merge(Map<Comparable<?>, List<FieldError>> errors) {
        if (errors == null || errors.isEmpty()) {
            return this;
        }
        errors.forEach(
                (key, value) -> {
                    List<FieldError> fieldErrors = this.errors.computeIfAbsent(key, k -> new ArrayList<>());
                    fieldErrors.addAll(value);
                    Collections.sort(fieldErrors);
                }
        );
        return this;
    }

    /**
     * 合并错误信息
     *
     * @param errorCollector 错误信息收集器
     * @return this
     */
    public ErrorCollector merge(ErrorCollector errorCollector) {
        if (errorCollector == null) {
            return this;
        }
        return merge(errorCollector.errors);
    }

    /**
     * 如果有错误 则 抛出错误信息
     */
    public void throwIfError() {
        if (hasError()) {
            throw new GlobalException(toString());
        }
    }

}

```

# com.medusa.gruul.global.model.exception.collector.FieldError

**文件路径**: `exception\collector\FieldError.java`

## 代码文档
///
字段错误信息

@author 张治保
@since 2023/10/18
 
///

///
字段信息 主语
     
///

///
错误描述信息 谓语 宾语...
     
///


## 文件结构
```
项目根目录
└── exception\collector
    └── FieldError.java
```

## 完整代码
```java
package com.medusa.gruul.global.model.exception.collector;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * 字段错误信息
 *
 * @author 张治保
 * @since 2023/10/18
 */
@Getter
@Setter
@EqualsAndHashCode(of = "filed")
@Accessors(chain = true)
public class FieldError implements Comparable<FieldError>, Serializable {

    /**
     * 字段信息 主语
     */
    private String filed;

    /**
     * 错误描述信息 谓语 宾语...
     */
    private String desc;


    @Override
    public String toString() {
        return StrUtil.blankToDefault(filed, "") + desc;
    }

    @Override
    public int compareTo(@NonNull FieldError o) {
        return ObjectUtil.compare(this.filed, o.filed);
    }
}

```

# com.medusa.gruul.common.model.constant.CommonPool

**文件路径**: `model\constant\CommonPool.java`

## 代码文档
///
@author xiaoq
@date 2022/02/19
Common常量池
 
///

///
*************  BigDecimal  ****************
///

///
*************  数字常量  ****************
///

///
@deprecated 请使用 {@link SqlHelper#SQL_LIMIT_1}
     
///

///
*********** String常量******************
///

///
手机区号
     
///

///
*********** 单位转换常量 ********************
///


## 文件结构
```
项目根目录
└── model\constant
    └── CommonPool.java
```

## 完整代码
```java
package com.medusa.gruul.common.model.constant;

import java.math.BigDecimal;

/**
 * @author xiaoq
 * @date 2022/02/19
 * Common常量池
 */
public interface CommonPool {
    /****************  BigDecimal  *****************/
    BigDecimal MIN = new BigDecimal("0.01");
    /****************  数字常量  *****************/
    int NUMBER_ZERO = 0;
    int NUMBER_ONE = 1;
    int NUMBER_TWO = 2;
    int NUMBER_THREE = 3;
    int NUMBER_FOUR = 4;
    int NUMBER_FIVE = 5;
    int NUMBER_SIX = 6;
    int NUMBER_SEVEN = 7;
    int NUMBER_EIGHT = 8;
    int NUMBER_NINE = 9;
    int NUMBER_TEN = 10;
    int NUMBER_TWELVE = 12;
    int NUMBER_FIFTEEN = 15;
    int NUMBER_TWENTY_EIGHT = 28;
    int NUMBER_THIRTY = 30;
    int NUMBER_NINETY = 90;
    int NUMBER_ONE_HUNDRED = 100;

    int NUMBER_ONE_HUNDRED_TWENTY = 120;

    int ONE_THOUSAND_TWENTY_FOUR = 1024;

    /**
     * @deprecated 请使用 {@link SqlHelper#SQL_LIMIT_1}
     */
    @Deprecated
    String SQL_LIMIT_1 = "LIMIT 1";

    /************** String常量*******************/
    String CODE = "code";

    /**
     * 手机区号
     */
    String NATION_CODE = "86";


    /************** 单位转换常量 *********************/
    Long UNIT_CONVERSION_TEN_THOUSAND = 10000L;

    Long UNIT_CONVERSION_HUNDRED = 100L;


    BigDecimal BIG_DECIMAL_UNIT_CONVERSION_TEN_THOUSAND = new BigDecimal("10000");

    BigDecimal BIG_DECIMAL_UNIT_CONVERSION_HUNDRED = new BigDecimal("100");

}

```

# com.medusa.gruul.common.model.constant.Xxl

**文件路径**: `model\constant\Xxl.java`

## 代码文档
///
@author 张治保
date 2023/4/1
 
///

///
xxl-job调度请求路径
     
///

///
xxl-job调度请求端口偏移量
     
///


## 文件结构
```
项目根目录
└── model\constant
    └── Xxl.java
```

## 完整代码
```java
package com.medusa.gruul.common.model.constant;

/**
 * @author 张治保
 * date 2023/4/1
 */
public interface Xxl {

    /**
     * xxl-job调度请求路径
     */
    String GATEWAY_XXL_EXEC_PATH = "/xxl-g/trigger";

    /**
     * xxl-job调度请求端口偏移量
     */
    Integer GATEWAY_XXL_EXEC_PORT_OFFSET = 20000;

}

```

# com.medusa.gruul.common.model.enums.RabbitParent

**文件路径**: `model\enums\RabbitParent.java`

## 代码文档
///
所有rabbit枚举的父类 抽象

@author 张治保
date 2022/7/7
 
///

///
获取交换机名称

@return 交换机名称
     
///

///
获取路由key名称

@return 路由key
     
///


## 文件结构
```
项目根目录
└── model\enums
    └── RabbitParent.java
```

## 完整代码
```java
package com.medusa.gruul.common.model.enums;

/**
 * 所有rabbit枚举的父类 抽象
 *
 * @author 张治保
 * date 2022/7/7
 */
public interface RabbitParent {

    /**
     * 获取交换机名称
     *
     * @return 交换机名称
     */
    String exchange();

    /**
     * 获取路由key名称
     *
     * @return 路由key
     */
    String routingKey();
}

```

