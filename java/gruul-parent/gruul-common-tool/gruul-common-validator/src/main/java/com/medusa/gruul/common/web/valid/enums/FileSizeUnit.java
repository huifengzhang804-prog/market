package com.medusa.gruul.common.web.valid.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * 文件size单位
 *
 * @author 张治保
 * date 2023/6/5
 */
@Getter
@RequiredArgsConstructor

public enum FileSizeUnit {
	/**
	 * B
	 */
	B(1),

	/**
	 * KB
	 */
	KB(1024),

	/**
	 * MB
	 */
	MB(1024 * 1024),

	/**
	 * GB
	 */
	GB(1024 * 1024 * 1024);

	private final long size;
}
