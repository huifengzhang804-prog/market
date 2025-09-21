package com.medusa.gruul.shop.api.model.dto;

import com.medusa.gruul.common.model.resp.SystemCode;
import com.medusa.gruul.common.module.app.shop.ShopMode;
import com.medusa.gruul.global.model.constant.SecurityConst;
import com.medusa.gruul.global.model.o.BaseDTO;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;


/**
 * 店铺与管理员映射
 *
 * @author 张治保
 * date 2022/5/25
 */
@Getter
@Setter
@ToString
@Accessors(chain = true)
public class ShopAdminMapDTO implements BaseDTO {

	/**
	 * 店铺类型
	 */
	private ShopMode shopMode;

	/**
	 * 店铺id
	 */
	private Long shopId;

	/**
	 * 用户id
	 */
	private Long userId;

	/**
	 * 是否直接启用
	 */
	private Boolean enable;


	@Override
	public void validParam() {
		SystemCode.PARAM_VALID_ERROR.trueThrow(getShopId() == null || getShopId() <= SecurityConst.NO_SHOP_ID_CLIENT_DEFAULT_SHOP_ID);
		SystemCode.PARAM_VALID_ERROR.trueThrow(getUserId() == null);
	}
}
