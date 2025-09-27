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
public class PurchaseMethodGroupVO {

	/**
	 * openid
	 */
	private List<PurchaseMethodVO> purchaseMethodGroupList;
	private int groupId;


}
