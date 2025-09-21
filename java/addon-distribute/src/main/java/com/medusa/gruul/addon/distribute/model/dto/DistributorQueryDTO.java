package com.medusa.gruul.addon.distribute.model.dto;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.medusa.gruul.addon.distribute.model.enums.DistributorStatus;
import com.medusa.gruul.addon.distribute.mp.entity.Distributor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * @author 张治保
 * date 2022/11/16
 */
@Getter
@Setter
@ToString
@Accessors(chain = true)
public class DistributorQueryDTO extends Page<Distributor> implements Serializable {

	/**
	 * 店铺id
	 */
	private Long shopId;

	/**
	 * 姓名
	 */
	private String name;

	/**
	 * 手机号
	 */
	private String mobile;

	/**
	 * 申请开始时间
	 */
	private LocalDateTime startTime;

	/**
	 * 申请结束时间
	 */
	private LocalDateTime endTime;
	
	/**
	 * 状态
	 */
	private DistributorStatus status;
}
