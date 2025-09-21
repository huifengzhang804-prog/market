package com.medusa.gruul.common.system.model.remote;

import cn.hutool.core.util.StrUtil;
import com.medusa.gruul.common.system.model.ISystem;
import com.medusa.gruul.common.system.model.constant.SystemHeaders;
import com.medusa.gruul.common.system.model.model.ClientType;
import com.medusa.gruul.common.system.model.model.Platform;
import com.medusa.gruul.common.system.model.model.Systems;
import io.vavr.control.Option;

import java.util.HashMap;
import java.util.Map;

/**
 * @author 张治保
 * date 2022/4/19
 */
public class HeaderRender {
	private HeaderRender() {
	}

	public static Map<String, String> requestHeaders() {
		Option<Systems> systemsOpt = ISystem.systemOpt();
		Map<String, String> headers = new HashMap<>(16);
		if (systemsOpt.isEmpty()) {
			return headers;
		}
		Systems systems = systemsOpt.get();
		/*
		 * ip地址
		 */
		String ip = systems.getIp();
		if (StrUtil.isNotBlank(ip)) {
			headers.put(SystemHeaders.IP, ip);
		}
		/*
		 * 设备id
		 */
		String deviceId = systems.getDeviceId();
		if (StrUtil.isNotBlank(deviceId)) {
			headers.put(SystemHeaders.DEVICE_ID, deviceId);
		}
		/*
		 * 客户端类型
		 */
		ClientType clientType = systems.getClientType();
		if (clientType != null) {
			headers.put(SystemHeaders.CLIENT_TYPE, clientType.name());
		}
		/*
		 * 客户端类型
		 */
		Platform platform = systems.getPlatform();
		if (platform != null) {
			headers.put(SystemHeaders.PLATFORM, platform.name());
		}
		/*
		 * api版本号
		 */
		String version = systems.getVersion();
		if (StrUtil.isNotBlank(version)) {
			headers.put(SystemHeaders.VERSION, version);
		}
		/*
		 * 服务商id
		 */
		Long platformId = systems.getPlatformId();
		if (platformId != null) {
			headers.put(SystemHeaders.PROVIDER_ID, String.valueOf(platformId));
		}
		/*
		 * 店铺id
		 */
		Long shopId = systems.getShopId();
		if (shopId != null) {
			headers.put(SystemHeaders.SHOP_ID, String.valueOf(shopId));
		}

		return headers;
	}
}
