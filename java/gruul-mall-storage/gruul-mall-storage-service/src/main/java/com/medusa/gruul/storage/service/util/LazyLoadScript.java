package com.medusa.gruul.storage.service.util;

import org.springframework.core.io.ClassPathResource;
import org.springframework.data.redis.core.script.RedisScript;

/**
 * @author 张治保
 * date 2023/3/7
 */
public interface LazyLoadScript {

	/**
	 * 批量 更新库存与销量
	 */
	interface UpdateStockAndSalesScript {
		RedisScript<Long> SCRIPT = RedisScript.of(new ClassPathResource("lua/update-stock-sales-batch.lua"), Long.class);
	}

	/**
	 * 通配符匹配删出
	 */
	interface RemoveStockPatternScript {
		RedisScript<Long> SCRIPT = RedisScript.of(new ClassPathResource("lua/delete-stock-pattern.lua"), Long.class);
	}
}
