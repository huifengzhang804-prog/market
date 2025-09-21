# com.medusa.gruul.common.model.ModelI18NLoader

**文件路径**: `common\model\ModelI18NLoader.java`

## 代码文档
///
@author 张治保
date 2023/6/16
 
///


## 文件结构
```
项目根目录
└── common\model
    └── ModelI18NLoader.java
```

## 完整代码
```java
package com.medusa.gruul.common.model;

import com.medusa.gruul.global.i18n.I18NPropertiesLoader;

import java.util.Set;

/**
 * @author 张治保
 * date 2023/6/16
 */
public class ModelI18NLoader implements I18NPropertiesLoader {
	@Override
	public Set<String> paths() {
		return Set.of("i18n/model");
	}
}

```

# com.medusa.gruul.common.model.resp.IResultCode

**文件路径**: `model\resp\IResultCode.java`

## 代码文档
///
状态码接口

@author L.cm
 
///

///
返回的code码

@return code
	 
///

///
返回的消息

@return 消息
	 
///

///
错误代码

@return 错误代码
	 
///

///
错误消息key

@return 错误消息key
	 
///


## 文件结构
```
项目根目录
└── model\resp
    └── IResultCode.java
```

## 完整代码
```java
package com.medusa.gruul.common.model.resp;

import com.medusa.gruul.global.model.exception.Error;

/**
 * 状态码接口
 *
 * @author L.cm
 */
public interface IResultCode extends Error {
	/**
	 * 返回的code码
	 *
	 * @return code
	 */
	int getCode();

	/**
	 * 返回的消息
	 *
	 * @return 消息
	 */
	String getMsg();

	/**
	 * 错误代码
	 *
	 * @return 错误代码
	 */
	@Override
	default int code() {
		return getCode();
	}

	/**
	 * 错误消息key
	 *
	 * @return 错误消息key
	 */
	@Override
	default String msg() {
		return getMsg();
	}
}

```

# com.medusa.gruul.common.model.resp.Result

**文件路径**: `model\resp\Result.java`

## 代码文档
///
description: 响应信息主体

@author alan
Date: 2019/7/13 19:23
 
///

///
响应状态码
	 
///

///
响应消息
	 
///

///
响应数据
	 
///

///
判断是否成功

@author 张治保
2021年12月8日
	 
///


## 文件结构
```
项目根目录
└── model\resp
    └── Result.java
```

## 完整代码
```java
package com.medusa.gruul.common.model.resp;

import com.medusa.gruul.global.model.exception.Error;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;

import java.io.Serial;
import java.io.Serializable;

/**
 * description: 响应信息主体
 *
 * @author alan
 * Date: 2019/7/13 19:23
 */
@Getter
@Setter
@ToString
@Accessors(chain = true)
public class Result<T> implements Serializable {
	@Serial
	private static final long serialVersionUID = 1L;
	/**
	 * 响应状态码
	 */
	private int code;
	/**
	 * 响应消息
	 */
	private String msg;
	/**
	 * 响应数据
	 */
	private T data;

	private Result() {
	}

	public static <T> Result<T> ok() {
		return ok(null);
	}

	public static <T> Result<T> ok(T data) {
		return ok(data, null);
	}

	public static <T> Result<T> ok(T data, String msg) {
		return restResult(data, SystemCode.SUCCESS_CODE, msg);
	}

	public static <T> Result<T> failed() {
		return failed((String) null);
	}

	public static <T> Result<T> failed(String msg) {
		return restResult(null, SystemCode.FAILURE_CODE, msg);
	}

	public static <T> Result<T> failed(T data) {
		return restResult(data, SystemCode.FAILURE_CODE, null);
	}

	public static <T> Result<T> failed(int code, String msg) {
		return restResult(null, code, msg);
	}

	@SuppressWarnings("unchecked")
	public static <T> Result<T> failed(Error error) {
		Result<T> failed = failed(error.code(), error.msg());
		failed.setData((T) error.data());
		return failed;
	}

	public static <T> Result<T> failed(Error error, String msg) {
		return restResult(null, error.code(), msg);
	}

	private static <T> Result<T> restResult(T data, int code, String msg) {
		return new Result<T>()
				.setCode(code)
				.setData(data)
				.setMsg(msg);
	}

	/**
	 * 判断是否成功
	 *
	 * @author 张治保
	 * 2021年12月8日
	 */
	public boolean isSuccess() {
		return SystemCode.SUCCESS_CODE == this.code;
	}

}

```

# com.medusa.gruul.common.model.resp.SystemCode

**文件路径**: `model\resp\SystemCode.java`

## 代码文档
///
系统内置code

@author L.cm
 
///

///
系统未知异常
     
///

///
缺少必要的请求参数
     
///

///
请求参数类型错误
     
///

///
请求参数绑定错误
     
///

///
参数校验失败
     
///

///
404 没找到请求
     
///

///
消息不能读取
     
///

///
数据不存在
     
///

///
数据已存在
     
///

///
数据添加失败
     
///

///
数据更新失败
     
///

///
数据删除失败
     
///

///
接口限流
     
///

///
服务降级
     
///

///
热点参数限流
     
///

///
触发系统保护规则
     
///

///
授权规则不通过
     
///

///
系统繁忙
     
///

///
短信模板不存在
     
///

///
短信模板更新失败
     
///

///
短信签名添加失败
     
///

///
短信模板添加失败
     
///

///
时间范围错误
     
///

///
微信第三方平台异常 code
     
///

///
通用 异常 code
     
///

///
通用数据层 code
     
///

///
商品已经售罄
     
///

///
网关sentinel block code
300001 ～ 300005
     
///

///
code编码
     
///

///
中文信息描述
     
///


## 文件结构
```
项目根目录
└── model\resp
    └── SystemCode.java
```

## 完整代码
```java
package com.medusa.gruul.common.model.resp;

import com.medusa.gruul.global.i18n.I18N;
import lombok.AllArgsConstructor;

/**
 * 系统内置code
 *
 * @author L.cm
 */
@AllArgsConstructor
public enum SystemCode implements IResultCode {
    /**
     * 系统未知异常
     */
    FAILURE(SystemCode.FAILURE_CODE, "model.unknown"),
    /**
     * 缺少必要的请求参数
     */
    PARAM_MISS(SystemCode.PARAM_MISS_CODE, "model.param.missed"),
    /**
     * 请求参数类型错误
     */
    PARAM_TYPE_ERROR(SystemCode.PARAM_TYPE_ERROR_CODE, "model.param.wrong.type"),
    /**
     * 请求参数绑定错误
     */
    PARAM_BIND_ERROR(SystemCode.PARAM_BIND_ERROR_CODE, "model.param.bind.error"),
    /**
     * 参数校验失败
     */
    PARAM_VALID_ERROR(SystemCode.PARAM_VALID_ERROR_CODE, "model.param.invalid"),
    /**
     * 404 没找到请求
     */
    NOT_FOUND(SystemCode.NOT_FOUND_CODE, "model.query.not.found"),
    /**
     * 消息不能读取
     */
    MSG_NOT_READABLE(SystemCode.MSG_NOT_READABLE_CODE, "model.parameter.read.error"),

    //-------------------------------------------------------------//
    /**
     * 数据不存在
     */
    DATA_NOT_EXIST(SystemCode.DATA_NOT_EXIST_CODE, "model.query.not.found"),
    /**
     * 数据已存在
     */
    DATA_EXISTED(SystemCode.DATA_EXISTED_CODE, "model.data.existed"),
    /**
     * 数据添加失败
     */
    DATA_ADD_FAILED(SystemCode.DATA_ADD_FAILED_CODE, "model.data.add.failed"),
    /**
     * 数据更新失败
     */
    DATA_UPDATE_FAILED(SystemCode.DATA_UPDATE_FAILED_CODE, "model.data.update.failed"),
    /**
     * 数据删除失败
     */
    DATA_DELETE_FAILED(SystemCode.DATA_DELETE_FAILED_CODE, "model.data.delete.failed"),

    /**
     * 接口限流
     */
    SENTINEL_FLOW_BLOCK(SystemCode.SENTINEL_FLOW_BLOCK_CODE, "model.system.busy"),

    /**
     * 服务降级
     */
    SENTINEL_DEGRADE_BLOCK(SystemCode.SENTINEL_DEGRADE_BLOCK_CODE, "model.system.busy"),

    /**
     * 热点参数限流
     */
    SENTINEL_PARAM_FLOW_BLOCK(SystemCode.SENTINEL_PARAM_FLOW_BLOCK_CODE, "model.system.busy"),

    /**
     * 触发系统保护规则
     */
    SENTINEL_SYSTEM_BLOCK(SystemCode.SENTINEL_SYSTEM_BLOCK_CODE, "model.system.busy"),

    /**
     * 授权规则不通过
     */
    SENTINEL_AUTHORITY_BLOCK(SystemCode.SENTINEL_AUTHORITY_BLOCK_CODE, "model.system.busy"),

    /**
     * 系统繁忙
     */
    SYSTEM_BUSY(SystemCode.SYSTEM_BUSY_CODE, "model.system.busy"),

    /**
     * 短信模板不存在
     */
    SMS_TEMPLATE_NOT_EXISTS(SystemCode.DATA_NOT_EXIST_CODE, "sms.template.not.exists"),

    /**
     * 短信模板更新失败
     */
    SMS_TEMPLATE_UPDATE_FAILED(SystemCode.DATA_UPDATE_FAILED_CODE, "sms.template.update.failed"),

    /**
     * 短信签名添加失败
     */
    SMS_SIGN_ADD_FAILED(SystemCode.DATA_ADD_FAILED_CODE, "sms.sign.add.failed"),

    /**
     * 短信模板添加失败
     */
    SMS_TEMPLATE_ADD_FAILED(SystemCode.DATA_ADD_FAILED_CODE, "sms.template.add.failed"),

    /**
     * 时间范围错误
     */
    TIME_RANGE_ERROR(SystemCode.TIME_RANGE_ERROR_CODE,"time.range.error");

    /**
     * 微信第三方平台异常 code
     */
    public static final int WX_PLATEFROM_EXCEPTION = 101000;


    /**
     * 通用 异常 code
     */
    public static final int FAILURE_CODE = 400;
    public static final int SUCCESS_CODE = 200;
    public static final int UNAUTHORIZED_CODE = 401;
    public static final int PARAM_MISS_CODE = 100000;
    public static final int PARAM_TYPE_ERROR_CODE = 100001;
    public static final int PARAM_BIND_ERROR_CODE = 100002;
    public static final int PARAM_VALID_ERROR_CODE = 100003;
    public static final int NOT_FOUND_CODE = 100004;
    public static final int MSG_NOT_READABLE_CODE = 100005;
    public static final int METHOD_NOT_SUPPORTED_CODE = 100006;
    public static final int MEDIA_TYPE_NOT_SUPPORTED_CODE = 100007;
    public static final int MEDIA_TYPE_NOT_ACCEPT_CODE = 100008;
    public static final int REQ_REJECT_CODE = 100009;
    public static final int SYSTEM_BUSY_CODE = 100010;
    public static final int TIME_RANGE_ERROR_CODE = 100011;

    /**
     * 通用数据层 code
     */
    public static final int DATA_NOT_EXIST_CODE = 100100;
    public static final int DATA_EXISTED_CODE = 100101;
    public static final int DATA_ADD_FAILED_CODE = 100102;
    public static final int DATA_UPDATE_FAILED_CODE = 100103;
    public static final int DATA_DELETE_FAILED_CODE = 100104;

    /**
     * 商品已经售罄
     */
    public static final int ITEM_SOLD_OUT_CODE = 200001;

    /**
     * 网关sentinel block code
     * 300001 ～ 300005
     */
    public static final int SENTINEL_FLOW_BLOCK_CODE = 300001;
    public static final int SENTINEL_DEGRADE_BLOCK_CODE = 300002;
    public static final int SENTINEL_PARAM_FLOW_BLOCK_CODE = 300003;
    public static final int SENTINEL_SYSTEM_BLOCK_CODE = 300004;
    public static final int SENTINEL_AUTHORITY_BLOCK_CODE = 300005;


    /**
     * code编码
     */
    final int code;
    /**
     * 中文信息描述
     */
    final String msgCode;

    @Override
    public int getCode() {
        return code;
    }

    @Override
    public String getMsg() {
        return I18N.msg(msgCode);
    }

}

```

