package com.medusa.gruul.addon.platform.mp.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.medusa.gruul.addon.platform.model.dto.DecorationTemplateCloneDTO;
import com.medusa.gruul.addon.platform.model.dto.DecorationTemplateCreateDTO;
import com.medusa.gruul.addon.platform.model.dto.DecorationTemplateModifyDTO;
import com.medusa.gruul.addon.platform.model.dto.DecorationTemplatePageQueryDTO;
import com.medusa.gruul.addon.platform.model.dto.DecorationTemplateStatusChangeDTO;
import com.medusa.gruul.addon.platform.model.enums.TemplateTypeEnum;
import com.medusa.gruul.addon.platform.model.vo.DecorationPageDetailVO;
import com.medusa.gruul.addon.platform.model.vo.DecorationTemplatePagesVO;
import com.medusa.gruul.addon.platform.model.vo.DecorationTemplateVO;
import com.medusa.gruul.addon.platform.mp.entity.DecorationTemplate;
import com.medusa.gruul.shop.api.enums.DecorationEndpointTypeEnum;
import com.medusa.gruul.shop.api.enums.PageTypeEnum;
import com.medusa.gruul.shop.api.enums.TemplateBusinessTypeEnum;

public interface IDecorationTemplateService extends IService<DecorationTemplate> {


    /**
     * 创建平台装修模版，并检查模版下所有页面的合法性
     *
     * @param dto {@link DecorationTemplateCreateDTO}
     */
    void create(DecorationTemplateCreateDTO dto);

    /**
     * 分页查询平台装修模板
     *
     * @param pageQueryDTO 分页参数,参考{@link DecorationTemplatePageQueryDTO}
     * @return {@link DecorationTemplateVO}
     */
    IPage<DecorationTemplateVO> pageQuery(DecorationTemplatePageQueryDTO pageQueryDTO);

    /**
     * 逻辑删除模板,当模板下存在页面引用时不能删除
     *
     * @param id 模板ID
     */
    void delete(Long id);

    /**
     * 复制模板
     *
     * @param dto 源模版
     */
    void clone(DecorationTemplateCloneDTO dto);

    /**
     * 编辑模板信息
     *
     * @param dto 模板信息对象,参考{@link DecorationTemplateModifyDTO}
     */
    void modify(DecorationTemplateModifyDTO dto);

    /**
     * 获取装修模板
     *
     * @param id 模板ID
     * @return {@link DecorationTemplateVO}
     */
    DecorationTemplateVO getDecorationTemplate(Long id);

    /**
     * 修改模板状态
     *
     * @param statusChangeDTO {@link DecorationTemplateStatusChangeDTO}
     */
    void modifyTemplateStatus(DecorationTemplateStatusChangeDTO statusChangeDTO);


    /**
     * 按照类型获取页面集合
     *
     * @param templateType 装修类型,参考{@link TemplateTypeEnum}
     * @param businessType 业务类型,参考{@link TemplateBusinessTypeEnum}
     * @param endpointType 终端类型,参考{@link DecorationEndpointTypeEnum}
     * @return {@link DecorationTemplatePagesVO}
     */
    DecorationTemplatePagesVO listPages(TemplateTypeEnum templateType, TemplateBusinessTypeEnum businessType,
            DecorationEndpointTypeEnum endpointType);

    /**
     * 获取平台端启用的模板对象
     *
     * @return {@link DecorationTemplateVO}
     */
    DecorationTemplateVO getOpeningUpDecorationTemplate();

    /**
     * 获取平台启用的模版
     *
     * @param endpointType 终端类型
     * @return {@link DecorationTemplateVO}
     */
    DecorationTemplateVO getEnabledTemplate(DecorationEndpointTypeEnum endpointType);

    /**
     * 获取平台启用的模版中的页面信息(根据页面类型)
     *
     * @param endpoint   终端类型
     * @param type       页面类型
     * @param customType 自定义类型
     * @return {@link DecorationPageDetailVO}
     */
    DecorationPageDetailVO getPageByEnabledTemplate(DecorationEndpointTypeEnum endpoint, PageTypeEnum type,
            String customType);

}
