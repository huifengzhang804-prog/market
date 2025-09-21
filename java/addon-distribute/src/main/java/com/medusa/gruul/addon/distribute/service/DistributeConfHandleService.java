package com.medusa.gruul.addon.distribute.service;

import com.medusa.gruul.addon.distribute.model.dto.DistributeConfDTO;
import com.medusa.gruul.addon.distribute.mp.entity.DistributeConf;
import io.vavr.control.Option;

/**
 * @author 张治保
 * date 2022/11/15
 */
public interface DistributeConfHandleService {

	/**
	 * 查询分销配置
	 *
	 * @return 分销配置
	 */
	Option<DistributeConf> config();

	/**
	 * 查询分销配置 必须存在
	 *
	 * @return 分销配置
	 */
	DistributeConf configMust();

	/**
	 * 修改分销配追
	 *
	 * @param distributeConf 分销配置详情
	 */
	void updateConf(DistributeConfDTO distributeConf);
}
