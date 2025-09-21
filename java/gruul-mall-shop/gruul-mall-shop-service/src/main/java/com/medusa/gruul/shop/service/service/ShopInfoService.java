package com.medusa.gruul.shop.service.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.medusa.gruul.common.module.app.shop.ShopMode;
import com.medusa.gruul.shop.api.entity.Shop;
import com.medusa.gruul.shop.api.model.dto.ShopInfoDTO;
import com.medusa.gruul.shop.api.model.vo.ShopInfoVO;
import com.medusa.gruul.shop.service.model.dto.ShopDetailDTO;
import com.medusa.gruul.shop.service.model.dto.ShopDetailVO;
import com.medusa.gruul.shop.service.model.dto.ShopSearchParamDTO;
import com.medusa.gruul.shop.service.model.vo.ShopAuditVO;
import com.medusa.gruul.shop.service.model.vo.ShopSearchVO;
import io.vavr.control.Option;

import java.util.List;
import java.util.Set;

/**
 * @author xiaoq Description date 2022-05-26 11:28
 */
public interface ShopInfoService {

    /**
     * 店铺设置信息修改
     *
     * @param shopInfo 店铺修改信息
     */
    void updateShopInfo(ShopInfoDTO shopInfo);

    /**
     * 根据店铺ID 查询店铺信息 先查缓存 缓存查不到查数据库 不为null则 放入缓存中
     *
     * @param shopId 店铺id
     * @return 店铺信息 Option
     */
    Option<Shop> getShopById(Long shopId);

    /**
     * C端店铺搜索
     *
     * @param param 搜索条件
     * @return 查询店铺结果
     */
    IPage<ShopSearchVO> searchShop(ShopSearchParamDTO param);

    /**
     * 根据销量查询店铺
     *
     * @param sortAsc 是否升序
     * @return 查询店铺结果
     */
    List<ShopInfoVO> searchShopBySales(Boolean sortAsc);

    /**
     * 根据距离查询店铺
     *
     * @param longitude         经度
     * @param latitude          纬度
     * @param sortAsc           是否升序
     * @param userId
     * @param showHeaderShopIds
     * @param moreCount
     * @return 查询店铺结果
     */
    List<ShopInfoVO> searchShopByDistance(Double longitude, Double latitude, Boolean sortAsc, Long userId, List<Long> showHeaderShopIds, Integer moreCount);

    ShopInfoVO getShopSaleAndDistance(Long shopId, Double longitude, Double latitude);

    /**
     * 获取供应商信息 List
     *
     * @param supplierName 供应商名称
     * @return List<shop>
     */
    List<Shop> getSupplierInfo(String supplierName);

    /**
     * 客户端 关注页面 查询推荐店铺
     *
     * @param param 检索条件
     * @return IPage ShopInfoVO
     */
    IPage<ShopInfoVO> searchRecommendationShop(ShopSearchParamDTO param);

    /**
     * 获取批量店铺数据
     *
     * @param shopIds 店铺ids
     * @return 店铺信息
     */
    List<Shop> getBatchShop(Set<Long> shopIds);

    /**
     * 根据店铺id查询店铺关注信息
     *
     * @param shopId 店铺id
     * @return 店铺关注信息
     */
    ShopInfoVO getShopInfoFollow(Long shopId);

    /**
     * 根据用户ID获取店铺审核信息
     *
     * @param userId   要获取信息的用户ID
     * @param shopMode 店铺模式
     * @return 包含店铺审核信息的ShopAuditVO对象
     */
    ShopAuditVO getShopAuditInfo(Long userId, ShopMode shopMode);


    /**
     * 查询店铺信息 (暂时用于 C端查询[商家首页，商品详情])
     *
     * @param param 查询参数
     * @return 店铺信息
     */
    ShopDetailVO shopDetail(Long userId, ShopDetailDTO param);

    /**
     * 根据名称模糊匹配店铺信息
     *
     * @param shopName
     * @param includeSupplier
     * @return
     */
    List<Shop> getShopInfo(String shopName, Boolean includeSupplier);
}
