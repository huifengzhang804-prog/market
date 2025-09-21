package com.medusa.gruul.addon.platform.mp.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.medusa.gruul.addon.platform.model.enums.ClientConfigType;
import com.medusa.gruul.common.mp.model.BaseEntity;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

/**
 * <p>
 * 平台商城配置
 * </p>
 *
 * @author 张治保
 * @since 2022-04-25
 */
@Getter
@Setter
@Accessors(chain = true)
@TableName("t_platform_client_config")
public class PlatformClientConfig extends BaseEntity {

    private static final long serialVersionUID = 1L;

    /**
     * 0 平台 1商家 2小程序 3.APP 4.h5
     */
    private ClientConfigType type;

    /**
     * 登录logo
     */
    private String loginLogo;

    /**
     * 登录背景
     */
    private String loginBackground;

    /**
     * 版权声明
     */
    private String copyright;

    /**
     * 标题logo
     */
    private String titleLogo;

    /**
     * 底部logo
     */
    private String bottomLogo;


}
