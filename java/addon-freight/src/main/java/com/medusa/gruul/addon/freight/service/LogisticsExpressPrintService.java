package com.medusa.gruul.addon.freight.service;

import cn.hutool.json.JSONObject;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.kuaidi100.sdk.response.PrintBaseResp;
import com.medusa.gruul.addon.freight.model.dto.LogisticsPrintDTO;
import com.medusa.gruul.addon.freight.model.param.LogisticsPrintParam;
import com.medusa.gruul.addon.freight.mp.entity.LogisticsPrint;
import com.medusa.gruul.order.api.addon.freight.SendDeliveryDTO;

/**
 *
 *  物流面单打印
 *
 *
 * @author xiaoq
 * @Description 物流面单打印
 * @date 2022-06-17 10:05
 */
public interface LogisticsExpressPrintService {

    /**
     * 打印并发货
     *
     * @param sendDeliveryDTO 打印信息dto
     * @return  com.kuaidi100.sdk.response.PrintBaseResp
     */
    PrintBaseResp<JSONObject> printDeliverGoods(SendDeliveryDTO sendDeliveryDTO);

    /**
     * 打印机新增
     * @param logisticsPrint 打印机信息
     */
    void addPrintDevice(LogisticsPrintDTO logisticsPrint);

    /**
     * 打印机修改
     *
     * @param logisticsPrintDTO 打印机信息
     */
    void updatePrintDevice(LogisticsPrintDTO logisticsPrintDTO);

    /**
     * 打印机列表
     *
     * @param logisticsPrintParam 查询参数
     * @return 分页 LogisticsPrintVO
     */
    IPage<LogisticsPrint> getPrintDeviceList(LogisticsPrintParam logisticsPrintParam);

    /**
     * 打印机删除
     * @param ids 打印机ids
     */
    void delPrintDevice(Long[] ids);
}
