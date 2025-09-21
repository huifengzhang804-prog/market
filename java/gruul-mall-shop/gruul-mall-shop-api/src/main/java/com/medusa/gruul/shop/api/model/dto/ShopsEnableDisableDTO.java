package com.medusa.gruul.shop.api.model.dto;

import cn.hutool.core.collection.CollUtil;
import com.medusa.gruul.common.model.resp.SystemCode;
import com.medusa.gruul.global.model.o.BaseDTO;
import com.medusa.gruul.shop.api.enums.OperaReason;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;

import java.util.Set;

/**
 * @author 张治保
 * date 2022/5/25
 */
@Getter
@Setter
@ToString
@Accessors(chain = true)
public class ShopsEnableDisableDTO implements BaseDTO {

	/**
	 * 是否可用
	 */
	private Boolean enable;

	/**
	 * 店铺id列表
	 */
	private Set<Long> shopIds;

	/**
	 * 操作原因
	 */
	private OperaReason reason;

	@Override
	public void validParam() {
		SystemCode.PARAM_VALID_ERROR.trueThrow(CollUtil.isEmpty(shopIds));
	}
}
