package com.medusa.gruul.addon.distribute.model.vo;

import com.baomidou.mybatisplus.core.metadata.IPage;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * @author 张治保
 * date 2023/5/13
 */
@Getter
@Setter
@ToString
@Accessors(chain = true)
public class DistributorOrderPageVO implements Serializable {

	/**
	 * 分页查询结果
	 */
	private IPage<DistributorMainOrderVO> page;

	/**
	 * 订单统计
	 */
	private DistributorOrderBonusStatisticVO statistic;
}

