package com.medusa.gruul.shop.service.mp.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.medusa.gruul.common.module.app.shop.ShopMode;
import com.medusa.gruul.common.module.app.shop.ShopStatus;
import com.medusa.gruul.common.mp.model.TenantShop;
import com.medusa.gruul.shop.api.entity.Shop;
import com.medusa.gruul.shop.service.model.dto.ShopQueryNoPageDTO;
import com.medusa.gruul.shop.service.model.dto.ShopQueryPageDTO;
import com.medusa.gruul.shop.service.model.vo.ShopListVO;
import com.medusa.gruul.shop.service.model.vo.ShopStatusQuantityVO;
import com.medusa.gruul.shop.service.model.vo.ShopVO;
import com.medusa.gruul.shop.service.model.vo.SupplierStatisticsVO;
import com.medusa.gruul.shop.service.mp.mapper.ShopMapper;
import com.medusa.gruul.shop.service.mp.service.IShopService;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 商家注册信息 服务实现类
 *
 * @author 张治保
 * @since 2022-04-14
 */
@Service
public class ShopServiceImpl extends ServiceImpl<ShopMapper, Shop> implements IShopService {

    @Override
    public IPage<ShopVO> pageShop(ShopQueryPageDTO page) {
        return baseMapper.pageShop(page);
    }

    @Override
    public Long getTodayAddShopQuantity() {
        return TenantShop.disable(() -> baseMapper.queryTodayAddShopQuantity());
    }

    @Override
    public List<ShopStatusQuantityVO> getShopQuantity() {
        return baseMapper.queryShopQuantity();
    }

    /**
     * 获取供应商数量
     *
     * @return 供应商数量
     */
    @Override
    public List<SupplierStatisticsVO> getSupplierQuantity() {
        return baseMapper.querySupplierQuantity();
    }

    /**
     * 获取与{@code modes}匹配的所有店铺信息
     *
     * @param modes 店铺运营模式,参考{@link ShopMode}
     * @return {@link Shop}
     */
    @Override
    public List<Shop> listShopByShopMode(List<ShopMode> modes) {
        return this.lambdaQuery().in(Shop::getShopMode, modes).list();
    }

    @Override
    public IPage<ShopListVO> pageShopList(ShopQueryPageDTO page) {
        return baseMapper.pageShopList(page);
    }

    @Override
    public Long getShopStatusCount(ShopQueryNoPageDTO page) {
        return baseMapper.queryShopStatusCount(page);
    }

    @Override
    public String queryRejectReason(Long shopId) {
        return getBaseMapper().queryRejectReason(shopId, ShopStatus.REJECT.getValue());
    }
}
