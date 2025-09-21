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
