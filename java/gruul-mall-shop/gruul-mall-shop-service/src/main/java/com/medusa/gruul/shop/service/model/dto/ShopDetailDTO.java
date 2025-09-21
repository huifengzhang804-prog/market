package com.medusa.gruul.shop.service.model.dto;

import com.medusa.gruul.shop.service.model.enums.DetailQueryType;
import com.vividsolutions.jts.geom.Point;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * @author 张治保
 * @since 2024/11/15
 */
@Getter
@Setter
@ToString
@Accessors(chain = true)
public class ShopDetailDTO implements Serializable {

    /**
     * 店铺id
     */
    @NotNull
    private Long shopId;

    /**
     * 查询类型
     */
    @NotNull
    private DetailQueryType type;

    /**
     * 用户定位
     */
    private Point location;


}
