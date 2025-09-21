package com.medusa.gruul.addon.ic.modules.opens.uupt;

import com.medusa.gruul.common.module.app.addon.CommonConfigController;
import com.medusa.gruul.common.module.app.addon.CommonConfigService;
import com.medusa.gruul.common.security.model.enums.Roles;
import com.medusa.gruul.common.security.model.enums.SecureCodes;
import com.medusa.gruul.common.security.resource.helper.ISecurity;
import org.springframework.context.annotation.Configuration;

/**
 * 请求 uri /common/config/intra_city_uupt
 *
 * @author 张治保
 * @see CommonConfigController
 * @since 2024/8/13
 */
@Configuration
public class UuptConfService extends CommonConfigService<UuptConfig> {
    public UuptConfService() {
        super("intra_city_uupt", new UuptConfig());
    }

    @Override
    public void setBefore(UuptConfig config) {
        if (exists()) {
            throw new RuntimeException("配置已存在，无法修改");
        }
        boolean permit = ISecurity.matcher()
                .anyRole(Roles.SUPER_ADMIN, Roles.CUSTOM_ADMIN)
                .match();
        if (!permit) {
            throw SecureCodes.PERMISSION_DENIED.exception();
        }
    }
}
