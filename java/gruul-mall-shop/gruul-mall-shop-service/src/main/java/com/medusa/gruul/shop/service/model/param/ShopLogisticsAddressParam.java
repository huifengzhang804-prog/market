package com.medusa.gruul.shop.service.model.param;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.medusa.gruul.shop.api.entity.ShopLogisticsAddress;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @author xiaoq
 * @Description 店铺物流地址查询Param
 * @date 2023-05-08 14:16
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class ShopLogisticsAddressParam extends Page<ShopLogisticsAddress> {

}
