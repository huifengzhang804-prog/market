package com.medusa.gruul.addon.freight.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.medusa.gruul.addon.freight.model.dto.LogisticsExpressDTO;
import com.medusa.gruul.addon.freight.model.param.LogisticsExpressParam;
import com.medusa.gruul.addon.freight.model.vo.LogisticsExpressUsableVO;
import com.medusa.gruul.addon.freight.model.vo.LogisticsExpressVO;

/**
 *
 *
 *
 *
 * @author xiaoq
 * @Description
 * @date 2022-06-20 09:29
 */

public interface LogisticsExpressInfoService {
    /**
     * 物流服务新增
     *
     * @param logisticsExpressDTO 物流服务公司DTO
     */
    void addLogisticsExpress(LogisticsExpressDTO logisticsExpressDTO);

    /**
     * 物流服务修改
     *
     * @param logisticsExpressDTO 物流服务公司DTO
     */
    void updateLogisticsExpress(LogisticsExpressDTO logisticsExpressDTO);

    /**
     * 物流服务删除
     *
     * @param ids 物流服务公司ids
     */
    void delLogisticsExpress(Long[] ids);

    /**
     * 物流服务列表
     *
     * @param logisticsExpressParam 查询参数<page>
     * @return IPage<LogisticsExpressVO>
     */
    IPage<LogisticsExpressVO> getLogisticsExpressList(LogisticsExpressParam logisticsExpressParam);

    /**
     * 未被禁用的服务列表
     *
     * @param logisticsExpressParam 查询参数<page>
     * @return IPage<LogisticsExpressUsableVO>
     */
    IPage<LogisticsExpressUsableVO> getLogisticsExpressUsableList(LogisticsExpressParam logisticsExpressParam);
}
