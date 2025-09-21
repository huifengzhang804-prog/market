package com.medusa.gruul.shop.service.mp.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.medusa.gruul.shop.api.enums.DecorationEndpointTypeEnum;
import com.medusa.gruul.shop.api.enums.TemplateBusinessTypeEnum;
import com.medusa.gruul.shop.api.model.dto.DecorationCopyPageParamDTO;
import com.medusa.gruul.shop.service.model.dto.*;
import com.medusa.gruul.shop.service.model.vo.ShopDecorationPageDetailsVO;
import com.medusa.gruul.shop.service.mp.entity.ShopDecorationPage;

import java.util.List;

/**
 * 店铺装修页面接口
 *
 * @author Andy.Yan
 */
public interface IShopDecorationPageService extends IService<ShopDecorationPage> {

    /**
     * 创建店铺装修页面对象
     *
     * @param dto 店铺装修页面对象,参考{@link ShopDecorationPageCreateDTO}
     * @return {@link ShopDecorationPage#getId()}
     */
    Long create(ShopDecorationPageCreateDTO dto);

    /**
     * 编辑店铺装修页面对象
     *
     * @param dto 店铺装修页面对象,参考{@link ShopDecorationPageModifyDTO}
     */
    void modify(ShopDecorationPageModifyDTO dto);


    /**
     * 克隆店铺装修页面对象
     *
     * @param dto 被克隆的页面对象,参考{@link ShopDecorationPageCloneDTO}
     */
    void clone(ShopDecorationPageCloneDTO dto);

    /**
     * 删除店铺装修页面对象
     *
     * @param dto 店铺装修页面对象
     */
    void delete(ShopDecorationPageDeleteDTO dto);


    /**
     * 根据ID获取店铺装修页面详情
     *
     * @param id 页面ID
     * @return {@link ShopDecorationPageDetailsVO}
     */
    ShopDecorationPageDetailsVO getShopPageById(Long id);

    /**
     * 分页查询店铺装修页面数据
     *
     * @param dto 查询对象 {@link ShopDecorationPagePagingQueryDTO}
     * @return {@link ShopDecorationPageDetailsVO}
     */
    IPage<ShopDecorationPageDetailsVO> pageQuery(ShopDecorationPagePagingQueryDTO dto);


    /**
     * 根据店铺ID+页面类型获取页面集合
     *
     * @param businessType 业务类型
     * @param endpointType 终端类型
     * @return {@link ShopDecorationPage}
     */
    List<ShopDecorationPage> listPageByShopIdAndPageType(TemplateBusinessTypeEnum businessType,
                                                         DecorationEndpointTypeEnum endpointType);


    /**
     * 根据参数复制平台装修页面
     *
     * @param param 复制参数
     */
    void clonePlatformPage(DecorationCopyPageParamDTO param);
}
