package com.medusa.gruul.addon.ic.opens.judanke;

import cn.hutool.core.util.IdUtil;
import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONReader;
import com.alibaba.fastjson2.PropertyNamingStrategy;
import com.alibaba.fastjson2.filter.NameFilter;
import com.medusa.gruul.addon.ic.modules.opens.judanke.JudankeConfig;
import com.medusa.gruul.addon.ic.modules.opens.judanke.JudankeResponse;
import com.medusa.gruul.addon.ic.modules.opens.judanke.api.IJudankeApiFactory;
import com.medusa.gruul.addon.ic.modules.opens.judanke.core.JudankeApiFactory;
import com.medusa.gruul.addon.ic.modules.opens.judanke.model.enums.DeliveryBrand;
import com.medusa.gruul.addon.ic.modules.opens.judanke.model.enums.GoodsTypeId;
import com.medusa.gruul.addon.ic.modules.opens.judanke.model.enums.PayMethod;
import com.medusa.gruul.addon.ic.modules.opens.judanke.model.enums.Switch;
import com.medusa.gruul.addon.ic.modules.opens.judanke.model.finance.BetBalanceResp;
import com.medusa.gruul.addon.ic.modules.opens.judanke.model.finance.GetRechargeQrcodeParam;
import com.medusa.gruul.addon.ic.modules.opens.judanke.model.finance.GetRechargeQrcodeResp;
import com.medusa.gruul.addon.ic.modules.opens.judanke.model.merchant.BindMerchantParam;
import com.medusa.gruul.addon.ic.modules.opens.judanke.model.merchant.BindMerchantResp;
import com.medusa.gruul.addon.ic.modules.opens.judanke.model.merchant.SendVerifyCodeParam;
import com.medusa.gruul.addon.ic.modules.opens.judanke.model.order.*;
import com.medusa.gruul.addon.ic.modules.opens.judanke.model.shop.CreateParam;
import com.medusa.gruul.addon.ic.modules.opens.judanke.model.shop.CreateResp;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.Set;

/**
 * @author 张治保
 * @since 2024/8/6
 */
public class JudankeTest {

    private IJudankeApiFactory factory() {
        JudankeConfig config = new JudankeConfig();
        config.setTest(true);
        return new JudankeApiFactory(config);
    }

    /**
     * 测试发送短信验证码
     */
    @Test
    void testSendSmsCode() {
        JudankeResponse<Void> response = factory().merchant()
                .sendVerifyCode(
                        new SendVerifyCodeParam()
                                .setPhone("17621222222")
                );
        Assertions.assertTrue(response.isSuccess(), response::toString);
        System.out.println(response);
    }

    /**
     * 绑定商户
     * relativeUserId=344903896, phone=17621222222
     */
    @Test
    void testBindMerchant() {
        JudankeResponse<BindMerchantResp> response = factory().merchant().bindMerchant(
                new BindMerchantParam()
                        .setPhone("17621222222")
                        .setCode("999999")
        );
        Assertions.assertTrue(response.isSuccess(), response::toString);
        //relativeUserId=344903896, phone=17621222222
        System.out.println(response);
    }

    /**
     * 创建店铺
     * shopId=6146
     */
    @Test
    void testCreateShop() {
        JudankeResponse<CreateResp> response = factory().shop()
                .create(
                        new CreateParam()
                                .setName("测试店铺")
                                .setPhone("17621222223")
                                .setProvince("浙江省")
                                .setCity("宁波市")
                                .setDistrict("江北区")
                                .setAddress("长兴路199号")
                                //121.490403, 29.937427
                                .setLng(BigDecimal.valueOf(121.490403))
                                .setLat(BigDecimal.valueOf(29.937427))
                                .setGoodsTypeId(GoodsTypeId.C)
                                .setContactPerson("毛毛")
                                .setRelativeUserId(344903896L)
                );
        Assertions.assertTrue(response.isSuccess(), response::toString);
        System.out.println(response);
    }


    /**
     * 预创建订单
     * "order_id":
     * 908075980500027
     * 908076029108920
     */
    @Test
    void testOrderPreCreate() {
        String orderNo = "SS" + IdUtil.getSnowflakeNextIdStr();
        JudankeResponse<OrderCreateResp> response = factory().order()
                .preCreate(
                        new OrderCreateParam()
                                .setThirdOrderNo(orderNo)
                                .setBooking(Switch.ON)
                                .setShopId(6146L)
                                .setReceiverName("薛之谦")
                                .setReceiverPhone("17621222223")
                                .setReceiverProvince("浙江省")
                                .setReceiverCity("宁波市")
                                .setReceiverDistrict("江北区")
                                .setReceiverAddress("")
                                .setReceiverLng(BigDecimal.valueOf(121.481118))
                                .setReceiverLat(BigDecimal.valueOf(29.940556))
                                .setGoodsPrice(BigDecimal.valueOf(38 * 100))
                                .setGoodsWeight(800L)
                                .setGoodsQuantity(1L)
                                .setDeliveryRemark("嗷嗷嗷嗷")
                                .setTipFee(0L)
                                .setRelativeUserId(344903896L)
                                .setThirdPlatform("smtsh")
                );
        Assertions.assertTrue(response.isSuccess(), response::toString);
        System.out.println(response);
    }

    /**
     * 新版获取报价（根据订单ID、支持自运力）
     */
    @Test
    void testNewThirdQuotation() {
        JudankeResponse<NewThirdQuotationResp> response = factory().order().newThirdQuotation(
                new NewThirdQuotationParam()
                        .setOrderId(908075980500027L)
                        .setRelativeUserId(344903896L)
                        .setBooking(Switch.ON)
                        .setDeliveryRemark("需要一个包装袋")
        );
        Assertions.assertTrue(response.isSuccess(), response::toString);
        System.out.println(response);
    }

    /**
     * 发起配送
     */
    @Test
    void testThirdPublish() {
        JudankeResponse<OrderCreateResp> response = factory().order()
                .thirdPublish(
                        new ThirdPublishParam()
                                .setOrderId(908075980500027L)
                                .setRelativeUserId(344903896L)
                                .setBooking(Switch.ON)
                                .setDeliveryBrands(Set.of(DeliveryBrand.ddks, DeliveryBrand.ss, DeliveryBrand.ccs))
                );
        Assertions.assertTrue(response.isSuccess(), response::toString);
        System.out.println(response);
    }

    /**
     * 查询账户余额
     */
    @Test
    void testGetBalance() {
        JudankeResponse<BetBalanceResp> response = factory().finance()
                .getBalance();
        Assertions.assertTrue(response.isSuccess(), response::toString);
        System.out.println(response);

    }

    @Test
    void testGetRechargeQrcode() {
        JudankeResponse<GetRechargeQrcodeResp> response = factory().finance()
                .getRechargeQrcode(
                        new GetRechargeQrcodeParam()
                                .setPayMethod(PayMethod.alisan)
                                .setMoney(1000L)
                );
        //测试环境 无法使用
//        Assertions.assertTrue(response.isSuccess(), response::toString);
        System.out.println(response);

    }

    @Test
    void testJSON() {
        GetRechargeQrcodeParam data = new GetRechargeQrcodeParam()
                .setMoney(100L)
                .setPayMethod(PayMethod.wxsan);
        String jsonString = JSON.toJSONString(data, NameFilter.of(PropertyNamingStrategy.SnakeCase));
        System.out.println(jsonString);
        GetRechargeQrcodeParam getRechargeQrcodeParam = JSON.parseObject(jsonString, GetRechargeQrcodeParam.class, JSONReader.Feature.SupportSmartMatch);
        System.out.println(getRechargeQrcodeParam);
    }

    @Test
    void testJSON2() {
        System.out.println(JSON.toJSONString(Switch.ON));
        System.out.println(JSON.toJSONString(Switch.OFF));
    }

    @Test
    void testMethodNameToApi() {
        Assertions.assertEquals("testMethodNameToApi", methodNameToApi());
    }

    private String methodNameToApi() {
        StackTraceElement[] stackTrace = Thread.currentThread().getStackTrace();
        return stackTrace[2].getMethodName();
    }
}
