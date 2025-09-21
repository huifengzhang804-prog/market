package com.medusa.gruul.addon.store.model.dto;

import cn.hutool.core.lang.RegexPool;
import com.medusa.gruul.addon.store.mp.entity.ShopStore;
import com.vividsolutions.jts.geom.Point;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;

import java.time.LocalTime;

/**
 * 新增或者修改店铺门店 DTO
 *
 * @author xiaoq
 * @Description 店铺门店DTO
 * @date 2023-03-07 16:41
 */

@Getter
@Setter
@ToString
@Accessors(chain = true)
public class ShopStoreDTO {

    private Long id;

    /**
     * 门店名称
     */
    @NotBlank
    private String storeName;

    /**
     * 门店Logo
     */
    @NotBlank
    @Pattern(regexp = RegexPool.URL_HTTP)
    private String storeLogo;


    /**
     * 门店图片 , 分割
     */
    @NotBlank
    private String storeImg;

    /**
     * 负责人名称
     */
    @NotBlank
    private String functionaryName;

    /**
     * 负责人手机号
     */
    @NotBlank
    @Pattern(regexp = RegexPool.MOBILE)
    private String functionaryPhone;

    /**
     * 营业开始时间
     */
    @NotBlank
    private String businessStartTime;

    /**
     * 营业结束时间
     */
    @NotBlank
    private String businessEndTime;

    /**
     * 定点 经纬度
     */
    @NotNull
    private Point location;

    /**
     * 开始提货时间
     */
    @NotNull
    private Integer startDeliveryDay;

    /**
     * 结束提货时间
     */
    @NotNull
    private Integer endDeliveryDay;

    /**
     * 详细地址
     */
    @NotBlank
    private String detailedAddress;


    public ShopStore coverShopStore() {
        ShopStore shopStore = new ShopStore();
        shopStore.setStoreName(this.storeName);
        shopStore.setStoreLogo(this.storeLogo);
        shopStore.setStoreImg(this.storeImg);
        shopStore.setFunctionaryName(this.functionaryName);
        shopStore.setFunctionaryPhone(this.functionaryPhone);
        shopStore.setLocation(this.location);
        shopStore.setStartDeliveryDay(this.startDeliveryDay);
        shopStore.setEndDeliveryDay(this.endDeliveryDay);
        shopStore.setBusinessStartTime(LocalTime.parse(this.businessStartTime));
        shopStore.setBusinessEndTime(LocalTime.parse(this.businessEndTime));
        shopStore.setDetailedAddress(this.detailedAddress);
        return shopStore;
    }
}
