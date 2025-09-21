package com.medusa.gruul.addon.freight.mp.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.medusa.gruul.addon.freight.model.param.LogisticsExpressParam;
import com.medusa.gruul.addon.freight.model.vo.LogisticsExpressExtendVO;
import com.medusa.gruul.addon.freight.model.vo.LogisticsExpressUsableVO;
import com.medusa.gruul.addon.freight.model.vo.LogisticsExpressVO;
import com.medusa.gruul.addon.freight.mp.entity.LogisticsExpress;
import org.apache.ibatis.annotations.Param;

/**
 * @author xiaoq
 * @Description LogisticsExpressMapper.java
 * @date 2022-06-20 10:42
 */
public interface LogisticsExpressMapper extends BaseMapper<LogisticsExpress> {

    /**
     * 获取物流服务列表
     *
     * @param logisticsExpressParam 查询参数
     * @return IPage<LogisticsExpressVO>
     */
    IPage<LogisticsExpressVO> queryLogisticsExpressList(@Param("logisticsExpressParam") LogisticsExpressParam logisticsExpressParam);

    /**
     * 获取可用的物流服务列表
     *
     * @param logisticsExpressParam 查询参数
     * @return IPage<LogisticsExpressUsableVO>
     */
    IPage<LogisticsExpressUsableVO> queryLogisticsExpressUsableList(@Param("logisticsExpressParam") LogisticsExpressParam logisticsExpressParam);

    /**
     * 物流服务扩展信息
     *
     * @param expressCompanyCode 快递公司code
     * @param shopId             店铺id
     * @return 物流服务扩展信息
     */
    LogisticsExpressExtendVO queryLogisticsExpressExtendInfo(@Param("expressCompanyCode") String expressCompanyCode, @Param("shopId") Long shopId);
}
