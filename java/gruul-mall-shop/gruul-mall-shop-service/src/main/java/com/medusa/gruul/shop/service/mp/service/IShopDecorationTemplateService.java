package com.medusa.gruul.shop.service.mp.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.medusa.gruul.shop.api.enums.DecorationEndpointTypeEnum;
import com.medusa.gruul.shop.api.enums.PageTypeEnum;
import com.medusa.gruul.shop.api.enums.TemplateBusinessTypeEnum;
import com.medusa.gruul.shop.api.model.dto.DecorationCopyTemplateParamDTO;
import com.medusa.gruul.shop.service.model.dto.*;
import com.medusa.gruul.shop.service.model.vo.DecorationTemplatePagesVO;
import com.medusa.gruul.shop.service.model.vo.ShopDecorationPageDetailsVO;
import com.medusa.gruul.shop.service.model.vo.ShopDecorationTemplateDetailsVO;
import com.medusa.gruul.shop.service.model.vo.ShopDecorationTemplatePageVO;
import com.medusa.gruul.shop.service.mp.entity.ShopDecorationTemplate;
import jakarta.validation.constraints.NotNull;


/**
 * 店铺装修模板服务接口
 *
 * @author Andy.Yan
 */
public interface IShopDecorationTemplateService extends IService<ShopDecorationTemplate> {

    /**
     * 创建店铺装修模板
     *
     * @param dto 装修模板DTO
     */
    void createTemplate(ShopDecorationTemplateCreateDTO dto);

    /**
     * 分页查询店铺装修模板
     *
     * @param dto 查询参数
     * @return {@link ShopDecorationTemplatePageVO}
     */
    IPage<ShopDecorationTemplatePageVO> pageQuery(ShopDecorationPageQueryDTO dto);

    /**
     * 复制店铺模板
     *
     * @param dto 源模板参数,参考{@link ShopDecorationTemplateCloneDTO}
     */
    void clone(ShopDecorationTemplateCloneDTO dto);

    /**
     * 删除店铺模板
     *
     * @param dto 参考{@link ShopDecorationTemplateDeleteDTO}
     */
    void delete(ShopDecorationTemplateDeleteDTO dto);

    /**
     * 编辑店铺模板
     *
     * @param dto 参考{@link ShopDecorationTemplateModifyDTO}
     */
    void modify(ShopDecorationTemplateModifyDTO dto);

    /**
     * 获取装修模板详情
     *
     * @param templateId 装修模板ID
     * @return {@link ShopDecorationTemplateDetailsVO}
     */
    ShopDecorationTemplateDetailsVO getShopDecorationTemplateById(@NotNull Long templateId);

    /**
     * 修改模板启用状态
     *
     * @param dto 参考{@link ShopDecorationTemplateChangeStatusDTO}
     */
    void modifyTemplateStatus(ShopDecorationTemplateChangeStatusDTO dto);


    /**
     * 获取店铺启用的模板对象
     *
     * @return {@link ShopDecorationTemplateDetailsVO}
     */
    ShopDecorationTemplateDetailsVO getOpeningUpTemplate();

    /**
     * 根据页面类型获取页面集合
     *
     * @param businessType 业务类型 {@link TemplateBusinessTypeEnum}
     * @param endpointType 终端类型 {@link DecorationEndpointTypeEnum}
     * @return {@link DecorationTemplatePagesVO}
     */
    DecorationTemplatePagesVO listPages(TemplateBusinessTypeEnum businessType, DecorationEndpointTypeEnum endpointType);

    /**
     * 获取店铺启用的模板所引用的页面对象
     *
     * @param shopId       店铺ID
     * @param endpointType 终端类型
     * @param pageType     页面类型
     * @param customType   自定义类型
     * @return {@link ShopDecorationPageDetailsVO}
     */
    ShopDecorationPageDetailsVO getShopPageByEnabledTemplate(Long shopId, DecorationEndpointTypeEnum endpointType,
                                                             PageTypeEnum pageType, String customType);

    /**
     * 给指定店铺 id 的店铺复制平台的店铺装修模板
     *
     * @param shopId 店铺 id 不能为空
     * @param param  目标模板复制参数
     * @return {@link ShopDecorationTemplate} 店铺装修模板
     */
    ShopDecorationTemplate copyPlatformTemplate(Long shopId, DecorationCopyTemplateParamDTO param);
}
