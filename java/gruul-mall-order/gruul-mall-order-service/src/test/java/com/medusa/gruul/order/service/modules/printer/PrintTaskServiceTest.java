package com.medusa.gruul.order.service.modules.printer;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.medusa.gruul.order.service.OrderServiceApplication;
import com.medusa.gruul.order.service.modules.printer.model.dto.OrderPrintDTO;
import com.medusa.gruul.order.service.modules.printer.model.dto.PrintTaskDTO;
import com.medusa.gruul.order.service.modules.printer.model.dto.PrintTaskPageDTO;
import com.medusa.gruul.order.service.modules.printer.model.enums.PrintMode;
import com.medusa.gruul.order.service.modules.printer.model.enums.PrintScene;
import com.medusa.gruul.order.service.modules.printer.model.vo.PrintTaskRecordVO;
import com.medusa.gruul.order.service.modules.printer.service.PrintTaskService;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Set;

import static com.medusa.gruul.order.service.modules.printer.PrinterServiceTest.TEST_SHOP_ID;

/**
 * @author 张治保
 * @since 2024/8/22
 */

@Slf4j
@SpringBootTest(classes = OrderServiceApplication.class)
public class PrintTaskServiceTest {

    @Autowired
    private PrintTaskService printTaskService;

    @Test
    void testAddPrintTask() {
        printTaskService.saveOrUpdate(
                TEST_SHOP_ID,
                new PrintTaskDTO()
                        .setMode(PrintMode.INTRA_CITY)
                        .setName("打印任务")
                        .setPrinterId(1826544562582261760L)
                        .setTemplateId(1830876096829603840L)
                        .setScene(PrintScene.AUTO_PAID)
                        .setTimes(2)
        );
    }

    @Test
    void testPage() {
        IPage<PrintTaskRecordVO> page = printTaskService.page(TEST_SHOP_ID, new PrintTaskPageDTO().setMode(PrintMode.INTRA_CITY));
        log.info("page:{}", page);
    }

    @Test
    void printOrder() {
        //<B><C>#{pickupCode} {platformName}</C></B><BR>
        // <C>{shopName}</C>
        // --------------------------------
        // <BOLD>备注：{orderRemark}</BOLD><BR>
        // ------------商品信息------------
        // {productListInf}<BR>
        // --------------------------------
        // {orderStatistic}
        // 订单号：{orderNo}<BR>
        // 支付方式：{payType}
        // <BR>下单时间：{orderTime}<BR>
        // 支付时间：{payTime}<BR>
        // --------------------------------
        // {targetName}<BR>
        // {targetMobile}<BR>
        // {targetAddress}<BR>
        // --------------------------------
        // <QR>哞哞</QR><C>牛怎么叫</C>
        // <C>全鸡配汉堡，华莱士吃好</C>
        // <CUT>
        printTaskService.printOrder(
                new OrderPrintDTO()
                        .setOrderNo("SS1808374994934726656")
                        .setMode(PrintMode.INTRA_CITY)
                        .setScene(PrintScene.AUTO_PAID)
                        .setShopIds(Set.of())
        );
    }
}
