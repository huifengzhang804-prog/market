package com.medusa.gruul.addon.platform.service;

import com.medusa.gruul.addon.platform.model.dto.ClientConfigDTO;
import com.medusa.gruul.addon.platform.model.enums.ClientConfigType;
import com.medusa.gruul.addon.platform.mp.entity.PlatformClientConfig;

/**
 * @author 张治保
 * date 2022/4/25
 */

public interface ClientConfigService {

    /**
     * 根据客户端查询配置信息
     * @param type 配置类型
     * @return 查询结果
     */
    PlatformClientConfig clientConfig(ClientConfigType type);

    /**
     * 新增客户端配置
     * @param clientConfig 配置数据
     */
    void newClientConfig(ClientConfigDTO clientConfig);

    /**
     * 编辑客户端配置
     * @param configId 配置id
     * @param clientConfig 配置详情
     */
    void editClientConfig(Long configId, ClientConfigDTO clientConfig);
}
