package com.medusa.gruul.common.model.base;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * @author 张治保
 * date 2023/7/1
 */
@Getter
@Setter
@ToString
@EqualsAndHashCode
@Accessors(chain = true)
public class ProductSkuKey implements Serializable {

	/**
	 * 商品 id
	 */
	private Long productId;

	/**
	 * sku Id
	 */
	private Long skuId;
}
