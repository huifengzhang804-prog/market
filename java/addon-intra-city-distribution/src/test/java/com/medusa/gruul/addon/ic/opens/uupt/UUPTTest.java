package com.medusa.gruul.addon.ic.opens.uupt;

import com.medusa.gruul.addon.ic.modules.opens.uupt.UuptConfig;
import com.medusa.gruul.addon.ic.modules.opens.uupt.UuptResponse;
import com.medusa.gruul.addon.ic.modules.opens.uupt.api.IUuptApiFactory;
import com.medusa.gruul.addon.ic.modules.opens.uupt.core.UuptApiFactory;
import com.medusa.gruul.addon.ic.modules.opens.uupt.model.enums.*;
import com.medusa.gruul.addon.ic.modules.opens.uupt.model.order.*;
import com.medusa.gruul.addon.ic.modules.opens.uupt.model.user.*;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import java.math.BigDecimal;
import java.util.List;

/**
 * @author 张治保
 * @since 2024/8/10
 */
public class UUPTTest {

    private final String testOpenId = "10fac3a8b79bd4eeead4410ee94626b8a";

    private IUuptApiFactory factory() {
        //AppId：db7079fe70c74bb083f2c33e5202b2cf
        //AppKey：254c3fcc7053410894c4072d6ad2a21a
        //OpenId：0fac3a8b79bd4eeead4410ee94626b8a
        UuptConfig config = new UuptConfig();
        config.setTest(false);
        config.setAppid("db7079fe70c74bb083f2c33e5202b2cf");
        config.setAppKey("254c3fcc7053410894c4072d6ad2a21a");
        config.setOpenId(testOpenId);
        return new UuptApiFactory(config);
    }

    void assertResp(UuptResponse<?> response) {
        Assertions.assertTrue(response.isSuccess(), response::toString);
        System.out.println(response);
    }

    @Test
    void testOpenCityList() {
        UuptResponse<OpenCityListResp> response = factory().order().openCityList();
        assertResp(response);
    }


    /**
     * 88000110 发送频率过快，请稍候重试
     */
    @Test
    void testSendSmsCode() {
        UuptResponse<Void> response = factory()
                .user()
                .sendSmsCode(
                        new SendSmsCodeParam()
                                .setUserMobile("17621228898")
                                .setUserIp("183.135.80.127")
                );
        assertResp(response);
    }

    @Test
    void testAuth() {
        UuptResponse<AuthResp> response = factory()
                .user()
                .auth(
                        new AuthParam()
                                .setUserMobile("17621228898")
                                .setUserIp("183.135.80.127")
                                .setSmsCode("8888")
                                .setCityName("宁波市")
                                .setCountyName("江北区")
                );
        assertResp(response);
    }

    @Test
    void testAccountBalance() {
        UuptResponse<AccountResp> response = factory()
                .user().account(
                        testOpenId,
                        new AccountParam()
                                .setPayTypeEnum(PayTypeEnum.BALANCE_PAY)
                );
        assertResp(response);
    }

    @Test
    void testAccountEnterprise() {
        UuptResponse<AccountResp> response = factory()
                .user().account(
                        testOpenId,
                        new AccountParam()
                                .setPayTypeEnum(PayTypeEnum.ENTERPRISE_PAY)
                );
        assertResp(response);
    }

    @Test
    void testRecharge() {
        UuptResponse<RechargeResp> response = factory()
                .user()
                .recharge(testOpenId);
        assertResp(response);
    }

    /**
     * //        //中旅城三期星巴克
     * //        double[] fromLngLat = CoordTransform.gcj02ToBD09(
     * //                121.481118,
     * //                29.940556
     * //        );
     * //
     * //        // 宁波启山科技
     * //        double[] toLngLat = CoordTransform.gcj02ToBD09(
     * //                121.490403,
     * //                29.937427
     * //        );
     */
    @Test
    void testOrderPrice() {
        assertResp(
                factory()
                        .order()
                        .orderPrice(
                                testOpenId,
                                new OrderPriceParam()
                                        //订单号
                                        //05d97b859e1743a88ba635c2ceca242a
                                        .setOriginId("SS123456789")
                                        //帮我送
                                        .setSendType(SendType.SEND)
                                        //发货地址
                                        .setFromAddress("金水路与玉凤路交汇处浦发国际金融中心")
                                        .setFromUserNote("21层2111房间")
                                        .setFromLat(BigDecimal.valueOf(34.767995))
                                        .setFromLng(BigDecimal.valueOf(113.71742))
                                        //收货地址
                                        .setToAddress("郑州市金水区金水路226号楷林国际")
                                        .setToUserNote("四楼")
                                        .setToLat(BigDecimal.valueOf(34.768788))
                                        .setToLng(BigDecimal.valueOf(113.724148))
                                        .setCityName("郑州市")
                                        .setCountyName("金水区")

                        )
        );

    }


    /**
     * orderCode=240812164010379000014456, originId=SS12345678
     * originId=SS123456789, orderCode=240813095410379000024424
     */
    @Test
    void testAddOrder() {
        //状态码88100031 表示priceToken已过期
        //
        // 需要重新获取价格然后重新发布
        assertResp(
                factory()
                        .order()
                        .addOrder(
                                testOpenId,
                                new AddOrderParam()
                                        .setPriceToken("05d97b859e1743a88ba635c2ceca242a")
                                        .setReceiverPhone("17621228898")
                                        .setCallbackUrl("https://rows.top/api/test")
                                        .setPushType(PushType.TEST_ORDER)
                                        .setSpecialType(SpecialType.NOT_NEED_WARM)
                                        .setPayType(PayType.BALANCE_PAY)
                                        .setShortOrderNum("#1")
                                        .setProductDetailList(
                                                List.of(
                                                        new ProductDetail()
                                                                .setGoodsName("奶茶")
                                                                .setNum(1)
                                                )
                                        )


                        )
        );
    }

    @Test
    void testOrderDetail() {
        assertResp(
                factory()
                        .order()
                        .orderDetail(
                                testOpenId,
                                new OrderKey()
//                                        .setOrderCode("240812164010379000014456")
                                        .setOriginId("SS123456789")
                        )
        );
    }

    @Test
    void cancelFee() {
        assertResp(
                factory()
                        .order()
                        .cancelFee(
                                testOpenId,
                                new OrderKey()
//                                        .setOrderCode("240812164010379000014456")
                                        .setOriginId("SS123456789")
                        )
        );
    }

    @Test
    void addGratuity() {
        assertResp(
                factory()
                        .order()
                        .addGratuity(
                                testOpenId,
                                (AddGratuityParam) new AddGratuityParam()
                                        .setFee(1L)
//                                        .setOrderCode("240812164010379000014456")
                                        .setOriginId("SS123456789")
                        )
        );
    }

    @Test
    void syncPickupCode() {
        assertResp(
                factory()
                        .order()
                        .syncPickupCode(
                                testOpenId,
                                (SyncPickupCodeParam) new SyncPickupCodeParam()
                                        .setPickupCode("1234")
//                                        .setOrderCode("240812164010379000014456")
                                        .setOriginId("SS123456789")
                        )
        );
    }

    @Test
    void cancelOrder() {
        assertResp(
                factory()
                        .order()
                        .cancelOrder(
                                testOpenId,
                                (CancelOrderParam) new CancelOrderParam()
                                        .setReason("取消订单接口调试")
                                        .setOrderCode("240812164010379000014456")
                                        .setOriginId("SS12345678")
                        )
        );
    }

    @Test
    void testJs() throws ScriptException {
        ScriptEngineManager manager = new ScriptEngineManager();
        ScriptEngine engine = manager.getEngineByName("JavaScript");
        Object eval = engine.eval(
                "function doub(b){console.log(b)}"
        );
        System.out.println(eval);

    }

}
