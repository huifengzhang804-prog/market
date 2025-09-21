package com.medusa.gruul.addon.freight.mp.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.handlers.Fastjson2TypeHandler;
import com.medusa.gruul.addon.freight.model.enums.PostTypeEnum;
import com.medusa.gruul.common.mp.model.BaseEntity;
import lombok.Getter;
import lombok.Setter;

import java.io.Serial;
import java.math.BigDecimal;
import java.util.List;

/**
 * 运费模板包邮设置信息
 *
 * @author xiaoq
 * @Description 运费模板包邮设置信息
 * @date 2022-05-26 10:00
 */
@Getter
@Setter
@TableName("t_logistics_include_postage")
public class LogisticsIncludePostage extends BaseEntity {


    @Serial
    private static final long serialVersionUID = 3378844778400393810L;

    /**
     * 店铺id
     */
    private Long shopId;

    /**
     * 物流运费模板表id
     */
    private Long logisticsId;

    /**
     * 邮寄类型
     */
    private PostTypeEnum postType;

    /**
     * 包邮件数
     */
    private Integer pieceNum;

    /**
     * 包邮重量 单位: 千克(kg)
     */
    private BigDecimal weight;


    /**
     * 包邮金额
     */
    private BigDecimal amountNum;


    /**
     * 生效区域
     */
    @TableField(typeHandler = Fastjson2TypeHandler.class)
    private List<Object> region;
}
