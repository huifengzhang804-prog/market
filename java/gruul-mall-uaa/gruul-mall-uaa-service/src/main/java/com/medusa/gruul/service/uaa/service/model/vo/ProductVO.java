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
public class ProductVO {

	/**
	 * openid
	 */
	private List<PurchaseMethodGroupVO> purchaseMethodList;
	private List<GiveAwayListVO> giveAwayList;
	private String presaleState;
	private String other;
	private long productId;
	private long shopId;
	private String picUrl;
	private String productName;
	


}
