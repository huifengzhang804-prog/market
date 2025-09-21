package com.medusa.gruul.goods.api.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.handlers.Fastjson2TypeHandler;
import com.medusa.gruul.common.mp.model.BaseEntity;
import com.medusa.gruul.goods.api.model.enums.SupplierStatus;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.math.BigDecimal;
import java.util.List;

/**
 * 供应商
 *
 * @author xiaoq
 * @since 2022-03-04
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
@TableName("t_supplier")
public class Supplier extends BaseEntity {

    private static final long serialVersionUID = 1L;


    /**
     * 供应商识别号
     */
    @TableField("supplier_sn")
    private String supplierSn;

    /**
     * 供应商名称
     */
    @TableField("name")
    private String name;

    /**
     * 手机号码
     */
    @TableField("mobile")
    private Long mobile;

    /**
     * 省
     */
    @TableField("province")
    private String province;

    /**
     * 市
     */
    @TableField("city")
    private String city;

    /**
     * 县
     */
    @TableField("country")
    private String country;

    /**
     * 地区
     */
    @TableField("address")
    private String address;

    /**
     * 完整地址
     */
    @TableField("area")
    private String area;

    /**
     * address_code
     */
    @TableField(value = "address_code", typeHandler = Fastjson2TypeHandler.class)
    private List<String> addressCode;


    /**
     * 产品信息
     */
    @TableField("product_info")
    private String productInfo;

    /**
     * 状态(默认待审核，1--已审核，3--禁用中)
     */
    @TableField("status")
    private SupplierStatus status;


    /**
     * 评分
     */
    @TableField("score")
    private BigDecimal score;


}
