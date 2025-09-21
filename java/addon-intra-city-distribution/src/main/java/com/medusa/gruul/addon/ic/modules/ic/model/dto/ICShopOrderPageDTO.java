package com.medusa.gruul.addon.ic.modules.ic.model.dto;

import com.alibaba.fastjson2.annotation.JSONField;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.medusa.gruul.addon.ic.modules.ic.mp.entity.ICShopOrder;
import com.medusa.gruul.order.api.enums.ICShopOrderStatus;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;

import java.util.Set;

/**
 * @author 张治保
 * @since 2024/8/27
 */
@Getter
@Setter
@Accessors(chain = true)
@ToString(callSuper = true)
public class ICShopOrderPageDTO extends Page<ICShopOrder> {

    /**
     * 订单状态
     */
    @NotNull
    @JSONField(serialize = false)
    private ICShopOrderStatus status;


    /**
     * 根据 同城单号过滤数据
     */
    @JSONField(serialize = false)
    private Set<String> icNos;

}
