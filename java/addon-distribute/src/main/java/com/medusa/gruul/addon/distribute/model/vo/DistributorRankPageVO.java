package com.medusa.gruul.addon.distribute.model.vo;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.medusa.gruul.addon.distribute.mp.entity.Distributor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;

/**
 * @author 张治保
 * date 2023/5/12
 */
@Getter
@Setter
@ToString
@Accessors(chain = true)
public class DistributorRankPageVO extends Page<Distributor> {

	/**
	 * 查询用户的排名
	 */
	private UserRankVO rank;
}
