package com.medusa.gruul.shop.service;


import com.baomidou.mybatisplus.core.metadata.IPage;
import com.medusa.gruul.common.module.app.shop.ShopMode;
import com.medusa.gruul.shop.service.model.dto.ShopSearchParamDTO;
import com.medusa.gruul.shop.service.model.vo.ShopSearchVO;
import com.medusa.gruul.shop.service.mp.service.IShopDecorationTemplateService;
import com.medusa.gruul.shop.service.mp.service.IShopService;
import com.medusa.gruul.shop.service.service.ShopInfoService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;

/**
 * @author 张治保
 * date 2022/4/19
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class CommonTest {

    @Autowired
    private RedisTemplate redisTemplate;

    @Autowired
    private IShopDecorationTemplateService templateService;

    @Autowired
    private IShopService shopService;

    @Autowired
    private ShopInfoService shopInfoService;


    public void test() {
        String PASSWORD = "^(?![A-Z]+$)(?![a-z]+$)(?!\\d+$)(?![\\W_]+$)\\S{6,16}$";
        System.out.println(java.util.regex.Pattern.matches(PASSWORD, "aaaa5."));
    }

    @Test
    public void queryEnabledTemplatesOfAllShopTest() {
//        List<ShopDecorationTemplate> templatesOfAllShop = TenantShop.disable(() ->
//                templateService.queryEnabledTemplatesOfAllShop(Arrays.asList(ShopMode.O2O, ShopMode.COMMON), TemplateBusinessTypeEnum.O2O)
//        );
//        System.out.println(templatesOfAllShop);
    }

    @Test
    public void listShopByShopModeTest() {
        System.out.println(shopService.listShopByShopMode(List.of(ShopMode.O2O)));
    }


    /**
     * 1731564294356123648
     * 1763408488020099072
     * 1765293654351069184
     */
    @Test
    public void testShopSearchOrder() {
        IPage<ShopSearchVO> searchShop = shopInfoService.searchShop(
                new ShopSearchParamDTO()
                        .setUserSearch(false)
                        .setProductIsNotEmpty(false)
                        .setShopIds(
                                List.of(
                                        1763408488020099072L,
                                        1731564294356123648L
                                )
                        )
        );
        System.out.println(1);
    }


}
