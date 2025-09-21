package com.medusa.gruul.shop.service.model.dto;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.medusa.gruul.common.module.app.shop.ShopMode;
import com.medusa.gruul.common.module.app.shop.ShopStatus;
import com.medusa.gruul.common.module.app.shop.ShopType;
import com.medusa.gruul.shop.api.enums.ExtractionType;
import java.util.Set;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;


/**
 * @author 张治保
 * date 2022/4/18
 */
@Getter
@Setter
@ToString
public class ShopQueryNoPageDTO{
    /**
     * 商户id
     */
    private String no;
    /**
     * 商户名称
     */
    private String name;
    /**
     * 商户状态
     */
    private ShopStatus status;

    /**
     * 店铺类型
     */
    private ShopType shopType;

    /**
     * 店铺运营模式
     */
    private Set<ShopMode> shopModes;

    /**
     * 抽佣类型
     */
    private ExtractionType extractionType;

    /**
     * 经度
     */
    private Double longitude;

    /**
     * 纬度
     */
    private Double latitude;

    /**
     * 有商品的店铺id集合
     */
    private Set<Long> shopIds;

    /**
     * 有商品的店铺
     */
    private Boolean productIsNotEmpty = Boolean.FALSE;


}
