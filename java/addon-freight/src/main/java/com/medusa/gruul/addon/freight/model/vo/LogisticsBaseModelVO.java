package com.medusa.gruul.addon.freight.model.vo;

import com.medusa.gruul.addon.freight.model.enums.ValuationModel;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

/**
 *
 * 物流基础模板信息VO
 *
 * @author xiaoq
 * @Description 物流基础模板信息VO
 * @date 2022-05-30 10:46
 */
@Data
public class LogisticsBaseModelVO {

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
    private BigDecimal firstQuantity;

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
    private List<Object> regionJson;
}
