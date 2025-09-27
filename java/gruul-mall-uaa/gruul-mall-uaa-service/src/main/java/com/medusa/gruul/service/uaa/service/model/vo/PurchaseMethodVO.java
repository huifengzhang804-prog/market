package com.medusa.gruul.service.uaa.service.model.vo;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.List;

/**
 * @author 张治保
 * date 2022/5/24
 */
@Getter
@Setter
@ToString
public class PurchaseMethodVO {

	private String type;
	private String name;
	private double price;
	private int pointsId;
	private int groupId;



}
