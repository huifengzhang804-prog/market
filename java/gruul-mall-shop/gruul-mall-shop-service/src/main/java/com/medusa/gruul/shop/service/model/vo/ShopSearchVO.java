package com.medusa.gruul.shop.service.model.vo;

import com.medusa.gruul.common.module.app.shop.ShopMode;
import com.medusa.gruul.common.module.app.shop.ShopStatus;
import com.medusa.gruul.common.module.app.shop.ShopType;
import com.medusa.gruul.search.api.model.ProductVO;
import com.medusa.gruul.shop.api.entity.Shop;
import com.medusa.gruul.shop.api.enums.ExtractionType;
import com.vividsolutions.jts.geom.Point;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.List;

/**
 * @author 张治保
 * @since 2024/3/26
 */
@Getter
@Setter
@Accessors(chain = true)
@ToString
public class ShopSearchVO implements Serializable {

    /**
     * 店铺id
     */
    private Long id;

    /**
     * 店铺运营模式
     */
    private ShopMode shopMode;

    /**
     * 店铺类型
     */
    private ShopType shopType;

    /**
     * 店铺名称
     */
    private String name;

    /**
     * 店铺logo
     */
    private String logo;

    /**
     * 联系方式
     */
    private String contractNumber;

    /**
     * 上新提示
     */
    private String newTips;

    /**
     * 是否可用
     */
    private ShopStatus status;

    /**
     * 店铺头部背景
     */
    private String headBackground;

    /**
     * 定点经纬度
     */
    private Point location;

    /**
     * 抽取类型
     */
    private ExtractionType extractionType;

    /**
     * 抽成百分比
     */
    private Integer drawPercentage;

    /**
     * 商品信息
     */
    private List<ProductVO> products;


    /**
     * 店铺综合体验评分
     */
    private String score;


    public static ShopSearchVO fromShop(Shop shop) {
        return new ShopSearchVO()
                .setId(shop.getId())
                .setShopMode(shop.getShopMode())
                .setShopType(shop.getShopType())
                .setName(shop.getName())
                .setContractNumber(shop.getContractNumber())
                .setLogo(shop.getLogo())
                .setNewTips(shop.getNewTips())
                .setStatus(shop.getStatus())
                .setDrawPercentage(shop.getDrawPercentage())
                .setExtractionType(shop.getExtractionType())
                .setLocation(shop.getLocation())
                .setHeadBackground(shop.getHeadBackground());
    }
}
