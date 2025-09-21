package com.medusa.gruul.addon.platform.addon.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.collection.CollectionUtil;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.medusa.gruul.addon.platform.addon.PlatformAddonProvider;
import com.medusa.gruul.addon.platform.addon.PlatformAddonSupporter;
import com.medusa.gruul.addon.platform.model.enums.PlatformError;
import com.medusa.gruul.addon.platform.model.enums.TemplateTypeEnum;
import com.medusa.gruul.addon.platform.model.enums.WebParamKeyEnum;
import com.medusa.gruul.addon.platform.model.enums.WebParamModuleEnum;
import com.medusa.gruul.addon.platform.mp.entity.DecorationPage;
import com.medusa.gruul.addon.platform.mp.entity.DecorationTemplate;
import com.medusa.gruul.addon.platform.mp.entity.PlatformCategory;
import com.medusa.gruul.addon.platform.mp.entity.PlatformShopSigningCategory;
import com.medusa.gruul.addon.platform.mp.service.IDecorationPageService;
import com.medusa.gruul.addon.platform.mp.service.IDecorationTemplateService;
import com.medusa.gruul.addon.platform.mp.service.IPlatformCategoryService;
import com.medusa.gruul.addon.platform.mp.service.IPlatformShopSigningCategoryService;
import com.medusa.gruul.addon.platform.service.PlatformConfigService;
import com.medusa.gruul.common.addon.provider.AddonProvider;
import com.medusa.gruul.common.addon.provider.AddonProviders;
import com.medusa.gruul.common.model.resp.SystemCode;
import com.medusa.gruul.common.module.app.shop.ShopMode;
import com.medusa.gruul.common.mp.model.BaseEntity;
import com.medusa.gruul.common.mp.model.SqlHelper;
import com.medusa.gruul.global.model.constant.Services;
import com.medusa.gruul.global.model.exception.GlobalException;
import com.medusa.gruul.goods.api.enums.GoodsRabbit;
import com.medusa.gruul.goods.api.model.CategoryLevel;
import com.medusa.gruul.goods.api.model.dto.CategorySigningCustomDeductionRationMqDTO;
import com.medusa.gruul.goods.api.model.enums.ProductStatus;
import com.medusa.gruul.goods.api.model.vo.CategoryLevelName;
import com.medusa.gruul.goods.api.model.vo.PlatformCategoryVo;
import com.medusa.gruul.goods.api.rpc.GoodsRpcService;
import com.medusa.gruul.shop.api.enums.PageTypeEnum;
import com.medusa.gruul.shop.api.model.bo.PageReference;
import com.medusa.gruul.shop.api.model.dto.*;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.util.*;
import java.util.stream.Collectors;


/**
 * @author xiaoq
 */
@Slf4j
@AddonProviders
@DubboService
@Service
@RequiredArgsConstructor
public class PlatformAddonProviderImpl implements PlatformAddonProvider {

    private final IPlatformCategoryService platformCategoryService;
    private final IPlatformShopSigningCategoryService platformShopSigningCategoryService;
    private final GoodsRpcService goodsRpcService;
    private final PlatformAddonSupporter platformAddonSupporter;
    private final IDecorationPageService decorationPageService;

    private final RabbitTemplate rabbitTemplate;

    private final IDecorationTemplateService decorationTemplateService;

    private final PlatformConfigService platformConfigService;


    @Override
    @AddonProvider(service = Services.GRUUL_MALL_GOODS, supporterId = "goodsAddonSupporter", methodName = "getProductStatus")
    public ProductStatus getProductStatus(ProductStatus status) {
        log.debug("PlatformAddonProviderImpl invoking " + status.name());
        return ProductStatus.UNDER_REVIEW;
    }

    /**
     * 根据三级类目id获取对应的一级、二级类目id
     *
     * @param platformCategoryIdSet 三级类目id
     */
    @Override
    @AddonProvider(service = Services.GRUUL_MALL_GOODS, supporterId = "goodsAddonSupporter", methodName = "getPlatformCategoryVoByLevel3Id", order = 1)
    public PlatformCategoryVo getPlatformCategoryVoByLevel3Id(Set<Long> platformCategoryIdSet) {
        Map<Long, Long> platformCategories = getPlatformCategories(platformCategoryIdSet);
        if (platformCategories == null) {
            return null;
        }
        PlatformCategoryVo platformCategoryVo = new PlatformCategoryVo();
        platformCategoryVo.setThirdSecondMap(platformCategories);
        Map<Long, Long> secondFirstMap = getPlatformCategories(platformCategories.values());
        platformCategoryVo.setSecondFirstMap(secondFirstMap);
        return platformCategoryVo;

    }


    /**
     * 签约类目信息保存
     *
     * @param signingCategories 签约类目
     * @return 是否成功
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    @AddonProvider(service = Services.GRUUL_MALL_SHOP, supporterId = "shopSigningCategory", methodName = "editSingingCategory", order = 1)
    public Boolean editPlatformShopSigningCategory(List<SigningCategoryDTO> signingCategories,
                                                   Long shopId, ShopMode shopMode) {

        List<PlatformShopSigningCategory> existingSigningCategories = platformShopSigningCategoryService.lambdaQuery()
                .eq(PlatformShopSigningCategory::getShopId, shopId)
                .list();
        if (CollUtil.isNotEmpty(existingSigningCategories)) {
            //获取更新后 未删除的的签约类目id
            List<Long> existingSigningCategoryIds = Lists.newArrayList();
            if (CollectionUtil.isNotEmpty(signingCategories)) {
                existingSigningCategoryIds = signingCategories.stream()
                        .map(SigningCategoryDTO::getId)
                        .filter(Objects::nonNull)
                        .collect(Collectors.toList());
            }
            // 过滤出数据库中还有 但是已经被前端删除的签约类目
            List<Long> finalExistingSigningCategoryIds = existingSigningCategoryIds;

            List<PlatformShopSigningCategory> platformShopSigningCategories = existingSigningCategories.stream()
                    .filter(dbCategory -> !finalExistingSigningCategoryIds.contains(dbCategory.getId())).toList();

            if (CollUtil.isNotEmpty(platformShopSigningCategories)) {
                Set<Long> deleteSigningCategoryIds = platformShopSigningCategories.stream()
                        .map(PlatformShopSigningCategory::getCurrentCategoryId).collect(Collectors.toSet());

                boolean signingCategoryProduct = goodsRpcService.getSigningCategoryProduct(deleteSigningCategoryIds,
                        shopId);
                if (signingCategoryProduct) {
                    throw new GlobalException("当前删除签约类目下存在商品请联系商家删除");
                }
                Boolean supplierSigningCategoryProduct = platformAddonSupporter.getSupplierSigningCategoryProduct(
                        deleteSigningCategoryIds, shopId);
                if (supplierSigningCategoryProduct != null && supplierSigningCategoryProduct) {
                    throw new GlobalException("当前删除签约类目下存在商品请联系供应商删除");
                }
                List<Long> needDeleteIds = platformShopSigningCategories.stream()
                        .map(PlatformShopSigningCategory::getId).collect(
                                Collectors.toList());
                platformShopSigningCategoryService.removeByIds(needDeleteIds);
            }
        }

        //新增
        List<PlatformShopSigningCategory> addSigningCategory = signingCategories.stream()
                .filter(bean -> bean.getId() == null)
                .map(bean -> convertToPlatformShopSigningCategory(bean, shopId))
                .collect(Collectors.toList());
        platformShopSigningCategoryService.saveBatch(addSigningCategory);
        //获取数据库中店铺签约类目信息
        List<SigningCategoryDTO> updateSignCategoryDtos = signingCategories.stream()
                .filter(x -> Objects.nonNull(x.getId())).collect(Collectors.toList());
        List<CategorySigningCustomDeductionRationMqDTO> signingCategoryCustomDeductionRationMqDtos = Lists.newArrayList();
        if (!CollectionUtils.isEmpty(updateSignCategoryDtos)) {
            List<Long> shopSigningCategoryIds = updateSignCategoryDtos.stream().map(x -> x.getId()).collect(Collectors.toList());
            List<PlatformShopSigningCategory> list = platformShopSigningCategoryService.lambdaQuery()
                    .in(PlatformShopSigningCategory::getId, shopSigningCategoryIds)
                    .list();
            Map<Long, Long> dbCategoryCustomDeductionRatioMap = Maps.newHashMap();
            for (PlatformShopSigningCategory platformShopSigningCategory : list) {
                dbCategoryCustomDeductionRatioMap.put(platformShopSigningCategory.getId(),
                        platformShopSigningCategory.getCustomDeductionRatio());
            }
            CategorySigningCustomDeductionRationMqDTO dto = new CategorySigningCustomDeductionRationMqDTO();
            //比较数据库中签约类目信息和前端传入的签约类目信息 获取出扣率变化的类目
            for (SigningCategoryDTO updateSignCategoryDto : updateSignCategoryDtos) {
                if (!Objects.equals(updateSignCategoryDto.getCustomDeductionRatio(),
                        dbCategoryCustomDeductionRatioMap.get(updateSignCategoryDto.getId()))) {
                    dto.setSecondCategoryId(updateSignCategoryDto.getCurrentCategoryId());
                    dto.setDeductionRation(updateSignCategoryDto.getCustomDeductionRatio());
                    dto.setShopId(shopId);
                    dto.setShopMode(shopMode);
                    signingCategoryCustomDeductionRationMqDtos.add(dto);
                }
            }


        }

        //修改
        List<PlatformShopSigningCategory> updateSigningCategory = signingCategories.stream()
                .filter(bean -> bean.getId() != null)
                .map(bean -> {
                    PlatformShopSigningCategory platformShopSigningCategory =
                            convertToPlatformShopSigningCategory(bean, shopId);
                    platformShopSigningCategory.setId(bean.getId());
                    return platformShopSigningCategory;
                })
                .collect(Collectors.toList());

        platformShopSigningCategoryService.updateBatchById(updateSigningCategory);
        if (CollUtil.isNotEmpty(updateSigningCategory)) {
            // 修改签约类目 应通知商品修改类目信息
            log.error("修改签约类目");
        }

        for (CategorySigningCustomDeductionRationMqDTO dto : signingCategoryCustomDeductionRationMqDtos) {
            //发送更新商品类目扣率的MQ
            rabbitTemplate.convertAndSend(GoodsRabbit.EXCHANGE,
                    GoodsRabbit.GOODS_CUSTOM_DEDUCTION_RATIO_CHANGE.routingKey(),
                    dto);
        }

        return Boolean.TRUE;
    }

    @Override
    @AddonProvider(service = Services.GRUUL_MALL_GOODS, supporterId = "goodsAddonSupporter", methodName = "getCustomDeductionRatio")
    public Long getCustomDeductionRatio(Long platformTwoCategory, Long shopId) {
        PlatformShopSigningCategory shopSigningCategory = platformShopSigningCategoryService.lambdaQuery()
                .select(PlatformShopSigningCategory::getCustomDeductionRatio, BaseEntity::getId)
                .eq(PlatformShopSigningCategory::getCurrentCategoryId, platformTwoCategory)
                .eq(PlatformShopSigningCategory::getShopId, shopId).one();
        if (shopSigningCategory == null) {
            throw new GlobalException("当前类目暂未无签约,请刷新后选择");
        }
        //先获取店铺的类目签约比例 如果店铺没有设置类目签约比例 则获取平台的签约比例
        if (Objects.nonNull(shopSigningCategory.getCustomDeductionRatio())) {
            return shopSigningCategory.getCustomDeductionRatio();
        }
        // 平台类目签约信息
        PlatformCategory platformCategory = platformCategoryService.getById(platformTwoCategory);
        SystemCode.DATA_NOT_EXIST.trueThrow(Objects.isNull(platformCategory));
        return platformCategory.getDeductionRatio();
    }

    /**
     * 获取店铺签约类目ids
     *
     * @param shopId 店铺id
     * @return 二级类目ids
     */
    @Override
    @AddonProvider(service = Services.GRUUL_MALL_GOODS, supporterId = "goodsAddonSupporter", methodName = "getSigningCategoryIds")
    public Set<Long> getSigningCategoryIds(Long shopId) {
        List<PlatformShopSigningCategory> platformShopSigningCategories =
                platformShopSigningCategoryService.lambdaQuery()
                        .eq(PlatformShopSigningCategory::getShopId, shopId)
                        .list();
        if (CollUtil.isNotEmpty(platformShopSigningCategories)) {
            return platformShopSigningCategories.stream()
                    .map(PlatformShopSigningCategory::getCurrentCategoryId)
                    .collect(Collectors.toSet());
        }
        return Collections.emptySet();
    }

    /**
     * 获取平台类目级别名称
     *
     * @param platformCategory 平台类目级别id
     * @return 平台类目级别名称
     */
    @Override
    @AddonProvider(service = Services.GRUUL_MALL_GOODS, supporterId = "goodsAddonSupporter", methodName = "getPlatformCategoryLevelName")
    public CategoryLevelName getPlatformCategoryLevelName(CategoryLevel platformCategory) {
        return platformCategoryService.getPlatformCategoryLevelName(platformCategory);
    }


    @Override
    @AddonProvider(service = Services.GRUUL_MALL_SHOP, supporterId = "shopSigningCategory", methodName = "platformTemplate")
    public PlatformShopDecorationTemplate platformTemplate(@NotNull @Valid DecorationCopyTemplateParamDTO param) {
        DecorationTemplate template = decorationTemplateService.lambdaQuery()
                .eq(DecorationTemplate::getBusinessType, param.getBusiness())
                .eq(DecorationTemplate::getEndpointType, param.getEndpoint())
                .eq(DecorationTemplate::getTemplateType, TemplateTypeEnum.SHOP)
                .eq(param.getId() != null, BaseEntity::getId, param.getId())
                .last(SqlHelper.SQL_LIMIT_1)
                .one();
        if (template == null) {
            throw PlatformError.TEMPLATE_NOT_EXIST.exception();
        }
        List<Long> pageIds = template.getPages()
                .stream()
                .filter(e -> e.getPageType() != PageTypeEnum.SAME_CITY_MALL_HOME_PAGE).map(PageReference::getPageId)
                .collect(Collectors.toList());
        List<DecorationPage> pages = this.decorationPageService.listPageByIds(pageIds);
        return new PlatformShopDecorationTemplate()
                .setName(template.getName())
                .setDescription(template.getDescription())
                .setPages(
                        pages.stream()
                                .map(
                                        page -> {
                                            DecorationCopyPageDTO result = new DecorationCopyPageDTO();
                                            result.setName(page.getName());
                                            result.setType(page.getType());
                                            result.setProperties(page.getProperties());
                                            return result;
                                        }
                                )
                                .collect(Collectors.toList())
                );
    }


    /**
     * 根据装修页面参数复制平台装修页面
     *
     * @param param 页面参数
     * @return {@link DecorationCopyPageDTO} 装修页面数据
     */
    @Override
    @AddonProvider(service = Services.GRUUL_MALL_SHOP, supporterId = "shopSigningCategory", methodName = "platformPage")
    public DecorationCopyPageDTO platformPage(@NotNull @Valid DecorationCopyPageParamDTO param) {
        DecorationPage page = decorationPageService.lambdaQuery()
                .select(DecorationPage::getName, DecorationPage::getType, DecorationPage::getProperties)
                .eq(param.getId() != null, DecorationPage::getId, param.getId())
                .eq(DecorationPage::getBusinessType, param.getBusiness())
                .eq(DecorationPage::getEndpointType, param.getEndpoint())
                .eq(DecorationPage::getType, param.getPageType())
                .eq(DecorationPage::getTemplateType, TemplateTypeEnum.SHOP)
                .last(SqlHelper.SQL_LIMIT_1)
                .one();
        if (page == null) {
            throw PlatformError.PAGE_NOT_EXIST.exception();
        }
        return new DecorationCopyPageDTO()
                .setName(page.getName())
                .setType(page.getType())
                .setProperties(page.getProperties());
    }

    @Override
    @AddonProvider(service = Services.GRUUL_MALL_ORDER, supporterId = "orderPlatform",
            methodName = "platformName")
    public String getPlatformName() {
        Map<String, String> stringStringMap = platformConfigService.queryConfigParamByModuleList(List.of(WebParamModuleEnum.PUBLIC_SETTING));
        return stringStringMap.get(WebParamKeyEnum.PLATFORM_NAME.name());

    }


    private PlatformShopSigningCategory convertToPlatformShopSigningCategory(SigningCategoryDTO bean, Long shopId) {
        return new PlatformShopSigningCategory()
                .setShopId(shopId)
                .setParentId(bean.getParentId())
                .setCurrentCategoryId(bean.getCurrentCategoryId())
                .setCustomDeductionRatio(bean.getCustomDeductionRatio());
    }


    private Map<Long, Long> getPlatformCategories(Collection<Long> platformCategoryIdSet) {
        return CollectionUtil.emptyIfNull(platformCategoryService.lambdaQuery()
                .select(PlatformCategory::getId, PlatformCategory::getParentId)
                .in(PlatformCategory::getId, platformCategoryIdSet).
                list()).stream().collect(Collectors.toMap(PlatformCategory::getId, PlatformCategory::getParentId));
    }
}
