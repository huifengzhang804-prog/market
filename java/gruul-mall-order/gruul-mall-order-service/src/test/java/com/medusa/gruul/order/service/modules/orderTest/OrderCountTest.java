package com.medusa.gruul.order.service.modules.orderTest;

import com.medusa.gruul.order.service.OrderServiceApplication;
import com.medusa.gruul.order.service.model.dto.OrderCountQueryDTO;
import com.medusa.gruul.order.service.model.vo.OrderCountVO;
import com.medusa.gruul.order.service.modules.order.service.QueryOrderService;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.function.Consumer;

/**
 * 订单统计约束 单元测试
 *
 * @author 张治保
 * @since 2024/7/10
 */
@Slf4j
@SpringBootTest(classes = OrderServiceApplication.class)
public class OrderCountTest {

    @Autowired
    private QueryOrderService queryOrderService;

    @Test
    void countTest() {
        OrderCountQueryDTO query = new OrderCountQueryDTO();
        //无参数
        test(query);
        //设置订单号
        setParamThenTestAndClear(
                () -> test(query),
                "SS1111",
                query::setOrderNo
        );
        //设置买家昵称
        setParamThenTestAndClear(
                () -> test(query),
                "李四",
                query::setBuyerNickname
        );
        //设置收货人姓名
        setParamThenTestAndClear(
                () -> test(query),
                "张三",
                query::setReceiverName
        );

        //设置商品名称
        setParamThenTestAndClear(
                () -> test(query),
                "商品名称",
                query::setProductName
        );
        //设置查询指定供应商的商品
        setParamThenTestAndClear(
                () -> test(query),
                1L,
                query::setSupplierId
        );
        //设置开始时间
        setParamThenTestAndClear(
                () -> test(query),
                null,
                query::setTime
        );

        //设置店铺ids 供查询条件店铺类型使用
        setParamThenTestAndClear(
                () -> test(query),
                null,
                query::setShopIds
        );

        //设置配送方式
        setParamThenTestAndClear(
                () -> test(query),
                null,
                query::setDistributionMode
        );
    }

    <T> void setParamThenTestAndClear(Runnable task, T param, Consumer<T> consumer) {
        consumer.accept(param);
        task.run();
        consumer.accept(null);
    }

    void test(OrderCountQueryDTO query) {
        OrderCountVO count = Assertions.assertDoesNotThrow(
                () -> queryOrderService.orderCount(query)
        );
        log.info(count.toString());
    }
}
