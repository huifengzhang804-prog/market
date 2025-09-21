package com.medusa.gruul.service.uaa.service.mp.mapper;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.medusa.gruul.service.uaa.service.model.dto.ShopCustomAdminPageDTO;
import com.medusa.gruul.service.uaa.service.model.vo.ShopUserDataVO;
import com.medusa.gruul.service.uaa.service.mp.entity.ShopUserData;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;

import java.util.Set;

/**
 * <p>
 * 管理员(平台/店铺)资料查询表 Mapper 接口
 * </p>
 *
 * @author 张治保
 * @since 2022-04-27
 */
public interface ShopUserDataMapper extends BaseMapper<ShopUserData> {

    /**
     * 店铺用户(管理员)资料分页查询
     *
     * @param shopCustomAdminPage 分页参数
     * @return 查询结果
     */
    IPage<ShopUserDataVO> shopUserDataPage(@Param("page") ShopCustomAdminPageDTO shopCustomAdminPage);

    /**
     * 根据id查询管理员资料
     *
     * @param dataId    资料id
     * @param clientIds
     * @return 查询结果
     */
    ShopUserDataVO shopUserDataById(@Param("dataId") Long dataId,@Param("clientRoleValues") Set<Integer> clientRoleValues);

}
