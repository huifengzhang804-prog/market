package com.medusa.gruul.common.model;

import com.medusa.gruul.global.i18n.I18NPropertiesLoader;

import java.util.Set;

/**
 * @author 张治保
 * date 2023/6/16
 */
public class ModelI18NLoader implements I18NPropertiesLoader {
	@Override
	public Set<String> paths() {
		return Set.of("i18n/model");
	}
}
