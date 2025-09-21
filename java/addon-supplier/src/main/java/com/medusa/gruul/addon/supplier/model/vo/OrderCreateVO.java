package com.medusa.gruul.addon.supplier.model.vo;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.Map;

/**
 * @author 张治保
 * date 2023/7/20
 */
@Getter
@Setter
@ToString
@Accessors(chain = true)
public class OrderCreateVO implements Serializable {


	/**
	 * 订单主单号
	 */
	private String mainNo;

	/**
	 * 供应商 id 与订单号映射
	 */
	private Map<Long, String> orderNoMap;

}
