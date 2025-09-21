package com.medusa.gruul.order.api.addon.freight;

import com.medusa.gruul.common.model.enums.DistributionMode;
import com.vividsolutions.jts.geom.Point;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

/**
 * 运费计算Dto
 *
 * @author xiaoq
 * @Description 运费计算param
 * @date 2022-06-07 16:09
 */
@Data
public class PlatformFreightParam implements Serializable {

    @Serial
    private static final long serialVersionUID = 5175345820490189210L;


    /**
     * 收货人定位
     */
    private Point location;

    /**
     * 省市区编码
     */
    @NotEmpty(message = "请选择省市区")
    private List<String> area;

    /**
     * 详细地址
     */
    private String address;

    /**
     * 店铺运费模板计算信息
     */
    private List<ShopFreightParam> shopFreights;


    /**
     * 是否有免运费权益
     */
    private Boolean freeRight;

    /**
     * 配送方式
     */
    private List<DistributionMode> distributionMode;

}
