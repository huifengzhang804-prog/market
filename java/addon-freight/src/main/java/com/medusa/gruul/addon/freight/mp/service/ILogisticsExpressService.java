package com.medusa.gruul.addon.freight.mp.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.medusa.gruul.addon.freight.model.param.LogisticsExpressParam;
import com.medusa.gruul.addon.freight.model.vo.LogisticsExpressExtendVO;
import com.medusa.gruul.addon.freight.model.vo.LogisticsExpressUsableVO;
import com.medusa.gruul.addon.freight.model.vo.LogisticsExpressVO;
import com.medusa.gruul.addon.freight.mp.entity.LogisticsExpress;

/**
 *
 *
 *
 *
 * @author xiaoq
 * @Description
 * @date 2022-06-20 10:40
 */
public interface ILogisticsExpressService extends IService<LogisticsExpress> {

    /**
     * 获取物流服务公司
     *
     * @param logisticsExpressParam 查询参数
     * @return IPage<LogisticsExpressVO>
     */
    IPage<LogisticsExpressVO> getLogisticsExpressList(LogisticsExpressParam logisticsExpressParam);

    /**
     * 获取可以使用的物流服务公司
     *
     * @param logisticsExpressParam 查询参数
     * @return IPage<LogisticsExpressUsableVO>
     */
    IPage<LogisticsExpressUsableVO> getLogisticsExpressUsableList(LogisticsExpressParam logisticsExpressParam);

    /**
     * 根据快递公司code 获取物流服务扩展信息
     *
     * @param expressCompanyCode 快递公司code
     * @return LogisticsExpressExtendVO
     */
    LogisticsExpressExtendVO getLogisticsExpressExtendInfo(String expressCompanyCode);
}
