package com.medusa.gruul.overview.api.model;

import com.medusa.gruul.common.model.enums.TransactionType;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.Map;

/**
 * @author 张治保
 * date 2023/6/9
 */

@Getter
@Setter
@ToString
@Accessors(chain = true)
public class OverViewStatementResp implements Serializable {

	/**
	 * 交易类型
	 */
	private TransactionType type;

	/**
	 * 用户id与金额
	 */
	private Map<Long, Long> userIdAmountMap;

}
