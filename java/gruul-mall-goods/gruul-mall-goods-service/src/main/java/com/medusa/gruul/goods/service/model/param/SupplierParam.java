package com.medusa.gruul.goods.service.model.param;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.medusa.gruul.goods.api.model.enums.SupplierStatus;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @Description: 供应商查询 param
 * @Author: xiaoq
 * @Date : 2022-04-27 15:16
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class SupplierParam extends Page<Object> {

    /**
     * 供应商识别号
     */
    private String supplierSn;

    /**
     * 供应商名称
     */
    private String name;

    /**
     * 手机号码
     */
    private String mobile;

    /**
     * 状态(，1--已审核，3--禁用中)
     */
    private SupplierStatus status;
}
