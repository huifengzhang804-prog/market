package com.medusa.gruul.shop.service.mp.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson2.JSON;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.medusa.gruul.common.model.constant.CommonPool;
import com.medusa.gruul.common.module.app.shop.ShopMode;
import com.medusa.gruul.common.mp.model.SqlHelper;
import com.medusa.gruul.common.mp.model.TenantShop;
import com.medusa.gruul.common.redis.util.RedisUtil;
import com.medusa.gruul.common.security.resource.helper.ISecurity;
import com.medusa.gruul.common.system.model.ISystem;
import com.medusa.gruul.shop.api.enums.DecorationEndpointTypeEnum;
import com.medusa.gruul.shop.api.enums.PageTypeEnum;
import com.medusa.gruul.shop.api.enums.TemplateBusinessTypeEnum;
import com.medusa.gruul.shop.api.model.bo.PageReference;
import com.medusa.gruul.shop.api.model.dto.DecorationCopyPageDTO;
import com.medusa.gruul.shop.api.model.dto.DecorationCopyTemplateParamDTO;
import com.medusa.gruul.shop.api.model.dto.PlatformShopDecorationTemplate;
import com.medusa.gruul.shop.service.model.dto.*;
import com.medusa.gruul.shop.service.model.enums.ShopError;
import com.medusa.gruul.shop.service.model.vo.DecorationTemplatePagesVO;
import com.medusa.gruul.shop.service.model.vo.ShopDecorationPageDetailsVO;
import com.medusa.gruul.shop.service.model.vo.ShopDecorationTemplateDetailsVO;
import com.medusa.gruul.shop.service.model.vo.ShopDecorationTemplatePageVO;
import com.medusa.gruul.shop.service.mp.entity.ShopDecorationPage;
import com.medusa.gruul.shop.service.mp.entity.ShopDecorationTemplate;
import com.medusa.gruul.shop.service.mp.mapper.ShopDecorationTemplateMapper;
import com.medusa.gruul.shop.service.mp.service.IShopDecorationPageService;
import com.medusa.gruul.shop.service.mp.service.IShopDecorationTemplateService;
import com.medusa.gruul.shop.service.properties.ShopConfigurationProperties;
import com.medusa.gruul.shop.service.service.ShopInfoService;
import com.medusa.gruul.shop.service.service.addon.ShopAddonSupporter;
import com.medusa.gruul.shop.service.util.ShopUtil;
import io.vavr.control.Option;
import io.vavr.control.Try;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;

/**
 * 店铺装修模板服务实现类
 *
 * @author Andy.Yan
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class ShopDecorationTemplateServiceImpl extends
        ServiceImpl<ShopDecorationTemplateMapper, ShopDecorationTemplate> implements IShopDecorationTemplateService {

    private final ShopInfoService shopInfoService;
    private final ShopAddonSupporter shopAddonSupporter;
    private final ShopConfigurationProperties shopConfigurationProperties;
    private final IShopDecorationPageService shopDecorationPageService;
    private IShopDecorationTemplateService shopDecorationTemplateService;

    /**
     * 创建店铺装修模板
     * todo 🤔 多线程需要加锁
     *
     * @param param 装修模板DTO
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void createTemplate(ShopDecorationTemplateCreateDTO param) {
        // 校验参数的合法性
        param.validParam();
        Long shopId = ISecurity.userMust().getShopId();
        //是否有同名模板
        ShopError.TEMPLATE_NAME_ALREADY_EXIST.trueThrow(
                TenantShop.disable(
                        () -> this.lambdaQuery()
                                .eq(ShopDecorationTemplate::getName, param.getName())
                                .eq(ShopDecorationTemplate::getShopId, shopId)
                                .eq(ShopDecorationTemplate::getBusinessType, param.getBusinessType())
                                .eq(ShopDecorationTemplate::getEndpointType, param.getEndpointType())
                                .exists()
                ));
        //是否所有页面都存在
        Set<Long> pageIds = param.getPages().stream().map(PageReference::getPageId).collect(Collectors.toSet());
        ShopError.THE_REFERENCED_PAGE_OF_TEMPLATE_NOT_EXIST.falseThrow(
                shopDecorationPageService.lambdaQuery()
                        .in(ShopDecorationPage::getId, pageIds)
                        .eq(ShopDecorationPage::getShopId, shopId)
                        .count().equals((long) pageIds.size())
        );
        // 创建模板
        ShopDecorationTemplate template = new ShopDecorationTemplate();
        //todo 禁用 BeanUtil
        BeanUtil.copyProperties(param, template);
        //如果没有其它模板启用 则把当前模板设置为启用状态
        boolean enabledExists = this.lambdaQuery()
                .eq(ShopDecorationTemplate::getEnabled, Boolean.TRUE)
                .eq(ShopDecorationTemplate::getShopId, shopId)
                .eq(ShopDecorationTemplate::getBusinessType, param.getBusinessType())
                .eq(ShopDecorationTemplate::getEndpointType, param.getEndpointType())
                .exists();
        template.setEnabled(!enabledExists);
        //保存模板数据
        ShopError.FAILED_TO_ADD_TEMPLATE.falseThrow(this.save(template));
    }

    /**
     * 分页查询店铺装修模板
     *
     * @param param 查询参数
     * @return {@link ShopDecorationTemplatePageVO}
     */
    @Override
    public IPage<ShopDecorationTemplatePageVO> pageQuery(ShopDecorationPageQueryDTO param) {
        Long shopId = ISecurity.userMust().getShopId();
        // 分页查询函数
        Supplier<Page<ShopDecorationTemplate>> pageSupplier = () -> TenantShop.disable(
                () -> this.lambdaQuery()
                        .eq(ShopDecorationTemplate::getShopId, shopId)
                        .eq(ShopDecorationTemplate::getBusinessType, param.getBusinessType())
                        .eq(ShopDecorationTemplate::getEndpointType, param.getEndpointType())
                        .orderByDesc(ShopDecorationTemplate::getEnabled)
                        .page(new Page<>(param.getCurrent(), param.getSize()))
        );
        //首次查询
        Page<ShopDecorationTemplate> page = pageSupplier.get();
        //如果查询不到 则复制平台模板 并重新查询
        if (page.getRecords().isEmpty() && param.getCurrent() == CommonPool.NUMBER_ONE) {
            page = Try.run(() -> shopDecorationTemplateService.copyPlatformTemplate(
                            shopId,
                            new DecorationCopyTemplateParamDTO()
                                    .setBusiness(param.getBusinessType())
                                    .setEndpoint(param.getEndpointType())
                    ))
                    .map(voi -> pageSupplier.get())
                    .getOrElseGet(
                            ex -> {
                                log.error("尝试同步平台模板是发生错误", ex);
                                return new Page<>();
                            }
                    );

        }
        //转换返回数据
        return page.convert(
                curPage -> {
                    ShopDecorationTemplatePageVO vo = new ShopDecorationTemplatePageVO();
                    //todo 禁用 BeanUtil
                    BeanUtil.copyProperties(curPage, vo);
                    return vo;
                }
        );
    }

    /**
     * 复制店铺模板
     * todo 🤔 多线程需要加锁
     *
     * @param param 源模板参数,参考{@link ShopDecorationTemplateCloneDTO}
     */
    @Override
    public void clone(ShopDecorationTemplateCloneDTO param) {
        // 检查模板是否存在
        ShopDecorationTemplate sourceTemplate = this.getById(param.getTemplateId());
        if (sourceTemplate == null) {
            throw ShopError.TEMPLATE_NOT_EXIST.exception();
        }
        RedisUtil.increment(
                ShopUtil.templateCopyCacheKey(sourceTemplate.getShopId(), sourceTemplate.getId()),
                CommonPool.NUMBER_ONE,
                (count) -> {
                    //拼接副本名称 eg. 模板名称_副本1, 模板名称_副本2
                    String clonedName = ShopUtil.copyName(sourceTemplate.getName(), count);
                    // 检查name长度
                    ShopError.TEMPLATE_NAME_LENGTH_TOO_LONG.trueThrow(clonedName.length() > CommonPool.NUMBER_TEN);
                    // 检查是否有同名模板
                    ShopError.TEMPLATE_NAME_ALREADY_EXIST.trueThrow(
                            this.lambdaQuery()
                                    .eq(ShopDecorationTemplate::getName, clonedName)
                                    .eq(ShopDecorationTemplate::getShopId, sourceTemplate.getShopId())
                                    .eq(ShopDecorationTemplate::getBusinessType, sourceTemplate.getBusinessType())
                                    .eq(ShopDecorationTemplate::getEndpointType, sourceTemplate.getEndpointType())
                                    .exists()
                    );
                    // 创建新模板
                    ShopDecorationTemplate newedTemplate = new ShopDecorationTemplate()
                            .setName(clonedName)
                            .setShopId(sourceTemplate.getShopId())
                            .setEnabled(Boolean.FALSE)
                            .setPages(sourceTemplate.getPages())
                            .setDescription(sourceTemplate.getDescription())
                            .setBusinessType(sourceTemplate.getBusinessType())
                            .setEndpointType(sourceTemplate.getEndpointType());
                    this.save(newedTemplate);
                }
        );

    }

    /**
     * 删除店铺模板
     * todo 🤔 多线程需要加锁
     *
     * @param param 参考{@link ShopDecorationTemplateDeleteDTO}
     */
    @Override
    @SuppressWarnings("unchecked")
    @Transactional(rollbackFor = Exception.class)
    public void delete(ShopDecorationTemplateDeleteDTO param) {
        Long shopId = ISecurity.userMust().getShopId();
        // 检查模板是否存在
        ShopDecorationTemplate template = TenantShop.disable(
                () -> this.lambdaQuery()
                        .eq(ShopDecorationTemplate::getId, param.getTemplateId())
                        .eq(ShopDecorationTemplate::getShopId, shopId)
                        .one()
        );
        if (template == null) {
            throw ShopError.TEMPLATE_NOT_EXIST.exception();
        }
        Boolean currentEnabled = template.getEnabled();
        RedisUtil.doubleDeletion(
                () -> {
                    //如果当前模板是启用状态
                    if (currentEnabled) {
                        // 则更新其它模板为启用状态
                        Boolean update = TenantShop.disable(
                                () -> this.lambdaUpdate()
                                        .set(ShopDecorationTemplate::getEnabled, Boolean.TRUE)
                                        .ne(ShopDecorationTemplate::getId, template.getId())
                                        .eq(ShopDecorationTemplate::getShopId, template.getShopId())
                                        .eq(ShopDecorationTemplate::getBusinessType, template.getBusinessType())
                                        .eq(ShopDecorationTemplate::getEndpointType, template.getEndpointType())
                                        .last(SqlHelper.SQL_LIMIT_1)
                                        .update()
                        );
                        ShopError.TEMPLATE_CAN_NOT_DELETE.falseThrow(update);
                    }
                    // 并且删除当前模板
                    ShopError.FAILED_TO_DELETE_TEMPLATE.falseThrow(this.removeById(param.getTemplateId()));
                },
                // 删除缓存
                () -> RedisUtil.executePipelined(
                        operations -> {
                            //1. 如果当前模板是启用状态，则删除当前缓存页面数据
                            if (currentEnabled) {
                                Collection<String> pageKeys = RedisUtil.keys(
                                        ShopUtil.pageCacheKeyPattern(
                                                template.getShopId(), template.getEndpointType()
                                        )
                                );
                                operations.delete(pageKeys);
                            }
                            //2. 删除模板副本数缓存
                            operations.delete(
                                    ShopUtil.templateCopyCacheKey(template.getShopId(), template.getId())
                            );
                        }
                )
        );

    }

    /**
     * 编辑店铺模板
     *
     * @param dto 参考{@link ShopDecorationTemplateModifyDTO}
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void modify(ShopDecorationTemplateModifyDTO dto) {
        // 检查模板是否存在
        ShopDecorationTemplate template = this.lambdaQuery().eq(ShopDecorationTemplate::getId, dto.getId()).one();
        if (template == null) {
            throw ShopError.TEMPLATE_NOT_EXIST.exception();
        }
        // 检查模板名称是否重复
        ShopError.TEMPLATE_NAME_ALREADY_EXIST.trueThrow(
                this.lambdaQuery()
                        .ne(ShopDecorationTemplate::getId, dto.getId())
                        .eq(ShopDecorationTemplate::getName, dto.getName())
                        .eq(ShopDecorationTemplate::getBusinessType, template.getBusinessType())
                        .eq(ShopDecorationTemplate::getEndpointType, template.getEndpointType())
                        .exists()
        );
        RedisUtil.doubleDeletion(
                // 修改模板
                () -> ShopError.FAILED_TO_UPDATE_TEMPLATE.falseThrow(
                        this.lambdaUpdate()
                                .set(ShopDecorationTemplate::getName, dto.getName())
                                .set(ShopDecorationTemplate::getPages, JSON.toJSONString(dto.getPages()))
                                .set(ShopDecorationTemplate::getDescription, dto.getDescription())
                                .eq(ShopDecorationTemplate::getId, dto.getId())
                                .update()
                ),
                // 删除当前页面缓存
                () -> {
                    //如果当前模板是启用状态
                    if (template.getEnabled()) {
                        //删除缓存的页面数据
                        RedisUtil.delete(
                                RedisUtil.keys(
                                        ShopUtil.pageCacheKeyPattern(template.getShopId(), template.getEndpointType())
                                )
                        );
                    }
                }
        );


    }

    /**
     * 获取装修模板详情
     *
     * @param templateId 装修模板ID
     * @return {@link ShopDecorationTemplateDetailsVO}
     */
    @Override
    public ShopDecorationTemplateDetailsVO getShopDecorationTemplateById(Long templateId) {
        // 检查模板是否存在
        ShopDecorationTemplate sourceTemplate = this.getById(templateId);
        ShopError.TEMPLATE_NOT_EXIST.trueThrow(sourceTemplate == null);
        ShopDecorationTemplateDetailsVO result = new ShopDecorationTemplateDetailsVO();
        //todo 禁用 BeanUtil
        BeanUtil.copyProperties(sourceTemplate, result);
        return result;
    }

    /**
     * 修改模板启用状态 启用当前模板
     * todo 🤔 多线程需要加锁
     *
     * @param dto 参考{@link ShopDecorationTemplateChangeStatusDTO}
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void modifyTemplateStatus(ShopDecorationTemplateChangeStatusDTO dto) {
        Long shopId = ISecurity.userMust().getShopId();
        // 检查模板是否存在
        ShopDecorationTemplate template = TenantShop.disable(
                () -> this.lambdaQuery()
                        .eq(ShopDecorationTemplate::getId, dto.getTemplateId())
                        .eq(ShopDecorationTemplate::getShopId, shopId)
                        .one()
        );
        if (template == null) {
            throw ShopError.TEMPLATE_NOT_EXIST.exception();
        }
        //如果当前已是启用状态 则不需要再次启用
        if (template.getEnabled()) {
            return;
        }
        // 如果存在已经启用的模版,则禁用它们
        RedisUtil.doubleDeletion(
                () -> {
                    //禁用其它模板
                    TenantShop.disable(
                            () -> this.lambdaUpdate()
                                    .set(ShopDecorationTemplate::getEnabled, Boolean.FALSE)
                                    .eq(ShopDecorationTemplate::getEnabled, Boolean.TRUE)
                                    .eq(ShopDecorationTemplate::getShopId, shopId)
                                    .eq(ShopDecorationTemplate::getBusinessType, template.getBusinessType())
                                    .eq(ShopDecorationTemplate::getEndpointType, template.getEndpointType())
                                    .ne(ShopDecorationTemplate::getId, dto.getTemplateId())
                                    .update()
                    );
                    // 启用模板
                    ShopError.FAILED_TO_CHANGE_TEMPLATE_STATUS.falseThrow(
                            TenantShop.disable(
                                    () -> this.lambdaUpdate()
                                            .set(ShopDecorationTemplate::getEnabled, Boolean.TRUE)
                                            .eq(ShopDecorationTemplate::getEnabled, Boolean.FALSE)
                                            .eq(ShopDecorationTemplate::getShopId, shopId)
                                            .eq(ShopDecorationTemplate::getId, dto.getTemplateId())
                                            .update()
                            )
                    );
                },
                () -> {
                    //删除缓存的页面数据
                    RedisUtil.delete(
                            RedisUtil.keys(
                                    ShopUtil.pageCacheKeyPattern(template.getShopId(), template.getEndpointType())
                            )
                    );
                }
        );
    }

    /**
     * 获取店铺启用的模板对象
     * todo  有啥用啊这个 不知道 哪里能用到？
     *
     * @return {@link ShopDecorationTemplateDetailsVO}
     */
    @Override
    public ShopDecorationTemplateDetailsVO getOpeningUpTemplate() {
        // 检查模板是否存在
        ShopDecorationTemplate openingUpTemplate = TenantShop.disable(() -> this.lambdaQuery()
                .eq(ShopDecorationTemplate::getShopId, ISecurity.userMust().getShopId())
                .eq(ShopDecorationTemplate::getEnabled, Boolean.TRUE)
                .one()
        );
        ShopError.TEMPLATE_NOT_EXIST.trueThrow(openingUpTemplate == null);
        ShopDecorationTemplateDetailsVO result = new ShopDecorationTemplateDetailsVO();
        //todo 禁用 BeanUtil
        BeanUtil.copyProperties(openingUpTemplate, result);
        return result;
    }

    /**
     * 根据页面类型获取页面集合
     *
     * @param businessType 业务类型 {@link TemplateBusinessTypeEnum}
     * @param endpointType 终端类型 {@link DecorationEndpointTypeEnum}
     * @return {@link DecorationTemplatePagesVO}
     */
    @Override
    public DecorationTemplatePagesVO listPages(TemplateBusinessTypeEnum businessType,
                                               DecorationEndpointTypeEnum endpointType) {
        // 获取与店铺匹配的页面集合
        List<ShopDecorationPage> pages = shopDecorationPageService.listPageByShopIdAndPageType(businessType,
                endpointType);
        if (CollUtil.isEmpty(pages)) {
            return new DecorationTemplatePagesVO();
        }
        // key:页面类型;value:与类型匹配的页面集合
        Map<PageTypeEnum, List<ShopDecorationPage>> map = pages.stream()
                .collect(Collectors.groupingBy(ShopDecorationPage::getType));
        //根据指定类型获取页面集合函数
        Function<PageTypeEnum, List<DecorationTemplatePagesVO.PageVO>> pagesFunction =
                pageTypeEnum -> Option.of(map.get(pageTypeEnum))
                        .map(currPages -> currPages.stream()
                                .map(item -> DecorationTemplatePagesVO.PageVO.of(item.getId(), item.getName()))
                                .toList()
                        ).getOrElse(List.of());
        //渲染数据
        return new DecorationTemplatePagesVO()
                .setShopHomePages(pagesFunction.apply(PageTypeEnum.SHOP_HOME_PAGE))
                .setShopCategoryPages(pagesFunction.apply(PageTypeEnum.SHOP_CATEGORY_PAGE))
                .setShopBottomNavigationPages(pagesFunction.apply(PageTypeEnum.SHOP_BOTTOM_NAVIGATION_PAGE))
                .setShopCustomPages(pagesFunction.apply(PageTypeEnum.SHOP_CUSTOMIZED_PAGE));
    }

    /**
     * 获取店铺启用的模板所引用的页面对象
     *
     * @param shopId       店铺ID
     * @param endpointType 业务类型
     * @param pageType     页面类型
     * @return {@link ShopDecorationPageDetailsVO}
     */
    @Override
    public ShopDecorationPageDetailsVO getShopPageByEnabledTemplate(
            Long shopId,
            DecorationEndpointTypeEnum endpointType,
            PageTypeEnum pageType,
            String customType
    ) {
        return RedisUtil.getCacheMap(
                ShopDecorationPageDetailsVO.class,
                () -> {
                    //如果是 PC端 直接查询页面数据 PC 端只有一个装修页面
                    if (DecorationEndpointTypeEnum.PC_MALL == endpointType) {
                        ShopDecorationPage sourcePage = TenantShop.disable(() ->
                                shopDecorationPageService.lambdaQuery()
                                        .eq(ShopDecorationPage::getShopId, shopId)
                                        .eq(ShopDecorationPage::getEndpointType, endpointType)
                                        .eq(ShopDecorationPage::getType, pageType)
                                        .eq(StrUtil.isNotEmpty(customType), ShopDecorationPage::getCustomType, customType)
                                        .last(SqlHelper.SQL_LIMIT_1)
                                        .one()
                        );
                        if (sourcePage == null) {
                            return null;
                        }
                        ShopDecorationPageDetailsVO result = new ShopDecorationPageDetailsVO();
                        //todo 禁用 BeanUtil
                        BeanUtil.copyProperties(sourcePage, result);
                        return result;
                    }
                    // 获取店铺启用的模板
                    ShopDecorationTemplate enabledTemplate = TenantShop.disable(() -> this.lambdaQuery()
                            .eq(ShopDecorationTemplate::getShopId, shopId)
                            .eq(ShopDecorationTemplate::getEndpointType, endpointType)
                            .eq(ShopDecorationTemplate::getEnabled, Boolean.TRUE)
                            .orderByDesc(ShopDecorationTemplate::getEnabled)
                            .last(SqlHelper.SQL_LIMIT_1)
                            .one());
                    //如果当前装修模板为空 则复制平台装修模板
                    if (enabledTemplate == null) {
                        enabledTemplate = shopDecorationTemplateService.copyPlatformTemplate(
                                shopId,
                                new DecorationCopyTemplateParamDTO().setEndpoint(endpointType)
                        );
                    }
                    //如果此时模板仍不存在 则抛出异常
                    if (enabledTemplate == null || enabledTemplate.getPages() == null) {
                        return null;
                    }
                    // 根据页面类型过滤
                    PageReference pageReference = enabledTemplate.getPages().stream()
                            .filter(page -> page.getPageType() == pageType).findFirst().orElse(null);
                    if (pageReference == null) {
                        return null;
                    }
                    // 查询页面详情并返回
                    try {
                        return ISystem.shopId(shopId, () -> this.shopDecorationPageService.getShopPageById(pageReference.getPageId()));
                    } catch (Exception ignored) {
                        return null;
                    }
                },
                shopConfigurationProperties.getPageCacheExpire(),
                ShopUtil.pageCacheKey(shopId, endpointType, pageType, customType)
        );
    }

    /**
     * 给指定店铺 id 的店铺复制平台的店铺装修模板
     * todo 🤔 多线程需要加锁
     *
     * @param shopId 店铺 id 不能为空
     * @param param  模板复制参数
     * @return {@link ShopDecorationTemplate} 店铺装修模板
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public ShopDecorationTemplate copyPlatformTemplate(Long shopId, DecorationCopyTemplateParamDTO param) {
        if (param.getBusiness() == null) {
            ShopMode shopMode = shopInfoService.getShopById(shopId).getOrElseThrow(ShopError.SHOP_NOT_EXIST::exception)
                    .getShopMode();
            param.setBusiness(ShopUtil.decorationBusiness(shopMode));
        }
        PlatformShopDecorationTemplate platformTemplate = shopAddonSupporter.platformTemplate(param);
        if (platformTemplate == null) {
            return null;
        }
        log.debug("开始创建从平台拉取的店铺模版，模板参数：{}", platformTemplate);
        // 所有店铺的页面对象集合&所有店铺的模版对象集合
        List<DecorationCopyPageDTO> pages = platformTemplate.getPages();
        TemplateBusinessTypeEnum business = param.getBusiness();
        DecorationEndpointTypeEnum endpoint = param.getEndpoint();
        List<ShopDecorationPage> shopPages = pages.stream()
                .map(page -> new ShopDecorationPage()
                        .setShopId(shopId)
                        .setName(page.getName())
                        .setRemark(ShopUtil.copiedDesc())
                        .setProperties(page.getProperties())
                        .setType(page.getType())
                        .setBusinessType(business)
                        .setEndpointType(endpoint)
                ).toList();
        // 保存装修页面数据 
        TenantShop.disable(() -> shopDecorationPageService.saveBatch(shopPages));
        //保存装修模板数据
        ShopDecorationTemplate template = new ShopDecorationTemplate()
                .setShopId(shopId)
                .setName(platformTemplate.getName())
                .setDescription(platformTemplate.getDescription())
                .setBusinessType(business)
                .setEndpointType(endpoint)
                .setEnabled(param.getId() == null)
                .setPages(
                        shopPages.stream()
                                .map(shopPage -> new PageReference()
                                        .setPageId(shopPage.getId())
                                        .setPageType(shopPage.getType())
                                ).toList()
                );
        TenantShop.disable(() -> this.save(template));
        return template;
    }


    @Autowired
    public void setShopDecorationTemplateService(IShopDecorationTemplateService shopDecorationTemplateService) {
        this.shopDecorationTemplateService = shopDecorationTemplateService;
    }
}
