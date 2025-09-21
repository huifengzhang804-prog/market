package com.medusa.gruul.addon.freight.addon;

import com.medusa.gruul.order.api.addon.freight.PlatformFreightParam;
import com.medusa.gruul.order.api.addon.freight.SendDeliveryDTO;
import org.springframework.web.bind.annotation.RequestBody;

import java.math.BigDecimal;
import java.util.Map;

/**
 * Provider
 *
 * @author xiaoq
 * @Description AddonFreightProvider
 * @date 2023-05-08 16:51
 */
public interface AddonFreightProvider {
    /**
     * 运费计算
     *
     * @param platformFreight 运费计算param
     * @return <Map<key sopId+":"+物流模板id ,value 当前模板价格>>
     */
    Map<String, BigDecimal> freightCalculation(@RequestBody PlatformFreightParam platformFreight);

    /**
     * 打印并发货
     *
     * @param sendDeliveryDTO 打印数据
     * @return 电子面单单号
     */
    String printDeliverGoods(@RequestBody SendDeliveryDTO sendDeliveryDTO);

    /**
     * 查询物流签收状态
     *
     * @param companyCode     物流公司
     * @param waybillNo       物流单号
     * @param recipientsPhone 签收人手机号
     * @return
     */
    Boolean queryPackageSignedInfo(String companyCode, String waybillNo, String recipientsPhone);
}
