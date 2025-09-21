package com.medusa.gruul.order.service.modules.orderTest.mock;

import com.medusa.gruul.goods.api.rpc.Good2OrderRpcService;
import com.medusa.gruul.order.service.modules.order.addon.OrderAddonDistributionSupporter;
import com.medusa.gruul.order.service.modules.orderTest.mock.rpc.AddonMockTest;
import com.medusa.gruul.order.service.modules.orderTest.mock.rpc.GoodMockTest;
import com.medusa.gruul.order.service.modules.orderTest.mock.rpc.StorageMockTest;
import com.medusa.gruul.order.service.modules.orderTest.mock.rpc.UserMockTest;
import com.medusa.gruul.user.api.rpc.UserRpcService;
import org.springframework.context.annotation.Bean;

/**
 * @author 张治保
 * @since 2024/2/20
 */
public class DubboMockConfig {

    @Bean
    public Good2OrderRpcService good2OrderRpcService() {
        return new GoodMockTest();
    }

    @Bean
    public UserRpcService userRpcService() {
        return new UserMockTest();
    }

    @Bean
    public StorageMockTest storageRpcService() {
        return new StorageMockTest();
    }

    @Bean
    public OrderAddonDistributionSupporter orderAddonDistributionSupporter() {
        return new AddonMockTest();
    }
}
