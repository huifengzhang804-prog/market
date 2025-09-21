package com.medusa.gruul.addon.freight.service;

import com.kuaidi100.sdk.pojo.HttpResult;
import com.kuaidi100.sdk.response.QueryTrackMapResp;
import com.medusa.gruul.addon.freight.model.param.LogisticsNodeInfoParam;

/**
 * @author xiaoq
 * @Description
 * @date 2022-06-09 18:52
 */
public interface LogisticsNodesService {
    /**
     * 物流节点查询
     *
     * @param logisticsNodeInfoParam 物流节点查询参数
     * @return HttpResult 快递100 返回entity
     */
    HttpResult getLogisticsNode(LogisticsNodeInfoParam logisticsNodeInfoParam);

    /**
     * 物流地图轨迹查询
     *
     * @param logisticsNodeInfoParam 物流地图轨迹查询参数
     * @return QueryTrackMapResp 快递100 返回entity
     */
    QueryTrackMapResp getLogisticsMapTrack(LogisticsNodeInfoParam logisticsNodeInfoParam);

    /**
     * 查询包裹的配送信息
     *
     * @param companyCode
     * @param waybillNo
     * @param recipientsPhone
     */
    Boolean queryPageSignedInfo(String companyCode, String waybillNo, String recipientsPhone);
}
