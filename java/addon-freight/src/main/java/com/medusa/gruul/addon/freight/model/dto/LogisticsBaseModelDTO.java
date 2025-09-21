package com.medusa.gruul.addon.freight.model.dto;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.extension.handlers.Fastjson2TypeHandler;
import com.medusa.gruul.addon.freight.model.enums.ValuationModel;
import com.medusa.gruul.addon.freight.mp.entity.LogisticsBaseModel;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

/**
 * 物流基础文档
 *
 * @author xiaoq
 * @Description LogisticsBaseModelDTO.java
 * @date 2022-05-30 14:54
 */
@Data
public class LogisticsBaseModelDTO {

    private Long id;

    /**
     * 物流运费模板表id
     */
    private Long logisticsId;

    /**
     * 计价方式
     */
    private ValuationModel valuationModel;

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

    public LogisticsBaseModel coverLogisticsBaseModel() {
        LogisticsBaseModel logisticsBaseModel = new LogisticsBaseModel();
        BeanUtil.copyProperties(this, logisticsBaseModel);
        return logisticsBaseModel;
    }
}
