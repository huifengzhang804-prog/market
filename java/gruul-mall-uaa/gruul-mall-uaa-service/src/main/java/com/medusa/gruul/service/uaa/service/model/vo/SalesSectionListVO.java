package com.medusa.gruul.service.uaa.service.model.vo;

import com.medusa.gruul.common.fastjson2.annotation.Desensitize;
import com.medusa.gruul.service.uaa.api.enums.Gender;
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
public class SalesSectionListVO {

	/**
	 * openid
	 */
	private String salesSectionName;
	private List<PointsVO> pointsList;
	private List<ProductVO> productList;


}
