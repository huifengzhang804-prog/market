package com.medusa.gruul.goods.api.model.param;

import com.alibaba.fastjson2.JSONObject;
import com.medusa.gruul.common.model.base.ShopProductKey;
import jakarta.annotation.Nullable;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * @author 张治保
 * @since 2024/6/25
 */
@Getter
@Setter
@ToString
@Accessors(chain = true)
public class EarningParam implements Serializable {

    /**
     * 当前计算的用户 id
     */
    @Nullable
    private Long userId;

    /**
     * 当前商品 key
     */
    @NotNull
    @Valid
    private ShopProductKey key;

    /**
     * 当前选择的商品总价 销售价* 数量
     */
    @NotNull
    @Min(0)
    private Long curAmount;

    /**
     * 额外信息
     */
    private JSONObject extra;

}
