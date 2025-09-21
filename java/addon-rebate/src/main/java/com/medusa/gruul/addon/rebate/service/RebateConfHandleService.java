package com.medusa.gruul.addon.rebate.service;

import com.medusa.gruul.addon.rebate.model.bo.UserRebatePercent;
import com.medusa.gruul.addon.rebate.model.dto.RebateConfDTO;
import com.medusa.gruul.addon.rebate.mp.entity.RebateConf;
import io.vavr.control.Option;

/**
 * @author jinbu
 */
public interface RebateConfHandleService {


    /**
     * 查询消费返利配置
     *
     * @return 消费返利配置
     */
    RebateConf config();

    /**
     * 获取消费返利配置
     *
     * @return 消费返利配置
     */
    RebateConf getConfig();


    /**
     * 获取消费返利配置
     *
     * @return 消费返利配置
     */
    Option<RebateConf> getConfigOpt();

    /**
     * 编辑消费返利配置
     *
     * @param rebateConfDTO 消费返利配置
     */
    void editRebateConf(RebateConfDTO rebateConfDTO);


    /**
     * 获取当前用户返利比例
     *
     * @param userId 用户id
     * @return 返利比例
     */
    UserRebatePercent getUserRebatePercent(Long userId);

}
