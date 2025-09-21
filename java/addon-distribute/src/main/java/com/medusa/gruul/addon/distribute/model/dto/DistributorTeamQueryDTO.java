package com.medusa.gruul.addon.distribute.model.dto;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.medusa.gruul.addon.distribute.model.enums.Level;
import com.medusa.gruul.addon.distribute.mp.entity.Distributor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * @author 张治保
 * date 2022/11/22
 */
@Getter
@Setter
@ToString
public class DistributorTeamQueryDTO extends Page<Distributor> {

	/**
	 * 分销商用户id
	 */
	private Long userId;

	/**
	 * 查询的分销员等级
	 */
	private Level level;

	/**
	 * 一级分销员数量
	 */
	private Long count1;

	/**
	 * 所属二级分销员数量
	 */
	private Long count2;

	/**
	 * 所属三级分销员数量
	 */
	private Long count3;
}
