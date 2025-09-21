package com.medusa.gruul.order.service.modules.printer;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.medusa.gruul.common.redis.util.RedisUtil;
import com.medusa.gruul.order.service.OrderServiceApplication;
import com.medusa.gruul.order.service.modules.printer.feie.api.FeieConfig;
import com.medusa.gruul.order.service.modules.printer.model.PrinterConstant;
import com.medusa.gruul.order.service.modules.printer.model.dto.PrinterPageDTO;
import com.medusa.gruul.order.service.modules.printer.model.dto.PrinterSaveDTO;
import com.medusa.gruul.order.service.modules.printer.model.enums.PrintMode;
import com.medusa.gruul.order.service.modules.printer.model.enums.PrinterBrand;
import com.medusa.gruul.order.service.modules.printer.model.vo.PrinterRecordVO;
import com.medusa.gruul.order.service.modules.printer.service.PrinterService;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

/**
 * @author 张治保
 * @since 2024/8/21
 */

@Slf4j
@SpringBootTest(classes = OrderServiceApplication.class)
public class PrinterServiceTest {

    public static final Long TEST_SHOP_ID = 2L;

    @Autowired
    private PrinterService printerService;


    @Test
    void addFeieConfig() {
        RedisUtil.setCacheMap(
                PrinterConstant.FEIE_CONFIG_CACHE_KEY,
                new FeieConfig()
                        .setUser("user")
                        .setUkey("ukey")
        );
    }

    @Test
    void addPrinter() {
        printerService.save(
                TEST_SHOP_ID,
                new PrinterSaveDTO()
                        .setMode(PrintMode.INTRA_CITY)
                        .setBrand(PrinterBrand.FEIE)
                        .setName("测试打印机")
                        .setPlace("办公室，第二排")
                        .setSn("sn")
                        .setKey("key")
                        .setFlowCard(null)
        );
    }

    @Test
    void page() {
        IPage<PrinterRecordVO> page = printerService.page(
                TEST_SHOP_ID,
                new PrinterPageDTO()
                        .setMode(PrintMode.INTRA_CITY)
        );
        log.info(page.toString());
    }

    @Test
    void delete() {
        printerService.delete(TEST_SHOP_ID, 1826437530663456768L);
    }
}
