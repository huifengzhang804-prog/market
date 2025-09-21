package com.medusa.gruul.order.service.modules.printer;

import com.medusa.gruul.order.api.enums.PrintLink;
import com.medusa.gruul.order.service.OrderServiceApplication;
import com.medusa.gruul.order.service.modules.printer.feie.api.model.enums.FeieTicketSize;
import com.medusa.gruul.order.service.modules.printer.model.dto.PrintTemplateDTO;
import com.medusa.gruul.order.service.modules.printer.model.enums.PrintMode;
import com.medusa.gruul.order.service.modules.printer.model.enums.PrinterBrand;
import com.medusa.gruul.order.service.modules.printer.model.template.*;
import com.medusa.gruul.order.service.modules.printer.model.template.conf.*;
import com.medusa.gruul.order.service.modules.printer.service.PrintTemplateService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static com.medusa.gruul.order.service.modules.printer.PrinterServiceTest.TEST_SHOP_ID;

/**
 * @author 张治保
 * @since 2024/8/22
 */
@SpringBootTest(classes = OrderServiceApplication.class)
public class PrintTemplateServiceTest {
    @Autowired
    private PrintTemplateService templateService;

    @Test
    void addTemplate() {
        templateService.saveOrUpdate(
                TEST_SHOP_ID,
                new PrintTemplateDTO()
                        .setMode(PrintMode.INTRA_CITY)
                        .setName("测试模板")
                        .setBrand(PrinterBrand.FEIE)
                        .setLink(PrintLink.CUSTOMER)
                        .setSize(FeieTicketSize.V58)
                        .setConfig(
                                new PrintTemplateConfig()
                                        .setTitle(
                                                new Title()
                                                        .setPickupCode(true)
                                                        .setStyle(
                                                                List.of(
                                                                        PrintFontStyle.SIZE_2,
                                                                        PrintFontStyle.ALIGN_CENTER
                                                                )
                                                        )
                                        )
                                        .setShopName(
                                                new ShopName()
                                                        .setSelected(true)
                                                        .setStyle(List.of(PrintFontStyle.ALIGN_CENTER))
                                        )
                                        .setOrderRemark(
                                                new OrderRemark()
                                                        .setSelected(true)
                                                        .setStyle(List.of(PrintFontStyle.WEIGHT_2))
                                        )
                                        .setProducts(
                                                new Products()
                                                        .setProductName(true)
                                                        .setType(PrintProductType.SPACE_BETWEEN)
                                                        .setSpecs(true)
                                        )
                                        .setOrderStatistic(
                                                new OrderStatistic()
                                                        .setProductNum(true)
                                                        .setTotalPrice(true)
                                                        .setFreight(true)
                                                        .setPlatformDiscount(true)
                                                        .setShopDiscount(true)
                                                        .setTotalDiscount(true)
                                                        .setPayPrice(
                                                                new PayPrice().setSelected(true)
                                                                        .setStyle(List.of(PrintFontStyle.SIZE_2))
                                                        )
                                        ).setOrderInfo(
                                                new OrderInfo()
                                                        .setOrderNo(new OrderNo().setSelected(true))
                                                        .setPayType(new PayType().setSelected(true))
                                                        .setOrderTime(new OrderTime().setSelected(true))
                                                        .setPayTime(new PayTime().setSelected(true))
                                        )
                                        .setTargetInfo(
                                                new TargetInfo()
                                                        .setName(new TargetName().setSelected(true))
                                                        .setMobile(new TargetMobile().setSelected(true))
                                                        .setAddress(new TargetAddress().setSelected(true))
                                        )
                                        .setCode(
                                                new PrintCode()
                                                        .setType(PrintCodeType.QR_CODE)
                                                        .setContent("哞哞")
                                                        .setDesc("牛怎么叫")
                                        )
                                        .setDesc("全鸡配汉堡，华莱士吃好")
                        )


        );
    }
}
