package com.medusa.gruul.goods.service.model.param;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @Description: 供应商商品param
 * @Author: xiaoq
 * @Date : 2022-04-28 16:01
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class SupplierProductParam extends Page<Object> {

    /**
     * 供应商id
     */
    private String providerId;
}
