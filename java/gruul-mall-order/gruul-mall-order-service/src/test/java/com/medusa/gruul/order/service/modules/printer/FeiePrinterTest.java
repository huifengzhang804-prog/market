package com.medusa.gruul.order.service.modules.printer;

import cn.hutool.core.util.StrUtil;
import cn.hutool.http.HtmlUtil;
import com.alibaba.fastjson2.JSON;
import com.medusa.gruul.common.fastjson2.FastJson2;
import com.medusa.gruul.global.model.helper.request.IResponse;
import com.medusa.gruul.order.service.modules.printer.feie.api.FeieApi;
import com.medusa.gruul.order.service.modules.printer.feie.api.FeieConfig;
import com.medusa.gruul.order.service.modules.printer.feie.api.FeieResponse;
import com.medusa.gruul.order.service.modules.printer.feie.api.IFeieApi;
import com.medusa.gruul.order.service.modules.printer.feie.api.model.AddPrinterParam;
import com.medusa.gruul.order.service.modules.printer.feie.api.model.PrintMsgParam;
import com.medusa.gruul.order.service.modules.printer.feie.api.model.PrinterInfoResp;
import com.medusa.gruul.order.service.modules.printer.feie.api.model.enums.FeiePrinterStatus;
import com.medusa.gruul.order.service.modules.printer.feie.api.model.enums.FeieTicketSize;
import com.medusa.gruul.order.service.modules.printer.model.template.*;
import com.medusa.gruul.order.service.modules.printer.model.template.conf.*;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author 张治保
 * @since 2024/8/14
 */
public class FeiePrinterTest {

    /**
     * 校验配置的 user 和 ukey 是否正确
     */
    @Test
    void testConfigSetting() {
        FeieApi feieApi = new FeieApi(
                new FeieConfig()
                        .setTest(false)
                        .setUser("xx@qq.com")
                        .setUkey("ssss")
        );
        FeieResponse<PrinterInfoResp> response = feieApi.printerInfo("null-undefined");
        // user 不正确
        // 1. FeieResponse(ret=-2, msg=参数错误 : 该帐号未注册., data=null, serverExecutedTime=2)
        // ukey 不正确
        // 2. FeieResponse(ret=-3, msg=验证失败 : 签名错误., data=null, serverExecutedTime=3)
        //以下情况 为 user和 ukey 正确
        // 3. FeieResponse(ret=1002, msg=验证失败 : 打印机编号和用户不匹配., data=null, serverExecutedTime=8) 
        // 4. FeieResponse(ret=1001, msg=验证失败 : 打印机编号不合法。, data=null, serverExecutedTime=2)
        // 5. FeieResponse(ret=1001, msg=参数错误 : 打印机编号和用户uid不能为空., data=null, serverExecutedTime=2)
        Assertions.assertFalse(response.isSuccess());

    }

    IFeieApi feieApi() {
        //USER： rowstop@yeah.net
        //UKEY： wEKNZzh3SGaKDakZ
        return new FeieApi(new FeieConfig()
                .setTest(false)
                .setUser("user")
                .setUkey("ukey"));
    }

    @Test
    void testAddPrinter() {
        assertResponse(
                feieApi()
                        .batchAddPrinter(
                                List.of(
                                        //316500010 # abcdefgh # 快餐前台 # 13688889999
                                        new AddPrinterParam()
                                                .setSn("sn")
                                                .setKey("key")
                                )
                        )
        );
    }

    @Test
    void print() {
        PrintTemplateConfig template = new PrintTemplateConfig();
        //打印订单内容
        //小票标题
        template.setTitle(
                new Title()
                        .setPickupCode(true)
                        .setStyle(List.of(
                                PrintFontStyle.ALIGN_CENTER,
                                PrintFontStyle.SIZE_2
                        ))
        );
        //店铺名称
        template.setShopName(
                new ShopName()
                        .setSelected(true)
                        .setStyle(List.of(PrintFontStyle.ALIGN_CENTER))
        );
        //订单备注
        template.setOrderRemark(
                new OrderRemark()
                        .setSelected(true)
                        .setStyle(List.of(
                                PrintFontStyle.WEIGHT_2
                        ))
        );
        //商品信息
        Products products = new Products()
                .setProductName(true)
                .setSkuId(true)
                .setSpecs(true)
                .setType(PrintProductType.FORMAT);
        template.setProducts(products);

        OrderStatistic orderStatistic = new OrderStatistic()
                .setProductNum(true)
                .setTotalPrice(true)
                .setFreight(true)
                .setPlatformDiscount(true)
                .setShopDiscount(true)
                .setTotalDiscount(true)
                .setPayPrice(
                        new PayPrice()
                                .setSelected(true)
                                .setStyle(List.of(PrintFontStyle.WEIGHT_2, PrintFontStyle.SIZE_2))
                );
        template.setOrderStatistic(orderStatistic);

        template.setOrderInfo(
                new OrderInfo()
                        .setOrderNo(
                                new OrderNo()
                                        .setSelected(true)
                                        .setStyle(List.of())
                        )
                        .setPayType(
                                new PayType()
                                        .setSelected(true)
                                        .setStyle(List.of())
                        )
                        .setOrderTime(
                                new OrderTime()
                                        .setSelected(true)
                                        .setStyle(List.of())
                        )
                        .setPayTime(
                                new PayTime()
                                        .setSelected(true)
                                        .setStyle(List.of())
                        )
        );

        template.setTargetInfo(
                new TargetInfo()
                        .setName(
                                new TargetName()
                                        .setSelected(true)
                                        .setStyle(List.of(PrintFontStyle.WEIGHT_2))
                        )
                        .setMobile(
                                new TargetMobile()
                                        .setSelected(true)
                                        .setStyle(List.of(PrintFontStyle.WEIGHT_2))
                        )
                        .setAddress(
                                new TargetAddress()
                                        .setSelected(true)
                                        .setStyle(List.of(PrintFontStyle.WEIGHT_2))
                        )
        );


        //打印二维码
        template.setCode(
                new PrintCode()
                        .setType(PrintCodeType.QR_CODE)
                        .setContent("1123456222222222222222")
                        .setDesc("二维码")
        );

        //打印底部信息
        template.setDesc("谢谢惠顾 欢迎下次光临");

        FeieTicketSize v58 = FeieTicketSize.V58;
        String fxml = template.fxml(v58);
        Map<Object, String> params = new HashMap<>();
        params.put(PrintPlaceHolder.pickupCode, "24");
        params.put(PrintPlaceHolder.platformName, "启山智软");
        params.put(PrintPlaceHolder.shopName, "一号当铺");
        params.put(PrintPlaceHolder.orderRemark, "订单备注订单备注订单备注订单备注订单备注订单备注订单备注订单备注订单备注订单备注");
        params.put(
                PrintPlaceHolder.productListInf,
                products.fxml(v58,
                        List.of(
                                new PrintProduct()
                                        .setName("华为 MateBook16")
                                        .setSpecs(List.of("黑色", "16 核", "32G"))
                                        .setSkuId(1234567893254332234L)
                                        .setNum(1)
                                        .setPrice(1231L),

                                new PrintProduct()
                                        .setName("华为 MateBook16")
                                        .setSpecs(List.of("黑色", "8核", "16G"))
                                        .setSkuId(1234567893254332234L)
                                        .setNum(1)
                                        .setPrice(1231L)
                        )
                )
        );
        params.put(
                PrintPlaceHolder.orderStatistic,
                orderStatistic.fxml(
                        v58,
                        new PrintOrderStatistic()
                                .setProductNum(1)
                                .setTotalPrice(1231L)
                                .setFreight(0L)
                                .setPlatformDiscount(0L)
                                .setShopDiscount(0L)
                                .setTotalDiscount(0L)
                                .setPayPrice(11242300L)
                )
        );
        params.put(PrintPlaceHolder.orderNo, "SS123214141");
        params.put(PrintPlaceHolder.payType, "支付宝");
        params.put(PrintPlaceHolder.orderTime, LocalDateTime.now().minusMinutes(30).format(FastJson2.DATETIME_FORMATTER));
        params.put(PrintPlaceHolder.payTime, LocalDateTime.now().format(FastJson2.DATETIME_FORMATTER));

        params.put(PrintPlaceHolder.targetName, "张三");
        params.put(PrintPlaceHolder.targetMobile, "138****8000");
        params.put(PrintPlaceHolder.targetAddress, "宁波市江北区洪塘街道长兴路 199 号");
        fxml = StrUtil.format(fxml, params);
        assertResponse(
                feieApi().printMsg(
                        new PrintMsgParam()
                                .setSn("932533866")
                                .setContent(fxml)
                                .setTimes(1)
                )
        );
    }

    @Test
    void printDemo() {
        feieApi().printMsg(
                new PrintMsgParam()
                        .setSn("932533866")
                        .setContent("""
                                哈哈哈哈<BR><B>哈</B><BR>
                                <W>哈</W><BR><L>哈</L>
                                """)
        );
    }


    @Test
    void printerInfo() {
        assertResponse(
                feieApi().printerInfo("932533866")
        );
    }

    void assertResponse(IResponse<?> response) {
        Assertions.assertTrue(response.isSuccess(), response::toString);
    }

    @Test
    void testJson() {
        FeiePrinterStatus v1 = JSON.parseObject("0", FeiePrinterStatus.class);
        FeiePrinterStatus v2 = JSON.parseObject("1", FeiePrinterStatus.class);
        FeiePrinterStatus v3 = JSON.parseObject("2", FeiePrinterStatus.class);
        FeiePrinterStatus v4 = JSON.parseObject("3", FeiePrinterStatus.class);

        v1 = JSON.parseObject("\"离线\"", FeiePrinterStatus.class);
        v2 = JSON.parseObject("\"在线，工作状态正常\"", FeiePrinterStatus.class);
        v3 = JSON.parseObject("\"在线，工作状态不正常\"", FeiePrinterStatus.class);
        v4 = JSON.parseObject("\"aaa\"", FeiePrinterStatus.class);
    }

    @Test
    void testa() {
        String str = "{&quot;appid&quot;:&quot;ccba8bd4a2d54a2fb6df97e87979f303&quot;,&quot;driver_jobnum&quot;:&quot;&quot;,&quot;driver_mobile&quot;:&quot;&quot;,&quot;driver_name&quot;:&quot;&quot;,&quot;driver_photo&quot;:&quot;&quot;,&quot;nonce_str&quot;:&quot;4RRr648gxq&quot;,&quot;order_code&quot;:&quot;240814160110379000017445&quot;,&quot;origin_id&quot;:&quot;F806412681970150&quot;,&quot;return_code&quot;:&quot;ok&quot;,&quot;return_msg&quot;:&quot;&quot;,&quot;sign&quot;:&quot;864A41CAED83F19724A4D4590DF89AB0&quot;,&quot;state&quot;:&quot;1&quot;,&quot;state_text&quot;:&quot;下单成功&quot;,&quot;timestamp&quot;:&quot;1723623580094&quot;}";
        String decode = HtmlUtil.unescape(str);
    }
}
