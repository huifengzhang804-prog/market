package com.medusa.gruul.global.config;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.env.EnvironmentPostProcessor;
import org.springframework.core.env.*;

/**
 * 配置增加 number.add() 函数
 *
 * @author 张治保
 * date 2023/6/28
 */
public class NumberPropertySource extends PropertySource<NumberPropertySource> implements EnvironmentPostProcessor {

	public static final String NAME = "number";
	public static final String PREFIX = "number.";
	public static final String ADD_FUNC_NAME = "add";
	private Environment environment;

	public NumberPropertySource() {
		super(NAME);
	}


	@Override
	public void postProcessEnvironment(ConfigurableEnvironment environment, SpringApplication application) {
		this.environment = environment;
		MutablePropertySources propertySources = environment.getPropertySources();
		propertySources.addAfter(
				StandardEnvironment.SYSTEM_ENVIRONMENT_PROPERTY_SOURCE_NAME,
				this
		);
	}

	@Override
	public Object getProperty(String name) {
		if (!name.startsWith(PREFIX)) {
			return null;
		}
		String function = name.substring(PREFIX.length());
		return evaluate(function);
	}

	private Object evaluate(String function) {
		if (!function.startsWith(ADD_FUNC_NAME)) {
			return null;
		}
		String numbersStr = function.substring(ADD_FUNC_NAME.length() + 1, function.length() - 1);
		String[] numbers = numbersStr.split(",");
		long result = 0L;
		for (String numberStr : numbers) {
			String trim = numberStr.trim();
			Long number = environment.getProperty(trim, Long.class);
			if (number == null) {
				number = Long.parseLong(trim);
			}
			result += number;
		}
		return result;
	}

}
