package com.medusa.gruul.goods.service.model.vo;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.extension.handlers.Fastjson2TypeHandler;
import com.medusa.gruul.goods.api.model.enums.SupplierStatus;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * @Description: 供应商vo
 * @Author: xiaoq
 * @Date : 2022-04-27 15:14
 */
@Data
public class SupplierVO {
    private Long id;

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
     * 地区
     */
    private String address;

    /**
     * 完整地址
     */
    private String area;

    /**
     * 产品信息
     */
    private String productInfo;


    @TableField(value = "address_code", typeHandler = Fastjson2TypeHandler.class)
    private List<String> addressCode;

    /**
     * 状态(默认待审核，0--已关闭，1--已审核，2--待审核，3--禁用中)
     */
    private SupplierStatus status;

    /**
     * 评分
     */
    private BigDecimal score;


    /**
     * 供应商创建时间
     */
    private LocalDateTime createTime;
}
