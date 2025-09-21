package com.medusa.gruul.addon.freight.model.dto;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.extension.handlers.Fastjson2TypeHandler;
import com.medusa.gruul.addon.freight.model.enums.PostTypeEnum;
import com.medusa.gruul.addon.freight.mp.entity.LogisticsIncludePostage;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

/**
 * @author xiaoq
 * @Description LogisticsIncludePostageDTO.java
 * @date 2022-05-30 14:54
 */
@Data
public class LogisticsIncludePostageDTO implements Serializable {

    private Long id;

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


    public LogisticsIncludePostage coverLogisticsIncludePostage() {
        LogisticsIncludePostage logisticsIncludePostage = new LogisticsIncludePostage();
        BeanUtil.copyProperties(this, logisticsIncludePostage);
        return logisticsIncludePostage;
    }
}
