package com.medusa.gruul.shop.api.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.handlers.Fastjson2TypeHandler;
import com.medusa.gruul.common.geometry.GeometryTypeHandler;
import com.medusa.gruul.common.module.app.shop.ShopMode;
import com.medusa.gruul.common.module.app.shop.ShopStatus;
import com.medusa.gruul.common.module.app.shop.ShopType;
import com.medusa.gruul.common.mp.model.BaseEntity;
import com.medusa.gruul.shop.api.enums.ExtractionType;
import com.medusa.gruul.shop.api.model.dto.ShopExtraDTO;
import com.vividsolutions.jts.geom.Point;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.io.Serial;
import java.time.LocalTime;
import java.util.List;


/**
 * 商家注册信息
 *
 * @author 张治保
 * @since 2022-04-14
 */
@Getter
@Setter
@Accessors(chain = true)
@TableName(value = "t_shop", autoResultMap = true)
public class Shop extends BaseEntity {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 店铺类型
     */
    private ShopMode shopMode;

    /**
     * 公司名称
     */
    private String companyName;

    /**
     * 店铺名称
     */
    @TableField("`name`")
    private String name;

    /**
     * 管理员用户id
     */
    private Long userId;
    /**
     * 管理员手机号
     */
    private String userPhone;

    /**
     * 店铺编号
     */
    @TableField("`no`")
    private String no;

    /**
     * 联系电话
     */
    private String contractNumber;

    /**
     * 状态 0.审核中,  1.正常, -1.禁用, -2审核拒绝
     */
    @TableField("`status`")
    private ShopStatus status;

    /**
     * 店铺类型
     */
    private ShopType shopType;

    /**
     * 抽取类型
     */
    private ExtractionType extractionType;

    /**
     * 抽成百分比
     */
    private Integer drawPercentage;

    /**
     * 定位
     */
    @TableField(typeHandler = GeometryTypeHandler.class)
    private Point location;

    /**
     * 省市区
     */
    @TableField(typeHandler = Fastjson2TypeHandler.class)
    private List<String> area;

    /**
     * 详细地址
     */
    private String address;

    /**
     * logo url
     */
    private String logo;
    /**
     * 介绍
     */
    private String briefing;
    /**
     * 店铺头部背景
     */
    private String headBackground;
    /**
     * 营业开始时间
     */
    @TableField("`start`")
    private LocalTime start;
    /**
     * 营业结束时间
     */
    @TableField("`end`")
    private LocalTime end;
    /**
     * 上新提示
     */
    private String newTips;
    /**
     * 在售且有库存的商品数量
     */
    private Integer onSaleGoodsCount;

    /**
     * 附加数据
     */
    @TableField(typeHandler = Fastjson2TypeHandler.class)
    private ShopExtraDTO extra;

    /**
     * 店铺银行账号信息 表中不存在的字段 放在最下方
     */
    @TableField(exist = false)
    private ShopBankAccount bankAccount;

    /**
     * 店铺评分
     */
    @TableField(exist = false)
    private String score;
}