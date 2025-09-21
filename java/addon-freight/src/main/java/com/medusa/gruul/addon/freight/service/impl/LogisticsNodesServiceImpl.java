package com.medusa.gruul.addon.freight.service.impl;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson2.JSON;
import com.kuaidi100.sdk.api.QueryTrack;
import com.kuaidi100.sdk.core.IBaseClient;
import com.kuaidi100.sdk.pojo.HttpResult;
import com.kuaidi100.sdk.request.QueryTrackParam;
import com.kuaidi100.sdk.request.QueryTrackReq;
import com.kuaidi100.sdk.response.QueryTrackData;
import com.kuaidi100.sdk.response.QueryTrackMapResp;
import com.kuaidi100.sdk.response.QueryTrackResp;
import com.kuaidi100.sdk.utils.SignUtils;
import com.medusa.gruul.addon.freight.model.enums.SequenceEnum;
import com.medusa.gruul.addon.freight.model.param.LogisticsNodeInfoParam;
import com.medusa.gruul.addon.freight.mp.entity.LogisticsSettings;
import com.medusa.gruul.addon.freight.mp.service.ILogisticsSettingService;
import com.medusa.gruul.addon.freight.service.LogisticsNodesService;
import com.medusa.gruul.common.model.constant.CommonPool;
import com.medusa.gruul.common.system.model.ISystem;
import com.medusa.gruul.common.system.model.model.ClientType;
import com.medusa.gruul.global.model.exception.GlobalException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * @author xiaoq
 * @Description
 * @date 2022-06-09 18:52
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class LogisticsNodesServiceImpl implements LogisticsNodesService {


    private final QueryTrack queryTrack;
    private final ILogisticsSettingService logisticsSettingService;


    /**
     * 查询参数封装
     *
     * @param logisticsNodeInfoParam 物流节点查询参数
     * @return com.kuaidi100.sdk.request.QueryTrackParam
     */
    private QueryTrackParam init(LogisticsNodeInfoParam logisticsNodeInfoParam) {
        QueryTrackParam queryTrackParam = new QueryTrackParam();
        queryTrackParam.setCom(logisticsNodeInfoParam.getCompanyCode());
        queryTrackParam.setNum(logisticsNodeInfoParam.getWaybillNo());
        //顺丰查询需要手机号
        queryTrackParam.setPhone(StrUtil.isEmpty(logisticsNodeInfoParam.getRecipientsPhone()) ? "" : logisticsNodeInfoParam.getRecipientsPhone());
        //默认升序
        queryTrackParam.setOrder(ObjectUtil.isEmpty(logisticsNodeInfoParam.getOrder()) ? SequenceEnum.ASC.getValue() : logisticsNodeInfoParam.getOrder().getValue());
        return queryTrackParam;
    }

    /**
     * 物流节点查询
     *
     * @param logisticsNodeInfoParam 物流节点查询参数
     * @return HttpResult 快递100 返回entity
     */
    @Override
    public HttpResult getLogisticsNode(LogisticsNodeInfoParam logisticsNodeInfoParam) {
        LogisticsSettings logisticsSettingInfo = logisticsSettingService.lambdaQuery().one();
        if (logisticsSettingInfo == null) {
            ClientType clientType = ISystem.clientTypeMust();
            if (clientType != ClientType.CONSUMER) {
                throw new GlobalException("请检查物流配置详情");
            } else {
                return new HttpResult();
            }
        }
        QueryTrackParam queryTrackParam = init(logisticsNodeInfoParam);
        //行政区域解析功能 返回节点状态
        queryTrackParam.setResultv2(String.valueOf(CommonPool.NUMBER_ONE));
        String param = JSON.toJSONString(queryTrackParam);

        QueryTrackReq queryTrackReq = new QueryTrackReq();
        queryTrackReq.setParam(param);
        queryTrackReq.setCustomer(logisticsSettingInfo.getCustomer());

        //签名加密
        queryTrackReq.setSign(SignUtils.querySign(param, logisticsSettingInfo.getKey(), logisticsSettingInfo.getCustomer()));
        IBaseClient baseClient = new QueryTrack();
        try {
            HttpResult execute = baseClient.execute(queryTrackReq);
            log.error("---------------------------------------");
            log.error("{}", execute);
            log.error("---------------------------------------");
            return execute;
        } catch (Exception e) {
            throw new GlobalException(e.getMessage());
        }
    }

    /**
     * 物流地图轨迹查询
     *
     * @param logisticsNodeInfoParam 物流地图轨迹查询参数
     * @return QueryTrackMapResp 快递100 返回entity
     */
    @Override
    public QueryTrackMapResp getLogisticsMapTrack(LogisticsNodeInfoParam logisticsNodeInfoParam) {
        LogisticsSettings logisticsSettingInfo = logisticsSettingService.lambdaQuery().one();
        QueryTrackParam queryTrackParam = init(logisticsNodeInfoParam);
        queryTrackParam.setFrom(logisticsNodeInfoParam.getFrom());
        queryTrackParam.setTo(logisticsNodeInfoParam.getTo());
        String param = JSON.toJSONString(queryTrackParam);

        QueryTrackReq queryTrackReq = new QueryTrackReq();
        queryTrackReq.setParam(param);
        queryTrackReq.setCustomer(logisticsSettingInfo.getCustomer());
        //签名加密
        queryTrackReq.setSign(SignUtils.querySign(param, logisticsSettingInfo.getKey(), logisticsSettingInfo.getCustomer()));
        QueryTrackMapResp queryTrackMapResp;
        try {
            HttpResult result = queryTrack.execute(queryTrackReq);
            queryTrackMapResp = JSON.parseObject(result.getBody(), QueryTrackMapResp.class);
        } catch (Exception e) {
            throw new GlobalException(e.getMessage());
        }
        return queryTrackMapResp;

    }

    @Override
    public Boolean queryPageSignedInfo(String companyCode, String waybillNo, String recipientsPhone) {
        LogisticsSettings logisticsSettingInfo = logisticsSettingService.lambdaQuery().one();
        if (logisticsSettingInfo == null) {
            throw new GlobalException("请检查物流配置详情");
        }
        LogisticsNodeInfoParam nodeInfoParam = new LogisticsNodeInfoParam();
        nodeInfoParam.setCompanyCode(companyCode);
        nodeInfoParam.setWaybillNo(waybillNo);
        nodeInfoParam.setRecipientsPhone(recipientsPhone);
        QueryTrackParam queryTrackParam = init(nodeInfoParam);
        //行政区域解析功能 返回节点状态
        queryTrackParam.setResultv2(String.valueOf(CommonPool.NUMBER_ONE));
        String param = JSON.toJSONString(queryTrackParam);

        QueryTrackReq queryTrackReq = new QueryTrackReq();
        queryTrackReq.setParam(param);
        queryTrackReq.setCustomer(logisticsSettingInfo.getCustomer());

        //签名加密
        queryTrackReq.setSign(SignUtils.querySign(param, logisticsSettingInfo.getKey(), logisticsSettingInfo.getCustomer()));
        IBaseClient baseClient = new QueryTrack();
        try {
            HttpResult frightResult = baseClient.execute(queryTrackReq);
            log.error("---------------------------------------");
            log.error("{}", frightResult);
            log.error("---------------------------------------");
            if (frightResult.getStatus() == 200) {
                //查询到了物流轨迹
                QueryTrackResp queryTrackResp = JSON.parseObject(frightResult.getBody(), QueryTrackResp.class);
                //已经签收
                if ("3".equals(queryTrackResp.getStatus())) {
                    //获取最后一条物流节点数据
                    QueryTrackData queryTrackData = queryTrackResp.getData().get(queryTrackResp.getData().size() - 1);
                    if ("签收".equals(queryTrackData.getStatus())) {
                        return Boolean.TRUE;
                    }
                }
            }

        } catch (Exception e) {
            log.info("查询物流签收信息失败 {}", e);
            throw new GlobalException(e.getMessage());
        }
        return Boolean.FALSE;
    }
}
