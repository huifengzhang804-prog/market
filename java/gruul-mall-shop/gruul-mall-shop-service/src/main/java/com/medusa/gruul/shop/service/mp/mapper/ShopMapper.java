package com.medusa.gruul.shop.service.mp.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.medusa.gruul.shop.api.entity.Shop;
import com.medusa.gruul.shop.service.model.dto.ShopQueryNoPageDTO;
import com.medusa.gruul.shop.service.model.dto.ShopQueryPageDTO;
import com.medusa.gruul.shop.service.model.vo.ShopListVO;
import com.medusa.gruul.shop.service.model.vo.ShopStatusQuantityVO;
import com.medusa.gruul.shop.service.model.vo.ShopVO;
import com.medusa.gruul.shop.service.model.vo.SupplierStatisticsVO;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 商家注册信息 Mapper 接口
 *
 * @author 张治保
 * @since 2022-04-14
 */
public interface ShopMapper extends BaseMapper<Shop> {

    /**
     * 分页查询店铺
     *
     * @param page 分页参数
     * @return 分页查询结果
     */
    IPage<ShopVO> pageShop(@Param("page") ShopQueryPageDTO page);

    /**
     * 获取当日店铺新增数量
     *
     * @return 当日店铺新增数量
     */
    Long queryTodayAddShopQuantity();

    /**
     * 获取店铺数量 group by ShopStatus
     *
     * @return 店铺数量
     */
    List<ShopStatusQuantityVO> queryShopQuantity();

    /**
     * 获取供应商数量
     *
     * @return 供应商数量
     */
    List<SupplierStatisticsVO> querySupplierQuantity();

    /**
     * 分页查询店铺列表
     * 此方法接受一个 ShopQueryPageDTO 对象作为参数，用于指定分页查询的参数，比如页码、每页条数等。方法返回一个 IPage<ShopListVO> 对象，其中包含了分页后的店铺列表视图对象。ShopListVO 可能是一个封装了店铺相关信息的视图对象，用于展示给前端或其他调用者。
     *
     * @param page ShopQueryPageDTO 对象，包含分页查询的参数
     * @return IPage<ShopListVO> 对象，包含符合分页条件的店铺列表视图对象
     */
    IPage<ShopListVO> pageShopList(@Param("page") ShopQueryPageDTO page);

    /**
     * 获取店铺状态(待审核)数量
     *
     * @param page 检索条件
     * @return 店铺待审核数量
     */
    Long queryShopStatusCount(@Param("page") ShopQueryNoPageDTO page);

    /**
     * 查询店铺审核被拒绝的原因
     * @param shopId 店铺ID
     * @param status 店铺状态
     * @return 审核被拒绝原因
     */
    String queryRejectReason(@Param("shopId") Long shopId,@Param("status") Integer status);
}
