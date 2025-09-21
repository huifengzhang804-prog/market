package com.medusa.gruul.order.service.modules.orderTest;

import com.google.common.collect.Sets;
import com.medusa.gruul.common.model.enums.DistributionMode;
import com.medusa.gruul.common.model.enums.OrderType;
import com.medusa.gruul.order.api.enums.OrderSourceType;
import com.medusa.gruul.order.api.rpc.OrderRpcService;
import com.medusa.gruul.order.service.MockUtil;
import com.medusa.gruul.order.service.OrderServiceApplication;
import com.medusa.gruul.order.service.config.DubboConfig;
import com.medusa.gruul.order.service.model.dto.OrderShopsDTO;
import com.medusa.gruul.order.service.model.dto.ProductDTO;
import com.medusa.gruul.order.service.model.dto.ReceiverDTO;
import com.medusa.gruul.order.service.model.dto.ShopPackageDTO;
import com.medusa.gruul.order.service.model.vo.OrderBudgetVO;
import com.medusa.gruul.order.service.modules.order.service.CreateOrderService;
import com.medusa.gruul.order.service.modules.orderTest.mock.DubboMockConfig;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.context.annotation.Import;

import java.util.HashMap;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

/**
 * @author 张治保
 * @since 2024/2/20
 */
@Slf4j
@Import(DubboMockConfig.class)
@ComponentScan(excludeFilters = {
        @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, value = {
                DubboConfig.class
        }),
        @ComponentScan.Filter(type = FilterType.REGEX, pattern = "com.medusa.gruul.order.service.modules.order.service.addon..")
}
)
@SpringBootTest(classes = OrderServiceApplication.class)
public class OrderTest {

    @Autowired
    private OrderRpcService orderRpcService;

    @Autowired
    private CreateOrderService createOrderService;

    @Test
    public void queryShopUnCompleteOrderNum() {
        List<Long> shopIds = orderRpcService.queryShopUnCompleteOrderNum(Sets.newHashSet(1567782336624394240L,
                1567782437451268096L));

        log.info(shopIds.toString());
    }

    /**
     * 订单价格预算 单元测试
     */
    @Test
    void testBudget() {
        OrderBudgetVO budget = MockUtil.consumerSystem(
                () -> MockUtil.consumerAuth(() -> createOrderService.orderBudget(param1(1L, 1L)))
        );
        assertNotNull(budget);
        assertEquals(budget.getTotal(), 1990000L);
        assertEquals(budget.getShopDiscount(), 0L);
        assertEquals(budget.getPlatformDiscount(), 0L);
        assertEquals(budget.getMemberDiscount(), 0L);
        assertEquals(budget.getFreight(), 0L);
        assertEquals(budget.getPayAmount(), 1990000L);
    }

    private OrderShopsDTO param1(Long shopId, Long productId) {
        OrderShopsDTO param = new OrderShopsDTO();
        param.setOrderType(OrderType.COMMON);
        param.setSource(OrderSourceType.CART);
        ReceiverDTO receiver = new ReceiverDTO();
        receiver.setName("张三");
        receiver.setAddress("上海市浦东新区");
        receiver.setMobile("13800000000");
        receiver.setArea(List.of("上海市", "上海市", "浦东新区"));
        param.setReceiver(receiver);
        param.setDistributionMode(DistributionMode.EXPRESS);

        ShopPackageDTO shopPackage = new ShopPackageDTO();
        shopPackage.setId(shopId);
        shopPackage.setName("测试店铺");
        shopPackage.setLogo("http://www.baidu.com");
        shopPackage.setRemark(new HashMap<>());
        ProductDTO product = new ProductDTO();
        product.setId(productId);
        product.setSkuId(1L);
        product.setNum(1);
        shopPackage.setProducts(List.of(product));
        param.setShopPackages(List.of(shopPackage));
        return param;
    }
}
