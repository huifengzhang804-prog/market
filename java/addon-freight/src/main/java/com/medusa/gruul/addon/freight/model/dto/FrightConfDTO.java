package com.medusa.gruul.addon.freight.model.dto;

import cn.hutool.core.bean.BeanUtil;
import com.medusa.gruul.addon.freight.model.enums.LogisticsCompanyStatus;
import com.medusa.gruul.addon.freight.mp.entity.FreightConf;
import lombok.Data;

/**
 *
 * 物流公司配置DTO
 *
 *
 * @author xiaoq
 * @Description 物流公司配置DTO
 * @date 2022-06-20 09:06
 */
@Data
public class FrightConfDTO {

    private Long id;

    /**
     * 物流公司名称
     */
    private String logisticsCompanyName;

    /**
     * 物流公司编码
     */
    private String logisticsCompanyCode;


    /**
     * 打印模板编号
     */
    private String printTempNo;

    /**
     * 物流公司状态
     */
    private LogisticsCompanyStatus logisticsCompanyStatus;


    public FreightConf coverFrightConf() {
        FreightConf freightConf = new FreightConf();
        BeanUtil.copyProperties(this, freightConf);
        return freightConf;
    }
}
