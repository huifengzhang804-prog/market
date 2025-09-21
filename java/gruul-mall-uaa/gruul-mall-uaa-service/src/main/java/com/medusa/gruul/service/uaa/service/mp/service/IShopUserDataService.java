package com.medusa.gruul.service.uaa.service.mp.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.medusa.gruul.service.uaa.service.model.dto.ShopCustomAdminPageDTO;
import com.medusa.gruul.service.uaa.service.model.vo.ShopUserDataVO;
import com.medusa.gruul.service.uaa.service.mp.entity.ShopUserData;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * <p>
 * 管理员(平台/店铺)资料查询表 服务类
 * </p>
 *
 * @author 张治保
 * @since 2022-04-27
 */
public interface IShopUserDataService extends IService<ShopUserData> {

    /**
     * 店铺用户(管理员)资料分页查询
     * @param shopCustomAdminPage 分页参数
     * @return 查询结果
     */
    IPage<ShopUserDataVO> shopUserDataPage(ShopCustomAdminPageDTO shopCustomAdminPage);

    /**
     * 根据id查询管理员资料
     * @param dataId 资料id
     * @return  查询结果
     */
    ShopUserDataVO shopUserDataById(Long dataId);
}
