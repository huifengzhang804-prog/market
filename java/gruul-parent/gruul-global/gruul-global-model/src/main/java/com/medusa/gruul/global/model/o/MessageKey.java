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
