package com.medusa.gruul.order.api.addon.fullreduction;

import com.medusa.gruul.order.api.BuyerOrder;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.Map;

@Getter
@Setter
@ToString
@Accessors(chain = true)
public class OrderFullReductionParam extends BuyerOrder implements Serializable {

    /**
     * 店铺 对应的商品价格Map
     * key 店铺id ，value Map<商品id，商品总价>
     */
    @NotNull
    @Size(min = 1)
    private Map<Long, Map<Long, Long>> shopProductAmountMap;


}
