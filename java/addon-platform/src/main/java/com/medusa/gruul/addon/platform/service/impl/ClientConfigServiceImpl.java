package com.medusa.gruul.addon.platform.service.impl;

import com.medusa.gruul.addon.platform.model.dto.ClientConfigDTO;
import com.medusa.gruul.addon.platform.model.enums.ClientConfigType;
import com.medusa.gruul.addon.platform.mp.entity.PlatformClientConfig;
import com.medusa.gruul.addon.platform.mp.service.IPlatformClientConfigService;
import com.medusa.gruul.addon.platform.service.ClientConfigService;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;


/**
 * @author 张治保
 * date 2022/4/25
 */
@Service
@RequiredArgsConstructor
public class ClientConfigServiceImpl implements ClientConfigService {

	private final IPlatformClientConfigService platformClientConfigService;

	@Override
	@Cacheable(cacheNames = "platform:client:config", key = "type")
	public PlatformClientConfig clientConfig(ClientConfigType type) {
		return platformClientConfigService.lambdaQuery()
				.eq(PlatformClientConfig::getType, type)
				.one();
	}

	@Override
	@CacheEvict(cacheNames = "platform:client:config", key = "clientConfig.type")
	public void newClientConfig(ClientConfigDTO clientConfig) {
		clientConfig.checkParam();
		clientConfig.saveOrUpdate(null, platformClientConfigService);
	}

	@Override
	@CacheEvict(cacheNames = "platform:client:config", key = "clientConfig.type")
	public void editClientConfig(Long configId, ClientConfigDTO clientConfig) {
		clientConfig.checkParam();
		clientConfig.saveOrUpdate(configId, platformClientConfigService);
	}

}
