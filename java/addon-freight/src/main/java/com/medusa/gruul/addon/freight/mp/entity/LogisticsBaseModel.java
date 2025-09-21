package com.medusa.gruul.addon.freight.mp.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.handlers.Fastjson2TypeHandler;
import com.medusa.gruul.common.mp.model.BaseEntity;
import lombok.Getter;
import lombok.Setter;

import java.io.Serial;
import java.math.BigDecimal;
import java.util.List;

/**
 * @author xiaoq
 * @Description 物流基础模板
 * @date 2022-05-03 11:47
 */
@Getter
@Setter
@TableName("t_logistics_base_model")
public class LogisticsBaseModel extends BaseEntity {

    @Serial
    private static final long serialVersionUID = -7844094667476001612L;
    /**
     * 店铺id
     */
    private Long shopId;

    /**
     * 物流运费模板表id
     */
    private Long logisticsId;


    /**
     * 首量(件数|重量)
     */
    private String firstQuantity;

    /**
     * 首费
     */
    private BigDecimal firstAmount;

    /**
     * 绪量(件数|重量)
     */
    private String secondQuantity;

    /**
     * 续费
     */
    private BigDecimal secondAmount;

    /**
     * 运送地区 json 格式 {"provinceid": ["cityId","cityId2"]}
     */
    @TableField(typeHandler = Fastjson2TypeHandler.class)
    private List<Object> regionJson;


}
