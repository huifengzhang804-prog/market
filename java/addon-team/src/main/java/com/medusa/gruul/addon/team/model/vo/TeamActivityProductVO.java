package com.medusa.gruul.addon.team.model.vo;

import com.alibaba.fastjson2.annotation.JSONField;
import com.medusa.gruul.addon.team.model.enums.TeamStatus;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;

import java.util.List;

/**
 * @author 张治保
 * date 2023/3/29
 */
@Getter
@Setter
@ToString
@Accessors(chain = true)
public class TeamActivityProductVO {

	/**
	 * 活动id
	 */
	private Long activityId;

	/**
	 * 活动名称
	 */
	private String activityName;

	/**
	 * 活动状态
	 */
	private TeamStatus status;

	/**
	 * 活动开始时间
	 */
	private String startTime;

	/**
	 * 活动结束时间
	 */
	private String endTime;

	/**
	 * 店铺ID
	 */
	private Long shopId;

	/**
	 * 商品id
	 */
	private Long productId;

	/**
	 * 商品名称
	 */
	private String productName;

	/**
	 * 商品图片
	 */
	private String productImage;

	/**
	 * 参团人数
	 */
	private Integer userNum;

	/**
	 * 拼团最低价
	 */
	private Long minPrice;

	/**
	 * 所有SKU价格合并
	 */
	@JSONField(serialize = false)
	private List<List<Long>> prices;

	/**
	 * 原价 划线价
	 */
	private Long price;

	/**
	 * 剩余库存
	 */
	private Long stock;
}
