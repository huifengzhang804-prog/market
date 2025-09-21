package com.medusa.gruul.addon.distribute.model.vo;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * @author 张治保
 * date 2023/5/24
 */
@Getter
@Setter
@Accessors(chain = true)
@ToString
public class UserRankVO implements Serializable {
	/**
	 * 排名
	 */
	private Long rank;

	/**
	 * 分摊给用户佣金额
	 */
	private Long total;
}
