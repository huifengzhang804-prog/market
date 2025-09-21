package com.medusa.gruul.order.service.modules.printer;

import com.medusa.gruul.order.service.OrderServiceApplication;
import com.medusa.gruul.order.service.modules.printer.service.PrinterOpenApiConfigService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

/**
 * @author 张治保
 * @since 2024/8/23
 */
@SpringBootTest(classes = OrderServiceApplication.class)
public class FeieConfigTest {

    @Autowired
    private PrinterOpenApiConfigService configService;


    @Test
    void testGetConfig() {
        configService.feieConfig();
    }

}
