package com.medusa.gruul.addon.store.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.medusa.gruul.addon.store.model.dto.ShopStoreDTO;
import com.medusa.gruul.addon.store.model.dto.ShopStoreOperateDTO;
import com.medusa.gruul.addon.store.model.enums.StoreStatus;
import com.medusa.gruul.addon.store.model.param.ShopStoreParam;
import com.medusa.gruul.addon.store.model.vo.ShopStoreListVO;
import com.medusa.gruul.shop.api.model.vo.ShopInfoVO;

import java.util.List;

/**
 *
 *
 * @author xiaoq
 * @Description 店铺门店管理
 * @date 2023-03-07 17:30
 */
public interface ManageShopStoreService {
    /**
     * 店铺门店新增
     *
     * @param shopStoreDTO 店铺门店DTO
     * @param shopId 店铺用户id
     */
    void issueShopStore(ShopStoreDTO shopStoreDTO,Long shopId);

    /**
     * 店铺门店删除
     *
     * @param shopId 店铺id
     * @param id 店铺门店id
     */
    void deletedShopStore(Long shopId,Long id);

    /**
     * 店铺门店修改
     *
     * @param shopStoreDTO 店铺门店DTO
     */
    void updateShopStore(ShopStoreDTO shopStoreDTO);


    /**
     * 获取店铺门店ListVO
     *
     * @param shopStoreParam 查询param
     * @return  IPage<ShopStoreListVO>
     */
    IPage<ShopStoreListVO> getShopStoreListVO(ShopStoreParam shopStoreParam);


    /**
     * 修改门店状态
     *
     * @param shopStoreOperate 店铺门店操作dto
     * @param status 修改状态
     */
    void updateShopStoreStatus(List<ShopStoreOperateDTO> shopStoreOperate, StoreStatus status);

    /**
     * 修改店铺名称
     *
     * @param shopInfo 店铺信息
     */
    void updateShopName(ShopInfoVO shopInfo);
}
