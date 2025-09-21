package com.medusa.gruul.addon.platform.mp.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson2.JSON;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.medusa.gruul.addon.platform.model.dto.DecorationPageOfTemplateDTO;
import com.medusa.gruul.addon.platform.model.dto.DecorationTemplateCloneDTO;
import com.medusa.gruul.addon.platform.model.dto.DecorationTemplateCreateDTO;
import com.medusa.gruul.addon.platform.model.dto.DecorationTemplateModifyDTO;
import com.medusa.gruul.addon.platform.model.dto.DecorationTemplatePageQueryDTO;
import com.medusa.gruul.addon.platform.model.dto.DecorationTemplateStatusChangeDTO;
import com.medusa.gruul.addon.platform.model.enums.PlatformError;
import com.medusa.gruul.addon.platform.model.enums.TemplateTypeEnum;
import com.medusa.gruul.addon.platform.model.vo.DecorationPageDetailVO;
import com.medusa.gruul.addon.platform.model.vo.DecorationTemplatePagesVO;
import com.medusa.gruul.addon.platform.model.vo.DecorationTemplateVO;
import com.medusa.gruul.addon.platform.mp.entity.DecorationPage;
import com.medusa.gruul.addon.platform.mp.entity.DecorationTemplate;
import com.medusa.gruul.addon.platform.mp.mapper.DecorationTemplateMapper;
import com.medusa.gruul.addon.platform.mp.service.IDecorationPageService;
import com.medusa.gruul.addon.platform.mp.service.IDecorationTemplateService;
import com.medusa.gruul.addon.platform.properties.PlatformConfigurationProperties;
import com.medusa.gruul.addon.platform.util.PlatformUtil;
import com.medusa.gruul.common.model.constant.CommonPool;
import com.medusa.gruul.common.mp.model.BaseEntity;
import com.medusa.gruul.common.mp.model.SqlHelper;
import com.medusa.gruul.common.redis.util.RedisUtil;
import com.medusa.gruul.shop.api.enums.DecorationEndpointTypeEnum;
import com.medusa.gruul.shop.api.enums.PageTypeEnum;
import com.medusa.gruul.shop.api.enums.TemplateBusinessTypeEnum;
import com.medusa.gruul.shop.api.model.bo.PageReference;
import io.vavr.control.Option;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * <p>平台装修模版服务接口实现类</p>
 *
 * @author Andy.Yan
 */
@Service
@RequiredArgsConstructor
@Lazy
public class DecorationTemplateServiceImpl extends ServiceImpl<DecorationTemplateMapper, DecorationTemplate> implements
        IDecorationTemplateService {


    private final IDecorationPageService decorationPageService;
    private final PlatformConfigurationProperties platformConfigurationProperties;

    /**
     * 创建平台装修模版，并检查模版下所有页面的合法性
     *
     * @param param {@link DecorationTemplateCreateDTO}
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void create(DecorationTemplateCreateDTO param) {
        // 校验参数
        param.validParam();
        // 检查装修模版名称是否重复
        TemplateBusinessTypeEnum businessType = param.getBusinessType();
        boolean nullBusinessType = businessType == null;
        boolean exists = this.lambdaQuery()
                .eq(DecorationTemplate::getName, param.getName())
                .eq(DecorationTemplate::getTemplateType, param.getTemplateType())
                .eq(DecorationTemplate::getEndpointType, param.getEndpointType())
                .isNull(nullBusinessType, DecorationTemplate::getBusinessType)
                .eq(!nullBusinessType, DecorationTemplate::getBusinessType, businessType)
                .exists();
        PlatformError.TEMPLATE_NAME_ALREADY_EXIST.trueThrow(exists);
        // 检查装修模版所包含的页面是否存在
        Set<Long> pageIds = param.getPages()
                .stream()
                .map(PageReference::getPageId)
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());
        Long count = decorationPageService.lambdaQuery().in(BaseEntity::getId, pageIds).count();
        PlatformError.PAGE_NOT_EXIST.falseThrow(count.equals((long) pageIds.size()));
        // 创建模板
        DecorationTemplate template = new DecorationTemplate();
        BeanUtil.copyProperties(param, template);
        //如果不存在启用的模板 则启用当前模板
        exists = this.lambdaQuery()
                .eq(DecorationTemplate::getEnabled, Boolean.TRUE)
                .eq(DecorationTemplate::getTemplateType, param.getTemplateType())
                .eq(DecorationTemplate::getEndpointType, param.getEndpointType())
                .isNull(nullBusinessType, DecorationTemplate::getBusinessType)
                .eq(!nullBusinessType, DecorationTemplate::getBusinessType, businessType)
                .exists();
        template.setEnabled(!exists);
        //保存模板
        PlatformError.TEMPLATE_CREATION_FAILED.falseThrow(
                this.save(template)
        );

    }

    /**
     * 分页查询平台装修模板
     *
     * @param pageQueryDTO 分页参数,参考{@link DecorationTemplatePageQueryDTO}
     * @return {@link DecorationTemplateVO}
     */
    @Override
    public IPage<DecorationTemplateVO> pageQuery(DecorationTemplatePageQueryDTO pageQueryDTO) {
        Page<DecorationTemplate> templatePage = this.lambdaQuery()
                .eq(pageQueryDTO.getTemplateType() != null, DecorationTemplate::getTemplateType,
                        pageQueryDTO.getTemplateType())
                .eq(pageQueryDTO.getBusinessType() != null, DecorationTemplate::getBusinessType,
                        pageQueryDTO.getBusinessType())
                .eq(pageQueryDTO.getEndpointType() != null, DecorationTemplate::getEndpointType,
                        pageQueryDTO.getEndpointType())
                .orderByDesc(DecorationTemplate::getEnabled)
                .page(new Page<>(pageQueryDTO.getCurrent(), pageQueryDTO.getSize()));
        return templatePage.convert(
                template -> {
                    DecorationTemplateVO vo = new DecorationTemplateVO();
                    BeanUtil.copyProperties(template, vo);
                    return vo;
                }
        );
    }

    /**
     * 逻辑删除模板,当模板下存在页面引用时不能删除
     * todo 🤔 多线程下应该加锁操作
     *
     * @param id 模板ID
     */
    @Override
    @SuppressWarnings("unchecked")
    @Transactional(rollbackFor = Exception.class)
    public void delete(Long id) {
        // 检查模板是否存在
        DecorationTemplate template = this.lambdaQuery().eq(DecorationTemplate::getId, id).one();
        PlatformError.TEMPLATE_NOT_EXIST.trueThrow(template == null);

        //如果是平台模板
        Boolean currentEnabled = template.getEnabled();
        if (template.getTemplateType() == TemplateTypeEnum.PLATFORM && currentEnabled) {
            //如果当前模板是启用状态  则把最新的模板启用
            boolean update = this.lambdaUpdate()
                    .set(DecorationTemplate::getEnabled, Boolean.TRUE)
                    .ne(DecorationTemplate::getId, id)
                    .eq(DecorationTemplate::getTemplateType, TemplateTypeEnum.PLATFORM)
                    .eq(DecorationTemplate::getBusinessType, template.getBusinessType())
                    .eq(DecorationTemplate::getEndpointType, template.getEndpointType())
                    .orderByDesc(DecorationTemplate::getCreateTime)
                    .last(SqlHelper.SQL_LIMIT_1)
                    .update();
            //如果启用失败 则说明当前只有一个模板 不能删除
            PlatformError.TEMPLATE_CAN_NOT_DELETE.falseThrow(update);
        }
        //当前页面缓存 key
        Collection<String> pageCacheKeys = RedisUtil.keys(PlatformUtil.pageCacheKeyPattern(template.getEndpointType()));

        RedisUtil.doubleDeletion(
                // 删除模板
                () -> PlatformError.FAILED_TO_DELETE_TEMPLATE.falseThrow(this.removeById(id)),
                //pipeline 模式删除缓存
                () -> RedisUtil.executePipelined(
                        operations -> {
                            //删除当前模板下的所有页面
                            operations.delete(PlatformUtil.templateCopyCacheKey(id));
                            //如果当前是开启状态 需要吧页面缓存删除
                            if (currentEnabled) {
                                operations.delete(pageCacheKeys);
                            }
                        }
                )
        );
    }

    /**
     * 复制模板
     *
     * @param dto 源模版
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void clone(DecorationTemplateCloneDTO dto) {
        // 检查源模板是否存在
        Long templateId = dto.getTemplateId();
        DecorationTemplate sourceTemplate = this.lambdaQuery().eq(DecorationTemplate::getId, templateId).one();
        PlatformError.TEMPLATE_NOT_EXIST.trueThrow(sourceTemplate == null);
        // 副本数量 +1
        RedisUtil.increment(
                //自增 key
                PlatformUtil.templateCopyCacheKey(templateId),
                //自增步长
                CommonPool.NUMBER_ONE,
                //自增后执行的函数
                (count) -> {
                    String clonedName = PlatformUtil.copyName(sourceTemplate.getName(), count);
                    // 检查名字长度
                    PlatformError.TEMPLATE_NAME_LENGTH_TOO_LONG.trueThrow(clonedName.length() > CommonPool.NUMBER_TEN);
                    // 检查装修模版名称是否重复
                    boolean exists = this.lambdaQuery()
                            .eq(DecorationTemplate::getName, clonedName)
                            .eq(DecorationTemplate::getTemplateType, sourceTemplate.getTemplateType())
                            .eq(DecorationTemplate::getEndpointType, sourceTemplate.getEndpointType())
                            .eq(sourceTemplate.getBusinessType() != null, DecorationTemplate::getBusinessType,
                                    sourceTemplate.getBusinessType())
                            .exists();
                    PlatformError.TEMPLATE_NAME_ALREADY_EXIST.trueThrow(exists);
                    // 创建clone对象并insert
                    DecorationTemplate copyOfTemplate = new DecorationTemplate()
                            .setDescription(sourceTemplate.getDescription())
                            .setName(clonedName)
                            .setPages(sourceTemplate.getPages())
                            .setTemplateType(sourceTemplate.getTemplateType())
                            .setEndpointType(sourceTemplate.getEndpointType())
                            .setBusinessType(sourceTemplate.getBusinessType())
                            .setEnabled(Boolean.FALSE);
                    PlatformError.TEMPLATE_CREATION_FAILED.falseThrow(this.save(copyOfTemplate));
                }
        );
    }

    /**
     * 编辑模板信息
     *
     * @param param 模板信息对象,参考{@link DecorationTemplateModifyDTO}
     */
    @Override
    public void modify(DecorationTemplateModifyDTO param) {
        // 检查页面类型
        param.validParam();
        // 检查模板是否存在
        DecorationTemplate template = this.getById(param.getId());
        if (template == null) {
            throw PlatformError.TEMPLATE_NOT_EXIST.exception();
        }
        // 如果名称不同 则 检查装修模版名称是否重复
        if (!StrUtil.equals(template.getName(), param.getName())) {
            boolean exists = this.lambdaQuery()
                    .eq(DecorationTemplate::getName, param.getName())
                    .eq(DecorationTemplate::getTemplateType, param.getTemplateType())
                    .eq(DecorationTemplate::getEndpointType, param.getEndpointType())
                    .eq(param.getBusinessType() != null, DecorationTemplate::getBusinessType, param.getBusinessType())
                    .ne(DecorationTemplate::getId, param.getId())
                    .exists();
            PlatformError.TEMPLATE_NAME_ALREADY_EXIST.trueThrow(exists);
        }
        // 更新模板
        RedisUtil.doubleDeletion(
                () -> {
                    boolean updatedResult = this.lambdaUpdate()
                            .set(DecorationTemplate::getName, param.getName())
                            .set(DecorationTemplate::getDescription, param.getDescription())
                            .set(DecorationTemplate::getPages, JSON.toJSONString(param.getPages()))
                            .eq(DecorationTemplate::getId, param.getId()).update();
                    PlatformError.FAILED_TO_UPDATE_TEMPLATE.falseThrow(updatedResult);
                },
                //删除所有缓存页面
                () -> RedisUtil.delete(RedisUtil.keys(PlatformUtil.pageCacheKeyPattern(template.getEndpointType())))
        );

    }

    /**
     * 获取装修模板
     *
     * @param id 模板ID
     * @return {@link DecorationTemplateVO}
     */
    @Override
    public DecorationTemplateVO getDecorationTemplate(Long id) {
        // 检查模板是否存在
        DecorationTemplate template = this.getById(id);
        PlatformError.TEMPLATE_NOT_EXIST.trueThrow(template == null);
        DecorationTemplateVO result = new DecorationTemplateVO();
        BeanUtil.copyProperties(template, result);
        return result;
    }

    /**
     * 修改模板状态 启用当前模板
     * todo 🤔 多线程下应该加锁操作
     *
     * @param param {@link DecorationTemplateStatusChangeDTO}
     */
    @Override
    public void modifyTemplateStatus(DecorationTemplateStatusChangeDTO param) {
        // 检查模板是否存在
        Long templateId = param.getTemplateId();
        DecorationTemplate template = this.lambdaQuery()
                .eq(DecorationTemplate::getId, templateId)
                .eq(DecorationTemplate::getTemplateType, TemplateTypeEnum.PLATFORM)
                .one();
        if (template == null) {
            throw PlatformError.TEMPLATE_NOT_EXIST.exception();
        }
        if (template.getEnabled()) {
            return;
        }
        //缓存双删 页面缓存
        RedisUtil.doubleDeletion(
                () -> {
                    //禁用其它模板
                    this.lambdaUpdate()
                            .set(DecorationTemplate::getEnabled, Boolean.FALSE)
                            .eq(DecorationTemplate::getEnabled, Boolean.TRUE)
                            .eq(DecorationTemplate::getTemplateType, TemplateTypeEnum.PLATFORM)
                            .eq(DecorationTemplate::getEndpointType, template.getEndpointType())
                            .ne(DecorationTemplate::getId, templateId)
                            .isNull(DecorationTemplate::getBusinessType)
                            .update();

                    //启用当前模板
                    PlatformError.FAILED_TO_CHANGE_TEMPLATE_STATUS.falseThrow(this.lambdaUpdate()
                            .set(DecorationTemplate::getEnabled, Boolean.TRUE)
                            .eq(DecorationTemplate::getId, templateId)
                            .update());
                },
                () -> RedisUtil.delete(RedisUtil.keys(PlatformUtil.pageCacheKeyPattern(template.getEndpointType())))
        );

    }

    /**
     * 按照类型获取页面集合
     * todo 🤔 太混乱了 有时间重写，拓展性不好  比如可以 返回一个页面类型和页面集合对应关系的Map
     * todo 由于修改返回结果需要前端做修改 所以暂不调整
     *
     * @param templateType 装修类型,参考{@link TemplateTypeEnum}
     * @param businessType 业务类型,参考{@link TemplateBusinessTypeEnum}
     * @param endpointType 终端类型,参考{@link DecorationEndpointTypeEnum}
     * @return {@link DecorationTemplatePagesVO} 页面集合
     */
    public DecorationTemplatePagesVO listPages(TemplateTypeEnum templateType, TemplateBusinessTypeEnum businessType,
            DecorationEndpointTypeEnum endpointType) {
        DecorationPageOfTemplateDTO dto = new DecorationPageOfTemplateDTO()
                .setTemplateType(templateType)
                .setEndpointType(endpointType)
                .setBusinessType(businessType);

        List<DecorationPage> pages = decorationPageService.listPageByPageType(dto);
        if (CollUtil.isEmpty(pages)) {
            return new DecorationTemplatePagesVO();
        }
        Map<PageTypeEnum, List<DecorationPage>> map = pages.stream()
                .collect(Collectors.groupingBy(DecorationPage::getType));
        Function<PageTypeEnum, List<DecorationTemplatePagesVO.PageVO>> pagesFunction =
                pageTypeEnum -> Option.of(map.get(pageTypeEnum))
                        .map(currPages -> currPages.stream()
                                .map(item -> DecorationTemplatePagesVO.PageVO.of(item.getId(), item.getName()))
                                .toList()
                        ).getOrElse(List.of());
        if (templateType == TemplateTypeEnum.PLATFORM) {
            return new DecorationTemplatePagesVO()
                    .setRecommendedMallHomePages(pagesFunction.apply(PageTypeEnum.RECOMMENDED_MALL_HOME_PAGE))
                    .setSameCityMallHomePages(pagesFunction.apply(PageTypeEnum.SAME_CITY_MALL_HOME_PAGE))
                    .setProductCategoryPages(pagesFunction.apply(PageTypeEnum.PRODUCT_CATEGORY_PAGE))
                    .setBottomNavigationPages(pagesFunction.apply(PageTypeEnum.BOTTOM_NAVIGATION_PAGE))
                    .setPersonalCenterPages(pagesFunction.apply(PageTypeEnum.PERSONAL_CENTER_PAGE))
                    .setCustomizedPages(pagesFunction.apply(PageTypeEnum.CUSTOMIZED_PAGE));
        }
        return new DecorationTemplatePagesVO()
                .setShopHomePages(pagesFunction.apply(PageTypeEnum.SHOP_HOME_PAGE))
                .setShopBottomNavigationPages(pagesFunction.apply(PageTypeEnum.SHOP_BOTTOM_NAVIGATION_PAGE))
                .setShopCategoryPages(pagesFunction.apply(PageTypeEnum.SHOP_CATEGORY_PAGE))
                .setShopCustomizedPages(pagesFunction.apply(PageTypeEnum.SHOP_CUSTOMIZED_PAGE));
    }

    /**
     * 获取平台端启用的模板对象
     * todo 不知道有啥用 前端用了吗？
     *
     * @return {@link DecorationTemplateVO}
     */
    @Override
    public DecorationTemplateVO getOpeningUpDecorationTemplate() {
        // 检查模板是否存在
        DecorationTemplate template = this.lambdaQuery()
                .eq(DecorationTemplate::getEnabled, Boolean.TRUE)
                .eq(DecorationTemplate::getTemplateType, TemplateTypeEnum.PLATFORM)
                .last(SqlHelper.SQL_LIMIT_1)
                .one();
        PlatformError.TEMPLATE_NOT_EXIST.trueThrow(template == null);
        DecorationTemplateVO result = new DecorationTemplateVO();
        BeanUtil.copyProperties(template, result);
        return result;
    }

    /**
     * 获取平台启用的模版中的页面信息(根据页面类型)
     *
     * @param type 页面类型
     * @return {@link DecorationPageDetailVO}
     */
    @Override
    public DecorationPageDetailVO getPageByEnabledTemplate(DecorationEndpointTypeEnum endpoint,
            PageTypeEnum type, String customType) {
        return RedisUtil.getCacheMap(
                DecorationPageDetailVO.class,
                () -> {
                    if (DecorationEndpointTypeEnum.PC_MALL == endpoint) {
                        DecorationPage page = decorationPageService.lambdaQuery()
                                .eq(DecorationPage::getEndpointType, endpoint)
                                .eq(DecorationPage::getType, type)
                                .eq(StrUtil.isNotEmpty(customType), DecorationPage::getCustomType, customType)
                                .last(SqlHelper.SQL_LIMIT_1)
                                .one();
                        return Option.of(page)
                                .map(
                                        item -> new DecorationPageDetailVO()
                                                .setId(page.getId())
                                                .setName(page.getName())
                                                .setRemark(page.getRemark())
                                                .setProperties(page.getProperties())
                                                .setType(page.getType())
                                                .setTemplateType(page.getTemplateType())
                                                .setBusinessType(page.getBusinessType())
                                                .setEndpointType(page.getEndpointType())
                                ).getOrNull();
                    }
                    DecorationTemplateVO enabledTemplate = this.getEnabledTemplate(endpoint);
                    PageReference pageReference = enabledTemplate.getPages()
                            .stream()
                            .filter(ref -> ref.getPageType() == type).findFirst().orElse(null);
                    if (pageReference == null) {
                        throw PlatformError.PAGE_NOT_EXIST.exception();
                    }
                    return this.decorationPageService.getPageById(pageReference.getPageId());
                },
                platformConfigurationProperties.getPageCacheExpire(),
                PlatformUtil.pageCacheKey(endpoint, type, customType)

        );
    }

    /**
     * 获取平台启用的模版
     *
     * @return {@link DecorationTemplateVO}
     */
    @Override
    public DecorationTemplateVO getEnabledTemplate(DecorationEndpointTypeEnum endpointType) {
        DecorationTemplate enabledTemplate = this.lambdaQuery()
                .eq(DecorationTemplate::getEnabled, Boolean.TRUE)
                .eq(DecorationTemplate::getEndpointType, endpointType)
                .eq(DecorationTemplate::getTemplateType, TemplateTypeEnum.PLATFORM)
                .last(SqlHelper.SQL_LIMIT_1)
                .one();
        PlatformError.TEMPLATE_NOT_ENABLED_ON_PLATFORM.trueThrow(enabledTemplate == null);
        DecorationTemplateVO result = new DecorationTemplateVO();
        BeanUtil.copyProperties(enabledTemplate, result);
        return result;
    }
}
