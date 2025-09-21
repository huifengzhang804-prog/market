package com.medusa.gruul.shop.api.model.vo;


import com.medusa.gruul.common.module.app.shop.ShopMode;
import com.medusa.gruul.common.module.app.shop.ShopStatus;
import com.medusa.gruul.common.module.app.shop.ShopType;
import com.medusa.gruul.goods.api.model.vo.ProductVO;
import com.medusa.gruul.search.api.model.SearchCouponVO;
import com.medusa.gruul.shop.api.entity.Shop;
import com.medusa.gruul.shop.api.enums.ExtractionType;
import com.medusa.gruul.shop.api.model.dto.ShopIcDistributeInfoDTO;
import com.vividsolutions.jts.geom.Point;
import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

/**
 * @author wudi
 */
@Data
@Accessors(chain = true)
public class ShopInfoVO implements Serializable {

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
     * 距离
     */
    private Double distance;

    /**
     * '起送费'
     */
    private BigDecimal initialDeliveryCharge;

    /**
     * 销量
     */
    private Long salesVolume;

    /**
     * 定点经纬度
     */
    private Point location;

    /**
     * 联系地址
     */
    private String address;

    /**
     * 抽取类型
     */
    private ExtractionType extractionType;

    /**
     * 抽成百分比
     */
    private Integer drawPercentage;

    /**
     * 优惠券
     */
    private List<SearchCouponVO> couponList;

    /**
     * 商品信息
     */
    private List<ProductVO> productList;

    /**
     * 关注人数
     */
    private Long followCount;

    /**
     * 当前用户是否已关注
     */
    private Boolean isFollow;

    /**
     * 店铺综合体验评分
     */
    private String score;
    /**
     * 同城配送相关信息
     */
    private ShopIcDistributeInfoDTO icDistributeInfo;


    public static ShopInfoVO fromShop(Shop shop) {
        return new ShopInfoVO()
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
