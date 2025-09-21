package com.medusa.gruul.shop.service.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.medusa.gruul.common.module.app.shop.ShopStatus;
import com.medusa.gruul.shop.service.model.dto.ShopDTO;
import com.medusa.gruul.shop.service.model.dto.ShopQueryNoPageDTO;
import com.medusa.gruul.shop.service.model.dto.ShopQueryPageDTO;
import com.medusa.gruul.shop.service.model.vo.ShopListVO;
import com.medusa.gruul.shop.service.model.vo.ShopVO;
import com.medusa.gruul.shop.service.model.vo.SupplierStatisticsVO;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author 张治保
 * date 2022/4/15
 */

public interface ShopManageService {

    /**
     * 分页查询店铺
     *
     * @param page 分页参数
     * @return 分页查询结果
     */
    IPage<ShopVO> pageShop(ShopQueryPageDTO page);

    /**
     * 新增店铺信息
     *
     * @param shop 店铺信息
     */
    void newShop(ShopDTO shop);

    /**
     * 编辑店铺信息
     *
     * @param shopId 店铺id
     * @param shop   店铺信息
     */
    void editShop(Long shopId, ShopDTO shop);

    /**
     * 根据店铺id删除商家信息
     *
     * @param shopIds 店铺id集合
     */
    void deleteShop(Set<Long> shopIds);


    /**
     * 根据店铺id批量启用/即用商家
     *
     * @param isEnable 是否启用
     * @param shopIds  店铺id集合
     */
    void enableDisableShop(Boolean isEnable, Set<Long> shopIds);

    /**
     * 店铺审核
     *
     * @param shopId             店铺id
     * @param pass               是否审核通过
     * @param reasonForRejection 审核不通过原因
     */
    void shopAudit(Long shopId, boolean pass, String reasonForRejection);

    /**
     * 获取今日新增店铺数量
     *
     * @return 今日新增店铺数量
     */
    Long getTodayAddShopQuantity();

    /**
     * 获取店铺数量 by status
     *
     * @return 店铺数量
     */
    Map<ShopStatus, Long> getShopQuantity();

    /**
     * 获取供应商数量 by status
     *
     * @return 店铺数量
     */
    List<SupplierStatisticsVO> getSupplierQuantity();

    /**
     * 添加用户访问
     */
    void addShopVisitor();

    /**
     * 获取店铺访问数量
     *
     * @return 店铺今日访问数量
     */
    Long getShopVisitorNum();


    /**
     * 分页查询店铺列表信息
     *
     * @param page 分页参数
     * @return 分页查询结果
     */
    IPage<ShopListVO> pageShopList(ShopQueryPageDTO page);

    /**
     * 店铺重新审核 修改状态为审核中
     *
     * @param shopId 店铺id
     */
    void reviewAuditShop(Long shopId);


    /**
     * 获取店铺状态(待审核)数量
     *
     * @param page 检索条件
     * @return 店铺待审核数量
     */
    Long getShopStatusCount(ShopQueryNoPageDTO page);

    /**
     * 查询店铺审核未通过的原因
     *
     * @param shopId
     * @return
     */
    String queryRejectReason(Long shopId);

    /**
     * 查询店铺的详情
     *
     * @param shopId
     * @return
     */
    ShopVO getShopDetail(Long shopId);

    /**
     * 更新店铺上架商品的数量
     *
     * @param shopId
     */
    void updateShopOnShelfGoodsCount(Long shopId);
}
