package com.medusa.gruul.order.service.modules;

import com.medusa.gruul.common.mp.model.TenantShop;
import com.medusa.gruul.order.service.OrderServiceApplication;
import com.medusa.gruul.order.service.mp.mapper.OrderMapper;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDate;

/**
 * @author 张治保
 * @since 2024/2/18
 */

@SpringBootTest(classes = OrderServiceApplication.class)
public class SupplierOrderTest {

    @Autowired
    private OrderMapper orderMapper;


    /**
     * AND ord.create_time BETWEEN '2023-01-01 00:00:00'
     * AND '2023-12-01 23:59:59'
     */
    @Test
    void test() {
        Assertions.assertDoesNotThrow(
                () -> TenantShop.disable(() -> orderMapper.countConsignmentProductTradeStatistic(
                        1681194307905175552L,
                        LocalDate.parse("2023-01-01").atStartOfDay(),
                        LocalDate.parse("2023-12-01").atTime(23, 59, 59)
                ))
        );
    }
}
