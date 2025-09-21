package com.medusa.gruul.addon.distribute.model.dto;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.medusa.gruul.addon.distribute.mp.entity.Distributor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * @author 张治保
 * date 2023/5/12
 */
@Getter
@Setter
@ToString
public class DistributorRankDTO extends Page<Distributor> {

	/**
	 * 用户id
	 */
	private Long userId;

	/**
	 * 店铺id
	 */
	private Long shopId;

}
