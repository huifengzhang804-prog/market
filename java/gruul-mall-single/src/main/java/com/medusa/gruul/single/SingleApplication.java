package com.medusa.gruul.single;

import com.medusa.gruul.addon.bargain.AddonBargainApplication;
import com.medusa.gruul.addon.coupon.AddonCouponApplication;
import com.medusa.gruul.addon.distribute.AddonDistributeApplication;
import com.medusa.gruul.addon.freight.AddonFreightApplication;
import com.medusa.gruul.addon.full.reduction.AddonFullReductionApplication;
import com.medusa.gruul.addon.ic.AddonIcApplication;
import com.medusa.gruul.addon.integral.AddonIntegralApplication;
import com.medusa.gruul.addon.invoice.AddonInvoiceApplication;
import com.medusa.gruul.addon.live.AddonLiveApplication;
import com.medusa.gruul.addon.matching.treasure.AddonMatchingTreasureApplication;
import com.medusa.gruul.addon.member.AddonMemberApplication;
import com.medusa.gruul.addon.platform.AddonPlatformApplication;
import com.medusa.gruul.addon.rebate.AddonRebateApplication;
import com.medusa.gruul.addon.seckill.AddonSeckillApplication;
import com.medusa.gruul.addon.store.AddonShopStoreApplication;
import com.medusa.gruul.addon.supplier.AddonSupplierApplication;
import com.medusa.gruul.addon.team.AddonTeamApplication;
import com.medusa.gruul.afs.service.AfsServiceApplication;
import com.medusa.gruul.carrier.pigeon.service.CarrierPigeonApplication;
import com.medusa.gruul.cart.service.CartServiceApplication;
import com.medusa.gruul.global.model.constant.AspectOrder;
import com.medusa.gruul.goods.service.GoodsApplication;
import com.medusa.gruul.live.service.LiveServiceApplication;
import com.medusa.gruul.order.service.OrderServiceApplication;
import com.medusa.gruul.overview.service.OverViewServiceApplication;
import com.medusa.gruul.payment.service.PaymentApplication;
import com.medusa.gruul.search.service.SearchServiceApplication;
import com.medusa.gruul.service.uaa.service.UaaServiceApplication;
import com.medusa.gruul.shop.service.ShopServiceApplication;
import com.medusa.gruul.storage.service.StorageServiceApplication;
import com.medusa.gruul.user.service.UserServiceApplication;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

import java.util.Collections;

/**
 * @author 张治保
 * date 2022/3/23
 */
@Import(SingleFilter.class)
@SpringBootApplication(
        scanBasePackageClasses = {
                SingleApplication.class,
                //基础服务
                UaaServiceApplication.class,//认证与授权
                ShopServiceApplication.class,//店铺
                OrderServiceApplication.class,//订单
                AfsServiceApplication.class,//售后
                PaymentApplication.class,//支付
                UserServiceApplication.class,//用户
                StorageServiceApplication.class,//库存
                GoodsApplication.class,//商品
                OverViewServiceApplication.class,//经营概况
                SearchServiceApplication.class,//检索
                CarrierPigeonApplication.class,//信鸽
                CartServiceApplication.class,//购物车
                LiveServiceApplication.class,//直播-小程序
                //插件
                AddonFreightApplication.class, // 快递配送
                AddonCouponApplication.class,//优惠券
                AddonDistributeApplication.class,//分销
                AddonSeckillApplication.class,//秒杀
                AddonMemberApplication.class,//付费会员
                AddonShopStoreApplication.class, //门店
                AddonRebateApplication.class, //返利
                AddonLiveApplication.class,//直播
                AddonTeamApplication.class,//拼团
                AddonFullReductionApplication.class,//满减
                AddonIcApplication.class,//同城配送
                AddonMatchingTreasureApplication.class,//套餐
                AddonBargainApplication.class,//砍价
                AddonIntegralApplication.class, //积分
                AddonInvoiceApplication.class,//发票
                /**
                 * b2b2c代码应注释掉 AddonSupplierApplication.class 启动
                 */
                AddonSupplierApplication.class,//供应商
                AddonPlatformApplication.class, //平台


        }
)
@EnableConfigurationProperties(SingleProperties.class)
@EnableCaching(order = AspectOrder.CACHE_ASPECT)
public class SingleApplication {

    public static void main(String[] args) {
        SpringApplication.run(SingleApplication.class, args);
    }

    /**
     * 跨域配置 只在开发环境生效
     *
     * @return 跨域过滤器
     */
    @Bean
//    @Profile("single-dev")
    public FilterRegistrationBean<CorsFilter> corsFilter() {
        CorsConfiguration corsConfiguration = new CorsConfiguration();
        corsConfiguration.setAllowedOriginPatterns(Collections.singletonList(CorsConfiguration.ALL));
        corsConfiguration.addAllowedHeader(CorsConfiguration.ALL);
        corsConfiguration.addAllowedMethod(CorsConfiguration.ALL);
        corsConfiguration.setAllowCredentials(Boolean.TRUE);
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", corsConfiguration);
        FilterRegistrationBean<CorsFilter> bean = new FilterRegistrationBean<>(new CorsFilter(source));
        bean.setOrder(Integer.MIN_VALUE);
        return bean;
    }
}
