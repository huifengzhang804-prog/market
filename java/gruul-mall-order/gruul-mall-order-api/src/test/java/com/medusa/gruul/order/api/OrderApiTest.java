package com.medusa.gruul.order.api;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

/**
 * @author 张治保
 * @since 2024/4/13
 */
@Slf4j
public class OrderApiTest {

    @Test
    void testAmount() {
        long amount = 1000;
        int num = 3;
        long dealPrice = (amount / (3 * 100)) * 100;
        long fixPrice = amount - dealPrice * num;
        log.info("dealPrice:{}", dealPrice);
        log.info("fixPrice:{}", fixPrice);
    }
}
