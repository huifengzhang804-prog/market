package com.medusa.gruul.service.uaa.service.model.vo;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * @author 张治保
 * date 2022/5/24
 */
@Getter
@Setter
@ToString
public class ProductInfoVO {

	private long productId;
	private long salePrice;
	private long shopId;
	private String	 sectionName;
	private String	 picUrl;
	private String	 productName;



}
