package com.medusa.gruul.addon.freight.mp.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.medusa.gruul.addon.freight.model.enums.LogisticsCompanyStatus;
import com.medusa.gruul.common.mp.model.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 *
 *
 *
 *
 * @author xiaoq
 * @Description 物流配置信息
 * @date 2022-06-06 09:30
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
@TableName("t_fright_conf")
public class FreightConf extends BaseEntity {


    private static final long serialVersionUID = 915637837171740075L;

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
}
