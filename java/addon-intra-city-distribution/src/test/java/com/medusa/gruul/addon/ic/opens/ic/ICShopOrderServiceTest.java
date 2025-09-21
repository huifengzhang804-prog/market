package com.medusa.gruul.addon.ic.opens.ic;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.medusa.gruul.addon.ic.AddonIcApplication;
import com.medusa.gruul.addon.ic.addon.IcAddonProvider;
import com.medusa.gruul.addon.ic.model.ICConstant;
import com.medusa.gruul.addon.ic.modules.ic.model.dto.BillMethod;
import com.medusa.gruul.addon.ic.modules.ic.model.dto.ICShopOrderPageDTO;
import com.medusa.gruul.addon.ic.modules.ic.model.enums.BillType;
import com.medusa.gruul.addon.ic.modules.ic.model.enums.ICDeliveryType;
import com.medusa.gruul.addon.ic.modules.ic.mp.entity.ICShopConfig;
import com.medusa.gruul.addon.ic.modules.ic.mp.entity.ICShopOrder;
import com.medusa.gruul.addon.ic.modules.ic.service.ICShopOrderService;
import com.medusa.gruul.common.geometry.Geometry;
import com.medusa.gruul.common.redis.util.RedisUtil;
import com.medusa.gruul.common.security.model.bean.SecureUser;
import com.medusa.gruul.global.model.express.UserAddressDTO;
import com.medusa.gruul.order.api.enums.ICShopOrderStatus;
import com.medusa.gruul.order.api.model.ic.ICOrder;
import com.medusa.gruul.order.api.model.ic.ICProductSku;
import com.vividsolutions.jts.geom.Coordinate;
import java.math.BigDecimal;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

/**
 * @author 张治保
 * @since 2024/8/27
 */
@SpringBootTest(classes = AddonIcApplication.class)
public class ICShopOrderServiceTest {

    //C
    static Long TEST_SHOP_ID = 2L;
    static Long TEST_USER_ID = 3L;
    static String TEST_ORDER_NO = "SS111111";
    static SecureUser<?> LOGIN_USER = new SecureUser<>()
            .setShopId(TEST_SHOP_ID)
            .setId(TEST_USER_ID);

    @Autowired
    private IcAddonProvider addonProvider;
    @Autowired
    private ICShopOrderService shopOrderService;

    @Test
    void saveShopConfig() {
        RedisUtil.setCacheMap(
                RedisUtil.key(ICConstant.SHOP_CONFIG_CACHE_KEY, TEST_SHOP_ID),
                new ICShopConfig()
                        .setShopId(TEST_SHOP_ID)
                        .setEnableSelf(true)
                        .setEnableOpen(true)
                        .setDefaultType(ICDeliveryType.SELF)
                        .setWarmBox(true)
                        .setDeliveryRange(BigDecimal.valueOf(50))
                        .setDescription("描述")
                        .setMinLimit(1000000L)
                        .setBaseDelivery(30000L)
                        .setBillMethod(
                                new BillMethod()
                                        //以距离计费
                                        .setType(BillType.DISTANCE)
                                        //两公里内免运费
                                        .setFree(BigDecimal.valueOf(2))
                                        //每一公里
                                        .setStep(BigDecimal.ONE)
                                        //收取一块钱运费
                                        .setStepPrice(10000L)

                        )
                        .setFreeLimit(5 * 1000000L)

        );
    }

    @Test
    void addOrder() {
        addonProvider.icOrderReport(
                true,
                new ICOrder()
                        .setShopId(TEST_SHOP_ID)
                        .setOrderNo(TEST_ORDER_NO)
                        .setReceiver(
                                new UserAddressDTO()
                                        .setName("张三")
                                        .setMobile("13800138000")
                                        .setLocation(Geometry.factory().createPoint(
                                                new Coordinate(11231.1, 11231.1)
                                        ))
                                        .setArea(List.of("浙江省", "宁波市", "江北区"))
                                        .setAddress("洪塘街道 长兴路")
                        )
                        .setSkus(
                                List.of(
                                        new ICProductSku()
                                                .setProductName("香辣鸡腿堡")
                                                .setSpecs(List.of())
                                                .setNum(2)
                                )
                        )

//                        .setRemark("需要餐具")
        );
    }

    /**
     * 分页查询派送单
     */
    @Test
    void testOrderPage() {
        IPage<ICShopOrder> page = shopOrderService.page(
                LOGIN_USER,
                new ICShopOrderPageDTO()
                        .setStatus(ICShopOrderStatus.PENDING)
        );
        System.out.println(page.getTotal());
        System.out.println(page.getRecords());
    }

    /**
     * 测试接单
     */
    @Test
    void testTakeOrder() {
        shopOrderService.takeOrder(LOGIN_USER, TEST_ORDER_NO);
    }

    /**
     * 测试取消接单
     */
    @Test
    void offerOrder() {
        shopOrderService.offerOrder(LOGIN_USER, TEST_ORDER_NO);
    }


    @Test
    void orderStatusNext() {
        shopOrderService.orderStatusNext(LOGIN_USER, TEST_ORDER_NO);
    }

}
