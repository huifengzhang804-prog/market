package com.medusa.gruul.addon.coupon.model.vo;

import com.medusa.gruul.addon.coupon.model.BaseCouponModel;
import com.medusa.gruul.addon.coupon.model.enums.CouponClaimedStatus;
import com.medusa.gruul.addon.coupon.mp.entity.CouponProduct;
import com.medusa.gruul.global.model.helper.AmountCalculateHelper;
import com.medusa.gruul.search.api.model.SearchCouponProductVO;
import io.vavr.control.Option;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;

/**
 * @author 张治保 date 2022/11/3
 */
@Getter
@Setter
@ToString
@Accessors(chain = true)
public class CouponVO extends BaseCouponModel {

    /**
     * 用户与优惠券对应关系id
     */
    private Long couponUserId;

    /**
     * 店铺名称
     */
    private String shopName;

    /**
     * 优惠券id
     */
    private Long id;

    /**
     * 限领券几日内可用
     */
    private Integer days;

    /**
     * 生效的商品 根据优惠券类型 couponType 判断是否需要这个数据
     */
    private Set<Long> productIds;

    /**
     * 计算出的 优惠金额 目前仅在 结算页使用优惠券
     */
    private Long discountAmount;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 剩余库存
     */
    private Long stock;

    /**
     * 优惠券领取状态
     */
    private CouponClaimedStatus claimedStatus;

    /**
     * 优惠券关联商品
     */
    private List<CouponProduct> couponProductList;

    /**
     * 同一个店铺 按照 无门槛现金券、无门槛折扣券、满减券、满折券 排序
     */
    private Integer rowNum;

    public Long getDiscountAmount() {
        return Option.of(this.discountAmount)
                .map(amount -> AmountCalculateHelper.priceFormat(true, discountAmount))
                .getOrNull();
    }

    public List<SearchCouponProductVO> copyProperties(List<CouponProduct> couponProductList) {
        List<SearchCouponProductVO> couponProducts = new ArrayList<>();
        for (CouponProduct couponProduct : couponProductList) {
            couponProducts.add(new SearchCouponProductVO().setProductId(couponProduct.getProductId()));
        }
        return couponProducts;
    }

}
