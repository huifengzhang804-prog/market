package com.medusa.gruul.addon.platform.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.map.MapUtil;
import com.medusa.gruul.addon.platform.addon.PlatformAddonSupporter;
import com.medusa.gruul.addon.platform.model.vo.CategoryVO;
import com.medusa.gruul.addon.platform.model.vo.SigningCategoryVO;
import com.medusa.gruul.addon.platform.mp.entity.PlatformCategory;
import com.medusa.gruul.addon.platform.mp.entity.PlatformShopSigningCategory;
import com.medusa.gruul.addon.platform.mp.service.IPlatformCategoryService;
import com.medusa.gruul.addon.platform.mp.service.IPlatformShopSigningCategoryService;
import com.medusa.gruul.addon.platform.service.SigningCategoryService;
import com.medusa.gruul.common.custom.aggregation.classify.enums.CategoryLevel;
import com.medusa.gruul.common.model.constant.CommonPool;
import com.medusa.gruul.common.mp.model.BaseEntity;
import com.medusa.gruul.common.security.model.bean.SecureUser;
import com.medusa.gruul.common.security.model.enums.Roles;
import com.medusa.gruul.common.security.resource.helper.ISecurity;
import com.medusa.gruul.global.model.exception.GlobalException;
import com.medusa.gruul.goods.api.rpc.GoodsRpcService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 签约类目实现层
 *
 * @author xiaoq
 * @Description SigningCategoryServiceImpl.java
 * @date 2023-05-15 09:44
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class SigningCategoryServiceImpl implements SigningCategoryService {
    private final IPlatformShopSigningCategoryService platformShopSigningCategoryService;
    private final IPlatformCategoryService categoryService;
    private final GoodsRpcService goodsRpcService;
    private final PlatformAddonSupporter platformAddonSupporter;
    private SigningCategoryVO signingCategoryVO;

    /**
     * 删除签约类目
     *
     * @param signingCategoryIds 签约类目ids
     */
    @Override
    public void delSigningCategory(Set<Long> signingCategoryIds) {
        Long shopId = ISecurity.userMust().getShopId();
        List<PlatformShopSigningCategory> list = platformShopSigningCategoryService.lambdaQuery()
                .eq(PlatformShopSigningCategory::getShopId, shopId)
                .in(BaseEntity::getId, signingCategoryIds)
                .list();

        if (CollUtil.isEmpty(list)) {
            throw new GlobalException("删除签约类目不存在");
        }
        // 获取要删除的签约类目
        Set<Long> collect = list.stream()
                .map(PlatformShopSigningCategory::getCurrentCategoryId)
                .collect(Collectors.toSet());
        ISecurity.match().ifAnyShopAdmin(
                secureUser -> {
                    // 店铺校验
                    boolean signingCategoryProduct = goodsRpcService.getSigningCategoryProduct(collect, shopId);
                    if (signingCategoryProduct) {
                        throw new GlobalException("当前店铺签约类目下存在商品不可删除");
                    }
                }

        ).when(
                secureUser -> {
                    // 供应商校验
                    Boolean signingCategoryProduct = platformAddonSupporter.getSupplierSigningCategoryProduct(collect, shopId);
                    if (signingCategoryProduct) {
                        throw new GlobalException("当前店铺签约类目下存在商品不可删除");
                    }
                },
                Roles.SUPPLIER_ADMIN, Roles.SUPPLIER_CUSTOM_ADMIN
        );


        boolean remove = platformShopSigningCategoryService.removeByIds(list);
        if (!remove) {
            throw new GlobalException("签约类目删除失败,清稍后再试");
        }

    }

    /**
     * 新增签约类目
     *
     * @param currentCategoryId 签约平台类目ids
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void addSigningCategory(Set<Long> currentCategoryId) {
        Long shopId = ISecurity.userMust().getShopId();
        // 获取已有的签约类目信息
        List<PlatformShopSigningCategory> platformShopSigningCategoryList =
                platformShopSigningCategoryService.lambdaQuery()
                        .eq(PlatformShopSigningCategory::getShopId, shopId)
                        .in(PlatformShopSigningCategory::getCurrentCategoryId, currentCategoryId)
                        .list();
        /*获取新增的二级类目信息
         */
        List<Long> collect = currentCategoryId
                .stream()
                .filter(currentCategory -> platformShopSigningCategoryList.stream()
                        .noneMatch(signingCategory ->
                                signingCategory.getCurrentCategoryId().equals(currentCategory)))
                .toList();
        if (CollUtil.isEmpty(collect)) {
            throw new GlobalException("暂无可添加的签约类目");
        }

        // 获取类目信息
        List<PlatformCategory> platformCategoryList = categoryService.lambdaQuery()
                .eq(PlatformCategory::getLevel, CategoryLevel.LEVEL_2)
                .in(BaseEntity::getId, collect)
                .list();
        if (CollUtil.isEmpty(platformCategoryList) && collect.size() != platformCategoryList.size()) {
            throw new GlobalException("添加签约类目与平台类目不一致，请稍后再试");
        }

        List<PlatformShopSigningCategory> signingCategoryList =
                platformCategoryList.stream()
                        .map(platformCategory -> new PlatformShopSigningCategory()
                                .setCurrentCategoryId(platformCategory.getId())
                                .setParentId(platformCategory.getParentId())
                                .setCustomDeductionRatio(platformCategory.getDeductionRatio())
                                .setShopId(shopId))
                        .collect(Collectors.toList());
        platformShopSigningCategoryService.saveBatch(signingCategoryList);
    }

    /**
     * 获取签约类目信息
     *
     * @return List<签约类目VO>
     */
    @Override
    public List<SigningCategoryVO> getSigningCategoryList(Long shopId, Boolean parentFlag) {
        boolean match = ISecurity.matcher()
                .any(SecureUser::getRoles, Roles.SUPER_ADMIN, Roles.SUPER_CUSTOM_ADMIN)
                .match();
        List<SigningCategoryVO> list = platformShopSigningCategoryService.getSigningCategoryListByShopId(
                shopId, match);
        if (CollectionUtils.isEmpty(list)) {
            return CollUtil.newArrayList();
        }
        //需要获取平台的店铺类目信息
        if (parentFlag) {
            //获取店铺中没有自定义扣率的类目
            List<Long> categoryIds = list.stream()
                    .filter(x -> Objects.isNull(x.getCustomDeductionRatio()))
                    .map(SigningCategoryVO::getCurrentCategoryId)
                    .collect(Collectors.toList());
            if (CollectionUtils.isEmpty(categoryIds)) {
                return list;
            }
            List<PlatformCategory> platformCategoryList = categoryService.lambdaQuery()
                    .eq(PlatformCategory::getDeleted, CommonPool.NUMBER_ZERO)
                    .in(PlatformCategory::getId, categoryIds).list();

            if (!CollectionUtils.isEmpty(platformCategoryList)) {
                //利用平台类目信息，补充自定义扣率
                Map<Long, Long> map = MapUtil.newHashMap(platformCategoryList.size());
                for (PlatformCategory platformCategory : platformCategoryList) {
                    map.put(platformCategory.getId(), platformCategory.getDeductionRatio());
                }
                for (SigningCategoryVO signingCategoryVO : list) {
                    if (Objects.isNull(signingCategoryVO.getCustomDeductionRatio())) {
                        signingCategoryVO.setCustomDeductionRatio(map.getOrDefault(signingCategoryVO.getCurrentCategoryId()
                                , Long.valueOf(CommonPool.NUMBER_ZERO)));
                    }
                }
            }
        }
        return list;
    }

    @Override
    public List<CategoryVO> getChoosableCategoryInfo(Long shopId) {
        return platformShopSigningCategoryService.getChoosableCategoryInfo(shopId);
    }

    @Override
    public void clearShopSigningCategory(Set<Long> shopIds) {
        if (CollUtil.isNotEmpty(shopIds)) {
            platformShopSigningCategoryService.lambdaUpdate()
                    .in(PlatformShopSigningCategory::getShopId, shopIds)
                    .remove();
            log.error("已删除店铺签约类目，shopIds: {}", shopIds);
        }
    }
}
