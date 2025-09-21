package com.medusa.gruul.common.mp.page;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

import java.util.List;

/**
 * 封装Page分页对象
 *
 * @author WuDi
 * date: 2022/11/7
 * @deprecated 禁用 未来将删除  多余配置
 */
@Deprecated
public class PageBean {
	public static <T, E> Page<T> getPage(Page<E> queryPage, List<T> recordList, long total, long pages) {
		Page<T> page = new Page<>();
		page.setTotal(total)
				.setPages(pages)
				.setSize(queryPage.getSize())
				.setCurrent(queryPage.getCurrent())
				.setRecords(recordList);
		return page;
	}
}
