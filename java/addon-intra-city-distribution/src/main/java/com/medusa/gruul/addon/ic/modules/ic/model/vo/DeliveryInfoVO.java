package com.medusa.gruul.addon.ic.modules.ic.model.vo;

import com.vividsolutions.jts.geom.Point;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;

import java.util.List;


/**
 * @author 张治保
 * @since 2024/8/28
 */
@Getter
@Setter
@ToString
@Accessors(chain = true)
public class DeliveryInfoVO {

    /**
     * 收货人位置
     */
    private Point receiverLocation;

    /**
     * 配送历史 用户查询只有一条，管理端查询会有多条数据
     */
    private List<ShopOrderInfo> orders;

}
