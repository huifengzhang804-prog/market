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
public class PurchaseMethodResultVO {

	private String type;
	private String name;
	private double priceRatio;
	private int pointsId;
	private int groupId;



}
