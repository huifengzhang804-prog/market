package com.medusa.gruul.cart.service.model.bo;

import com.medusa.gruul.global.model.exception.GlobalException;
import com.medusa.gruul.goods.api.entity.Product;
import com.medusa.gruul.shop.api.model.vo.ShopInfoVO;
import com.medusa.gruul.storage.api.entity.StorageSku;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;

/**
 * <p></p>
 *
 * @author 张治保
 * date 2022/7/25
 */
@Getter
@Setter
@ToString
@Accessors(chain = true)
public class ShopProductSkuValid {
    /**
     * 校验不通过的异常
     */
    private GlobalException exception;

    /**
     * 店铺数据
     */
    private ShopInfoVO shopInfo;
    /**
     * 商品数据
     */
    private Product productInfo;
    /**
     * 仓储sku数据
     */
    private StorageSku storageSku;

}
