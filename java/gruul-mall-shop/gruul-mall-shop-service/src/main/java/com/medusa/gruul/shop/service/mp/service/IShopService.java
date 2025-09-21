package com.medusa.gruul.shop.service.mp.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.medusa.gruul.common.module.app.shop.ShopMode;
import com.medusa.gruul.shop.api.entity.Shop;
import com.medusa.gruul.shop.service.model.dto.ShopQueryNoPageDTO;
import com.medusa.gruul.shop.service.model.dto.ShopQueryPageDTO;
import com.medusa.gruul.shop.service.model.vo.ShopListVO;
import com.medusa.gruul.shop.service.model.vo.ShopStatusQuantityVO;
import com.medusa.gruul.shop.service.model.vo.ShopVO;
import com.medusa.gruul.shop.service.model.vo.SupplierStatisticsVO;

import java.util.List;

/**
 * 商家注册信息 服务类
 *
 * @author 张治保
 * @since 2022-04-14
 */
public interface IShopService extends IService<Shop> {

    /**
     * 分页查询店铺
     *
     * @param page 分页参数
     * @return 分页查询结果
     */
    IPage<ShopVO> pageShop(ShopQueryPageDTO page);


    /**
     * 获取当日新增店铺数量
     *
     * @return 当前新增店铺数量
     */
    Long getTodayAddShopQuantity();

    /**
     * 获取店铺数量
     *
     * @return 店铺数量
     */
    List<ShopStatusQuantityVO> getShopQuantity();

    /**
     * 获取供应商数量
     *
     * @return 供应商数量
     */
    List<SupplierStatisticsVO> getSupplierQuantity();


    /**
     * 获取与{@code modes}匹配的所有店铺信息
     *
     * @param modes 店铺运营模式,参考{@link ShopMode}
     * @return {@link Shop}
     */
    List<Shop> listShopByShopMode(List<ShopMode> modes);

    /**
     * 查询分页店铺列表
     *
     * @param page 包含分页信息的ShopQueryPageDTO对象
     * @return 返回一个IPage<ShopListVO>对象，包含分页查询到的店铺列表视图对象
     */
    IPage<ShopListVO> pageShopList(ShopQueryPageDTO page);

    /**
     * 获取店铺状态(待审核) 数量
     *
     * @param page 检索条件
     * @return 待审核店铺数量
     */
    Long getShopStatusCount(ShopQueryNoPageDTO page);

    /**
     * 查询店铺审核未通过的原因
     * @param shopId 店铺ID
     * @return 审核未通过的原因
     */
    String queryRejectReason(Long shopId);
}
