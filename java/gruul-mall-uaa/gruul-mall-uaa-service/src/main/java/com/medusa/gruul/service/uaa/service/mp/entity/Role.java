package com.medusa.gruul.service.uaa.service.mp.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.medusa.gruul.common.mp.model.BaseEntity;
import com.medusa.gruul.common.security.model.enums.Roles;
import com.medusa.gruul.common.system.model.model.ClientType;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * <p>
 *
 * </p>
 *
 * @author 张治保
 * @since 2022-02-23
 */
@EqualsAndHashCode(callSuper = false)
@Data
@Accessors(chain = true)
@TableName("t_role")
public class Role extends BaseEntity {

	private static final long serialVersionUID = 1L;

	/**
	 * 店铺id
	 */
	private Long shopId;

	/**
	 * 角色值
	 */
	private Roles value;

	/**
	 * 角色名称
	 */
	private String name;

	/**
	 * 角色所属客户端
	 */
	private ClientType clientType;
	/**
	 * 是否启用
	 */
	private Boolean enabled;

}
