package com.medusa.gruul.order.service;

import com.medusa.gruul.order.service.model.enums.OrderError;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.i18n.LocaleContextHolder;

import java.util.Locale;

/**
 * @author 张治保
 * @since 2024/4/16
 */
@SpringBootTest(classes = OrderServiceApplication.class)
public class I18NTest {

    @Test
    void test() {
        String msg;
        LocaleContextHolder.setLocale(Locale.CHINA, true);
        try {
            msg = OrderError.GOODS_NOT_AVAILABLE.msg();
        } finally {
            LocaleContextHolder.resetLocaleContext();
        }
        Assertions.assertNotEquals(OrderError.GOODS_NOT_AVAILABLE.getMsgCode(), msg);
    }

}
