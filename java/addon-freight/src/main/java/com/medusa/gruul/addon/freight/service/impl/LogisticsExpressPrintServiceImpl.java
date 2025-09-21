package com.medusa.gruul.addon.freight.service.impl;

import cn.hutool.core.lang.TypeReference;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.google.gson.Gson;
import com.kuaidi100.sdk.api.PrintCloud;
import com.kuaidi100.sdk.contant.ApiInfoConstant;
import com.kuaidi100.sdk.core.IBaseClient;
import com.kuaidi100.sdk.pojo.HttpResult;
import com.kuaidi100.sdk.request.ManInfo;
import com.kuaidi100.sdk.request.PrintCloudParam;
import com.kuaidi100.sdk.request.PrintReq;
import com.kuaidi100.sdk.response.PrintBaseResp;
import com.kuaidi100.sdk.utils.SignUtils;
import com.medusa.gruul.addon.freight.model.dto.LogisticsPrintDTO;
import com.medusa.gruul.addon.freight.model.param.LogisticsPrintParam;
import com.medusa.gruul.addon.freight.model.vo.LogisticsExpressExtendVO;
import com.medusa.gruul.addon.freight.mp.entity.LogisticsPrint;
import com.medusa.gruul.addon.freight.mp.entity.LogisticsSettings;
import com.medusa.gruul.addon.freight.mp.service.ILogisticsExpressService;
import com.medusa.gruul.addon.freight.mp.service.ILogisticsPrintService;
import com.medusa.gruul.addon.freight.mp.service.ILogisticsSettingService;
import com.medusa.gruul.addon.freight.service.LogisticsExpressPrintService;
import com.medusa.gruul.common.model.constant.CommonPool;
import com.medusa.gruul.common.mp.model.BaseEntity;
import com.medusa.gruul.common.security.model.bean.SecureUser;
import com.medusa.gruul.common.security.model.enums.Roles;
import com.medusa.gruul.common.security.resource.helper.ISecurity;
import com.medusa.gruul.global.model.exception.GlobalException;
import com.medusa.gruul.global.model.express.UserAddressDTO;
import com.medusa.gruul.order.api.addon.freight.SendDeliveryDTO;
import com.medusa.gruul.shop.api.enums.SelfShopEnum;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Arrays;

/**
 * 物流面单打印实现
 *
 * @author xiaoq
 * @Description 物流面单打印实现
 * @date 2022-06-17 10:05
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class LogisticsExpressPrintServiceImpl implements LogisticsExpressPrintService {

    private final ILogisticsPrintService logisticsPrintService;

    private final ILogisticsSettingService logisticsSettingService;

    private final ILogisticsExpressService logisticsExpressService;


    /**
     * 打印并发货
     *
     * @param sendDeliveryDTO 打印信息dto
     * @return com.kuaidi100.sdk.response.PrintBaseResp
     */
    @Override
    public PrintBaseResp<JSONObject> printDeliverGoods(SendDeliveryDTO sendDeliveryDTO) {
        LogisticsSettings logisticsSetting = logisticsSettingService.getOne(null);
        if (logisticsSetting == null) {
            throw new GlobalException("快递100打印配置信息不存在");
        }
        LogisticsExpressExtendVO logisticsExpressExtend = logisticsExpressService.getLogisticsExpressExtendInfo(sendDeliveryDTO.getExpressCompanyCode());
        if (logisticsExpressExtend == null) {
            throw new GlobalException("打印物流信息未正确配置");
        }
        // 收货人信息
        ManInfo recManInfo = new ManInfo();
        UserAddressDTO receiver = sendDeliveryDTO.getReceiver();
        recManInfo.setName(receiver.getName());
        recManInfo.setMobile(receiver.getMobile());
        recManInfo.setPrintAddr(receiver.fullAddress());

        // 发货人信息
        ManInfo sendManInfo = new ManInfo();
        UserAddressDTO sender = sendDeliveryDTO.getSender();
        sendManInfo.setName(sender.getName());
        sendManInfo.setMobile(sender.getMobile());
        sendManInfo.setPrintAddr(sender.fullAddress());

        // 打印信息参数封装
        PrintCloudParam printCloudParam = new PrintCloudParam();
        printCloudParam.setKuaidicom(sendDeliveryDTO.getExpressCompanyCode());
        printCloudParam.setCount(CommonPool.NUMBER_ONE + "");
        printCloudParam.setRemark(sendDeliveryDTO.getRemark());
        printCloudParam.setSendMan(sendManInfo);
        printCloudParam.setRecMan(recManInfo);
        printCloudParam.setSiid(logisticsExpressExtend.getDeviceNo());
        printCloudParam.setTempid(logisticsExpressExtend.getPrintTempNo());
        printCloudParam.setWeight(sendDeliveryDTO.getWeight().toString());
        printCloudParam.setOrderId(sendDeliveryDTO.getNo());
        printCloudParam.setCargo("*** 物品");

        printCloudParam.setPartnerId(logisticsExpressExtend.getCustomerCode());
        printCloudParam.setPartnerKey(logisticsExpressExtend.getCustomerPassword());
        printCloudParam.setPartnerSecret("WkB94Nga2r1FZiHkYyT2WHcMazPcsZRY");
        printCloudParam.setType("10");

        String param = new Gson().toJson(printCloudParam);
        String currentTimeMillis = String.valueOf(System.currentTimeMillis());
        PrintReq printReq = new PrintReq();
        printReq.setT(currentTimeMillis);
        printReq.setKey(logisticsSetting.getKey());
        printReq.setMethod(ApiInfoConstant.ELECTRONIC_ORDER_PRINT_METHOD);
        //签名加密
        String s = SignUtils.printSign(param, currentTimeMillis, logisticsSetting.getKey(), logisticsSetting.getSecret());
        printReq.setSign(s);
        printReq.setParam(param);
        IBaseClient baseClient = new PrintCloud();

        HttpResult execute;
        try {
            execute = baseClient.execute(printReq);
        } catch (Exception e) {
            log.error("打印电子面单数据",printReq);
            log.error("打印电子面单失败", e);
            throw new GlobalException(e.getMessage());
        }

        return JSONUtil.parseObj(execute.getBody()).toBean(new TypeReference<PrintBaseResp<JSONObject>>() {
        });
    }

    /**
     * 增加打印机
     *
     * @param logisticsPrint 打印机信息
     */
    @Override
    public void addPrintDevice(LogisticsPrintDTO logisticsPrint) {
        validPrintParam(logisticsPrint);
        LogisticsPrint logisticsPrintInfo = logisticsPrint.coverLogisticsPrint();
        boolean save = logisticsPrintService.save(logisticsPrintInfo);
        if (!save) {
            throw new GlobalException("打印机保存失败");
        }
    }

    /**
     * 打印机信息修改
     *
     * @param logisticsPrint 打印机信息
     */
    @Override
    public void updatePrintDevice(LogisticsPrintDTO logisticsPrint) {
        validPrintParam(logisticsPrint);
        LogisticsPrint logisticsPrintInfo = logisticsPrint.coverLogisticsPrint();
        boolean update = logisticsPrintService.updateById(logisticsPrintInfo);
        if (!update) {
            throw new GlobalException("打印机修改失败");
        }
    }

    /**
     * 校验平台的应用商家勾选框
     *
     * @param logisticsPrint 打印设置入参
     */
    private void validPrintParam(LogisticsPrintDTO logisticsPrint) {
        //平台应用商家必选一个
        boolean isSuperAdmin = ISecurity.matcher().any(SecureUser::getRoles, Roles.SUPER_ADMIN).match();
        if (isSuperAdmin
                && SelfShopEnum.NO.equals(logisticsPrint.getDefSelfShop())
                && SelfShopEnum.NO.equals(logisticsPrint.getDefSelfSupplier())) {
            throw new GlobalException("应用商家至少勾选一个");
        }
    }


    @Override
    public IPage<LogisticsPrint> getPrintDeviceList(LogisticsPrintParam logisticsPrintParam) {
        return logisticsPrintService.lambdaQuery()
                .select(
                        LogisticsPrint::getDeviceNo,
                        BaseEntity::getId,
                        LogisticsPrint::getPrintName,
                        LogisticsPrint::getDefSelfShop,
                        LogisticsPrint::getDefSelfSupplier)
                .orderByAsc(BaseEntity::getCreateTime)
                .page(logisticsPrintParam);
    }

    /**
     * 打印机删除
     *
     * @param ids 打印机ids
     */
    @Override
    public void delPrintDevice(Long[] ids) {
        boolean remove = logisticsPrintService.lambdaUpdate().in(BaseEntity::getId, Arrays.asList(ids)).remove();
        if (!remove) {
            throw new GlobalException("打印机删除失败");
        }
    }
}
