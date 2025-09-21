package com.medusa.gruul.service.uaa.service.model.dto;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.medusa.gruul.common.system.model.model.ClientType;
import com.medusa.gruul.service.uaa.service.model.vo.UserAnchorVO;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;

/**
 * @author 张治保
 * date 2022/5/24
 */
@Getter
@Setter
@ToString
@Accessors(chain = true)
public class AvailableUserPageDTO extends Page<UserAnchorVO> {

	/**
	 * 关键词 手机号 用户名
	 */
	private String keywords;

	/**
	 * 排除在外的店铺 id
	 */
	private Long excludeShopId;

	/**
	 * 客户端
	 */
	private ClientType clientType;
}
