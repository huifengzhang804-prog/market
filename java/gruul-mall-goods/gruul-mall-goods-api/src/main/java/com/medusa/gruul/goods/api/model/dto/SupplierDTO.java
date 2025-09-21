package com.medusa.gruul.goods.api.model.dto;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.lang.RegexPool;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.extension.handlers.Fastjson2TypeHandler;
import com.medusa.gruul.goods.api.entity.Supplier;
import com.medusa.gruul.goods.api.model.enums.SupplierStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

/**
 * @Description: 新增或修改供应商DTO
 * @Author: xiaoq
 * @Date : 2022-03-14 17:44
 */
@Data
public class SupplierDTO {

    private Long id;

    /**
     * 供应商识别号
     */
    private String supplierSn;

    /**
     * 供应商名称
     */
    @NotBlank(message = "供应商名称不可为空")
    private String name;

    /**
     * 手机号码
     */
    @Pattern(regexp = RegexPool.MOBILE)
    private String mobile;

    /**
     * 省
     */
    private String province;

    /**
     * 市
     */
    private String city;

    /**
     * 县
     */
    private String country;

    /**
     * 完整地址
     */
    private String area;

    /**
     * 地区
     */
    private String address;

    /**
     * List<code0,code1,code2>
     */
    @TableField(value = "address_code", typeHandler = Fastjson2TypeHandler.class)
    private List<String> addressCode;


    /**
     * 产品信息
     */
    private String productInfo;

    /**
     * 状态(默认待审核，1--已审核，3--禁用中)
     */
    private SupplierStatus status;

    /**
     * 评分
     */
    private BigDecimal score;


    public Supplier coverSupplier() {
        Supplier supplier = new Supplier();
        BeanUtil.copyProperties(this, supplier);
        return supplier;
    }

}
