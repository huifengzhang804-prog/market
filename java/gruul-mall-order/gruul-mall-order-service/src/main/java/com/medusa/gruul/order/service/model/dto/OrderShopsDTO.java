package com.medusa.gruul.order.service.model.dto;

import cn.hutool.json.JSONObject;
import com.alibaba.fastjson2.annotation.JSONField;
import com.medusa.gruul.common.model.enums.DistributionMode;
import com.medusa.gruul.common.model.enums.OrderType;
import com.medusa.gruul.global.model.o.BaseDTO;
import com.medusa.gruul.order.api.addon.activity.ActivityParam;
import com.medusa.gruul.order.api.enums.OrderSourceType;
import com.medusa.gruul.order.service.model.enums.DiscountType;
import com.medusa.gruul.order.service.model.enums.OrderError;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * @author 张治保
 * date 2022/3/7
 */
@Getter
@Setter
@ToString
public class OrderShopsDTO implements BaseDTO {

    /**
     * 选择的配送方式
     */
    @NotNull
    private DistributionMode distributionMode;

    /**
     * 订单类型
     */
    @NotNull
    private OrderType orderType;

    /**
     * 订单来源
     */
    @NotNull
    private OrderSourceType source;

    /**
     * 收货地址详情
     */
    @Valid
    private ReceiverDTO receiver;

    /**
     * 选购的店铺id
     */
    @NotNull
    @Valid
    @Size(min = 1)
    private List<ShopPackageDTO> shopPackages;

    /**
     * 活动参数
     */
    private ActivityParam activity;

    /**
     * 优惠信息
     * key-》优惠类型  优惠券/满减
     * value-》优惠信息
     */
    private Map<DiscountType, JSONObject> discounts = Collections.emptyMap();

    /**
     * 额外数据
     */
    private JSONObject extra = new JSONObject();

    /**
     * 是否是预计算
     *
     * @ignore
     */
    @JSONField(serialize = false)
    private Boolean budget = Boolean.FALSE;

    /**
     * 参数校验
     */
    @Override
    public void validParam() {
        OrderType orderType = getOrderType();
        //活动需要校验 活动id信息
        if (orderType.isActivity() && (activity == null || activity.getActivityId() == null)) {
            throw new IllegalArgumentException("activityId cannot be null");
        }
        //校验是否是单商品活动
        if (orderType.isOnlySingleProduct()) {
            for (ShopPackageDTO shopPackage : shopPackages) {
                if (shopPackage.getProducts().size() > 1) {
                    throw new IllegalArgumentException("orderType is onlySingleProduct, but shopPackage has more than one product");
                }
            }
        }
        //校验收货地址
        //不需要运费的配送方式不需要校验收货地址
        //预计算也不需要校验收货地址
        if (this.getDistributionMode().isNoFreight() || getBudget()) {
            return;
        }
        int size = getReceiver().getArea().size();
        if (size <2  || size > 3) {
            throw OrderError.RECEIVER_AREA_ERROR.exception();
        }
        //其它情况收货地址不能为空
        if (emptyReceiver()) {
            throw OrderError.RECEIVER_NOT_EXIST.exception();
        }
    }

    /**
     * 是否没有收货人信息
     *
     * @return boolean
     */
    public boolean emptyReceiver() {
        return getReceiver() == null || getReceiver().empty();
    }

    /**
     * 活动id
     *
     * @return Long 活动id
     */
    public Long activityId() {
        return activity == null || activity.getActivityId() == null ? 0L : activity.getActivityId();
    }
}
