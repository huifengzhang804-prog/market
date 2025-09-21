package com.medusa.gruul.addon.platform.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.collection.CollectionUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.medusa.gruul.addon.platform.functions.PlatformCategoryQueryFunction;
import com.medusa.gruul.addon.platform.model.dto.CategoryDTO;
import com.medusa.gruul.addon.platform.model.dto.PlatformCategoryRankDTO;
import com.medusa.gruul.addon.platform.model.dto.PlatformCategorySortDTO;
import com.medusa.gruul.addon.platform.model.parpm.CategoryParam;
import com.medusa.gruul.addon.platform.model.vo.CategoryFirstIdWithNumVO;
import com.medusa.gruul.addon.platform.model.vo.CategoryVO;
import com.medusa.gruul.addon.platform.mp.entity.PlatformCategory;
import com.medusa.gruul.addon.platform.mp.entity.PlatformShopSigningCategory;
import com.medusa.gruul.addon.platform.mp.mapper.CategoryMapper;
import com.medusa.gruul.addon.platform.mp.service.IPlatformCategoryService;
import com.medusa.gruul.addon.platform.mp.service.IPlatformShopSigningCategoryService;
import com.medusa.gruul.addon.platform.service.PlatformCategoryService;
import com.medusa.gruul.common.custom.aggregation.classify.dto.CategoryRankDTO;
import com.medusa.gruul.common.custom.aggregation.classify.enums.CategoryLevel;
import com.medusa.gruul.common.model.constant.CommonPool;
import com.medusa.gruul.common.model.resp.SystemCode;
import com.medusa.gruul.common.mp.model.BaseEntity;
import com.medusa.gruul.common.mp.model.TenantShop;
import com.medusa.gruul.global.model.exception.GlobalException;
import com.medusa.gruul.goods.api.model.dto.CategorySigningCustomDeductionRationMqDTO;
import com.medusa.gruul.goods.api.model.vo.ApiProductVO;
import com.medusa.gruul.goods.api.rpc.GoodsRpcService;
import com.medusa.gruul.search.api.enums.CategoryCountType;
import com.medusa.gruul.search.api.enums.SearchRabbit;
import com.medusa.gruul.search.api.model.CategoryCountParam;
import com.medusa.gruul.search.api.model.CategoryStaticVo;
import com.medusa.gruul.search.api.model.NestedCategory;
import com.medusa.gruul.search.api.rpc.SearchRpcService;
import com.medusa.gruul.shop.api.enums.ShopRabbit;
import com.medusa.gruul.shop.api.model.vo.ShopInfoVO;
import com.medusa.gruul.shop.api.rpc.ShopRpcService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;


/**
 * Description: 类目信息 ServiceImpl
 *
 * @author xiaoq
 * @since 2022-04-18 15:01
 */
@Slf4j
@Service("platformCategoryService")
@RequiredArgsConstructor
public class PlatformCategoryServiceImpl implements PlatformCategoryService {


    private final RabbitTemplate rabbitTemplate;
    private final CategoryMapper categoryMapper;
    private final ShopRpcService shopRpcService;
    private final GoodsRpcService goodsRpcService;
    private final SearchRpcService searchRpcService;
    private final IPlatformCategoryService platformCategoryService;
    private final PlatformCategoryQueryFunction categoryQueryFunction;
    private final IPlatformShopSigningCategoryService platformShopSigningCategoryService;


    /**
     * 一级类目新增,先判断一级类目名称是否已存在
     *
     * @param categoryDto 类目dto
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void addCategory(CategoryDTO categoryDto) {
        categoryDto.paramCheck();
        categoryDto.save(platformCategoryService);
    }


    @Override
    public void updateCategory(CategoryDTO categoryDto) {
        categoryDto.paramCheck();
        categoryDto.saveOrUpdate(platformCategoryService, categoryDto.getId());
        if (categoryDto.getId() == null) {
            return;
        }
        //更新操作 查询店铺签约了类目 但是没有使用自定义抵扣比例类目信息
        List<PlatformShopSigningCategory> list = platformShopSigningCategoryService.queryNonSigningCategory(
                categoryDto.getId());
        if (CollectionUtils.isEmpty(list)) {
            return;
        }
        // MQ循环发送消息
        list.forEach(platformShopSigningCategory -> {
            CategorySigningCustomDeductionRationMqDTO dto = new CategorySigningCustomDeductionRationMqDTO();
            dto.setShopId(platformShopSigningCategory.getShopId());
            dto.setSecondCategoryId(platformShopSigningCategory.getCurrentCategoryId());
            dto.setDeductionRation(categoryDto.getNameImgs().get(CommonPool.NUMBER_ZERO).getDeductionRatio());
            //发送更新商品类目扣率的MQ
            rabbitTemplate.convertAndSend(ShopRabbit.EXCHANGE, ShopRabbit.SHOP_SGINING_CATEGORY_CUSTOM_DEDUCTION_RATIO_CHANGE.routingKey(), dto);
        });
    }


    /**
     * 类目删除 有子类目删子类目
     *
     * @param id 类目id
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteCategory(Long id) {
        PlatformCategory platformCategory = categoryMapper.selectById(id);
        if (platformCategory == null) {
            throw new GlobalException(SystemCode.DATA_EXISTED.getCode(), "当前分类不存在");
        }
        List<Long> ids;
        NestedCategory nestedCategory = new NestedCategory();
        // 判断删除类目是否为一级类目 如为一级类目同步删除该一级类目下的二级类目 及三级类目
        if (platformCategory.getLevel() == CategoryLevel.LEVEL_1) {
            nestedCategory.setOne(id);
            List<PlatformCategory> categories = categoryMapper.selectList(new QueryWrapper<PlatformCategory>().eq("parent_id", id));
            ids = categories.stream().map(PlatformCategory::getId).collect(Collectors.toList());
            // 判断是否有签约类目
            if (CollUtil.isNotEmpty(ids)) {
                isCategorySigned(ids);
                //删除三级类目
                categoryMapper.delete(new QueryWrapper<PlatformCategory>().in("parent_id", ids));
                //删除二级类目
                categoryMapper.delete(new QueryWrapper<PlatformCategory>().eq("parent_id", id));
            }
        }
        //判断删除类目是否为二级类目 删除二级类目下的三级类目
        if (platformCategory.getLevel() == CategoryLevel.LEVEL_2) {
            nestedCategory.setTwo(id);
            isCategorySigned(Stream.of(id).collect(Collectors.toList()));
            categoryMapper.delete(new QueryWrapper<PlatformCategory>().eq("parent_id", id));
        }
        if (platformCategory.getLevel() == CategoryLevel.LEVEL_3) {
            PlatformCategory thirdLevelCategory = categoryMapper.selectById(id);
            isCategorySigned(Stream.of(thirdLevelCategory.getParentId()).collect(Collectors.toList()));
            nestedCategory.setThree(id);
        }
        //删除自身分类
        categoryMapper.deleteById(id);
        rabbitTemplate.convertAndSend(SearchRabbit.EXCHANGE, SearchRabbit.CLASSIFY_REMOVE.routingKey(), nestedCategory);

    }

    /**
     * 获取类目信息
     *
     * @param page 分页对象
     * @return IPage<CategoryVO>
     */
    @Override
    public IPage<CategoryVO> getCategoryList(Page<?> page) {
        IPage<CategoryVO> categoryList = categoryMapper.getCategoryList(page);
        List<CategoryVO> records = categoryList.getRecords();
        if (CollUtil.isEmpty(records)) {
            return categoryList;
        }
        //获取类目商品数量统计
        Map<Long, CategoryStaticVo> categoryCountMap = searchRpcService.categoryCount(
                new CategoryCountParam()
                        .setType(CategoryCountType.PLATFORM)
                        .setFirstIds(
                                records.stream().map(CategoryVO::getId).collect(Collectors.toSet())
                        )
        );
        //默认的商品数量
        CategoryStaticVo defaultValue = new CategoryStaticVo();
        //设置商品数量
        records.forEach(
                //设置一级类目商品数量
                first -> {
                    first.setProductNumber(categoryCountMap.getOrDefault(first.getId(), defaultValue).getProductCount());
                    // 设置二级类目商品数量
                    CollUtil.emptyIfNull(first.getSecondCategoryVos())
                            .forEach(second -> {
                                        second.setProductNumber(categoryCountMap.getOrDefault(second.getId(), defaultValue).getProductCount());
                                        //设置三级类目商品数量
                                        CollUtil.emptyIfNull(second.getCategoryThirdlyVos())
                                                .forEach(third -> third.setProductNumber(categoryCountMap.getOrDefault(third.getId(),
                                                        defaultValue).getProductCount()));
                                    }
                            );

                });
        return categoryList;
    }


    /**
     * 修改平台类目排序  ORDER BY FIELD (id 按id 顺序排序
     *
     * @param platformCategorySortDto platformCategorySortDto
     */
    @Override
    public void updatePlatformCategorySort(PlatformCategorySortDTO platformCategorySortDto) {
        List<Long> sortedIds = platformCategorySortDto.getSortedIds();
        List<PlatformCategory> categories = platformCategoryService.lambdaQuery().in(PlatformCategory::getId, sortedIds)
                .eq(PlatformCategory::getParentId, platformCategorySortDto.getParentId())
                .last("ORDER BY FIELD (id," + sortedIds.stream().map(String::valueOf)
                        .collect(Collectors.joining(",")) + ")")
                .list();
        IntStream.range(0, categories.size()).forEach(
                index -> categories.get(index).setSort(index)
        );
        platformCategoryService.updateBatchById(categories);

    }

    /**
     * 获取指定level 等级类目
     *
     * @param categoryParam 查询参数
     * @return IPage<PlatformCategory>
     */
    @Override
    public IPage<PlatformCategory> getCategoryListByLevel(CategoryParam categoryParam) {
        return categoryMapper.selectPage(categoryParam, new QueryWrapper<PlatformCategory>()
                .eq("level", categoryParam.getLevel())
                .eq("parent_id", categoryParam.getParentId()));
    }

    @Override
    public List<Long> getLevelCategoryList(Long platformCategoryId) {
        return TenantShop.disable(() -> categoryMapper.getLevelCategoryList(platformCategoryId));
    }

    @Override
    public List<PlatformCategory> getCategoryInfoByIds(PlatformCategoryRankDTO categoryRank) {

        List<PlatformCategory> categories = platformCategoryService.lambdaQuery()
                .select(PlatformCategory::getId, 
                       PlatformCategory::getName, 
                       PlatformCategory::getParentId, 
                       PlatformCategory::getLevel, 
                       PlatformCategory::getSort, 
                       PlatformCategory::getCategoryImg, 
                       PlatformCategory::getDeductionRatio, 
                       PlatformCategory::getAds)
                .eq(PlatformCategory::getParentId, CommonPool.NUMBER_ZERO)
                .eq(PlatformCategory::getLevel, CategoryLevel.LEVEL_1)
                .last("ORDER BY FIELD (id," + categoryRank.getIds().stream().map(String::valueOf).collect(Collectors.joining(",")) + ")")
                .in(BaseEntity::getId, categoryRank.getIds())
                .list();
        //查询出的一级分类列表
        if (CollUtil.isEmpty(categories)) {
            return categories;
        }
        Map<Long, CategoryStaticVo> categoryIdProductNumCountMap = searchRpcService.categoryCount(
                new CategoryCountParam()
                        .setType(CategoryCountType.PLATFORM)
                        .setNormal(Boolean.TRUE)
                        .setFirstIds(categories.stream().map(PlatformCategory::getId).collect(Collectors.toSet())));
        for (PlatformCategory category : categories) {
            category.setProductNumber(categoryIdProductNumCountMap.getOrDefault(category.getId(), new CategoryStaticVo()).getProductCount());
        }
        List<PlatformCategory> categoriesSecond = categoryQueryFunction.setChildren(categories, CategoryLevel.LEVEL_2);

        //二级分类列表
        if (CollUtil.isEmpty(categoriesSecond)) {
            return categories;
        }
        for (PlatformCategory category : categoriesSecond) {
            category.setProductNumber(categoryIdProductNumCountMap.getOrDefault(category.getId(), new CategoryStaticVo()).getProductCount());
        }
        if (categoryRank.getCategoryLevel() == CategoryLevel.LEVEL_3) {
            List<PlatformCategory> categoriesThird = categoryQueryFunction.setChildren(categoriesSecond,
                    CategoryLevel.LEVEL_3);
            if (CollectionUtil.isNotEmpty(categoriesThird)) {
                for (PlatformCategory category : categoriesThird) {
                    category.setProductNumber(categoryIdProductNumCountMap.getOrDefault(category.getId(), new CategoryStaticVo()).getProductCount());
                }
            }

        }
        return categories;
    }

    @Override
    public IPage<CategoryFirstIdWithNumVO> getCategoryFirstIdWithNumVO(Page<PlatformCategory> page) {
        Page<PlatformCategory> result = platformCategoryService.lambdaQuery()
                .select(PlatformCategory::getId, PlatformCategory::getName)
                .eq(PlatformCategory::getLevel, CategoryLevel.LEVEL_1)
                .page(page);
        List<PlatformCategory> records = result.getRecords();

        if (CollUtil.isEmpty(records)) {
            return null;
        }
        Set<Long> firstIds = records.stream()
                .map(PlatformCategory::getId).collect(Collectors.toSet());

        Map<Long, CategoryStaticVo> categoryIdProductNumCountMap = searchRpcService.categoryCount(
                new CategoryCountParam()
                        .setType(CategoryCountType.PLATFORM)
                        .setNormal(Boolean.TRUE)
                        .setFirstIds(firstIds)
        );
        IPage<CategoryFirstIdWithNumVO> categoryPage = page.convert(
                record -> {
                    CategoryFirstIdWithNumVO categoryFirstIdWithNumVO = new CategoryFirstIdWithNumVO();
                    categoryFirstIdWithNumVO.setPlatformCategoryFirstId(record.getId());
                    categoryFirstIdWithNumVO.setPlatformCategoryFirstName(record.getName());
                    categoryFirstIdWithNumVO.setProductNum(categoryIdProductNumCountMap.getOrDefault(record.getId(),
                            new CategoryStaticVo()).getProductCount().intValue());
                    categoryFirstIdWithNumVO.setSalesCount(categoryIdProductNumCountMap.getOrDefault(record.getId(),
                            new CategoryStaticVo()).getSalesCount());
                    return categoryFirstIdWithNumVO;
                }
        );
        categoryPage.setRecords(
                categoryPage.getRecords()
                        .stream()
                        .filter(record -> record.getProductNum() > 0)
                        .toList()
        );
        return categoryPage;
    }

    @Override
    public Page<ApiProductVO> getProductInfoByPlatformCategoryId(PlatformCategoryRankDTO categoryRank) {
        Set<Long> ids;
        switch (categoryRank.getCategoryLevel()) {
            case LEVEL_1:
                // 如果当前等级为一级类目，获取对应的三级平台类目ID
                ids = getThirdLevelPlatformCategoryIds(categoryRank.getIds());
                break;
            case LEVEL_2:
                // 如果当前等级为二级类目，获取二级以下所有的三级平台类目ID
                List<PlatformCategory> secondLevelCategories = platformCategoryService.lambdaQuery()
                        .in(PlatformCategory::getId, categoryRank.getIds())
                        .list();
                if (CollUtil.isEmpty(secondLevelCategories)) {
                    return null;
                }
                List<Long> secondLevelIds = secondLevelCategories.stream()
                        .map(BaseEntity::getId)
                        .collect(Collectors.toList());
                List<PlatformCategory> thirdLevelCategories = platformCategoryService.lambdaQuery()
                        .in(PlatformCategory::getParentId, secondLevelIds)
                        .list();
                if (CollUtil.isEmpty(thirdLevelCategories)) {
                    return null;
                }
                ids = thirdLevelCategories.stream()
                        .map(BaseEntity::getId)
                        .collect(Collectors.toSet());
                break;
            case LEVEL_3:
                ids = categoryRank.getIds();
                break;
            default:
                // 其他等级不支持，返回null
                return null;
        }

        if (CollUtil.isEmpty(ids)) {
            return null;
        }
        CategoryRankDTO productCategoryRank = new CategoryRankDTO();
        productCategoryRank.setIds(ids);
        productCategoryRank.setSize(categoryRank.getSize());
        productCategoryRank.setCurrent(categoryRank.getCurrent());

        return goodsRpcService.getApiProductInfoByPlatformCategoryId(productCategoryRank);
    }

    private Set<Long> getThirdLevelPlatformCategoryIds(Collection<Long> categoryIds) {
        // 获取一级类目
        List<PlatformCategory> firstLevelCategories = platformCategoryService.lambdaQuery()
                .in(BaseEntity::getId, categoryIds)
                .list();

        if (CollUtil.isEmpty(firstLevelCategories)) {
            return null;
        }
        List<Long> collect = firstLevelCategories.stream()
                .map(BaseEntity::getId)
                .collect(Collectors.toList());
        // 获取二级类目ID
        List<Long> secondLevelIds = platformCategoryService.lambdaQuery()
                .in(PlatformCategory::getParentId, collect)
                .list()
                .stream()
                .map(BaseEntity::getId)
                .collect(Collectors.toList());

        if (CollUtil.isEmpty(secondLevelIds)) {
            return null;
        }

        // 获取三级类目
        List<PlatformCategory> thirdLevelCategories = platformCategoryService.lambdaQuery()
                .in(PlatformCategory::getParentId, secondLevelIds)
                .list();
        if (CollUtil.isEmpty(thirdLevelCategories)) {
            return null;
        }
        return thirdLevelCategories.stream()
                .map(BaseEntity::getId)
                .collect(Collectors.toSet());
    }


    private void isCategorySigned(List<Long> ids) {
        // 判断是否有签约类目
        List<PlatformShopSigningCategory> list = platformShopSigningCategoryService.lambdaQuery()
                .in(PlatformShopSigningCategory::getCurrentCategoryId, ids)
                .list();
        if (CollUtil.isNotEmpty(list)) {
            /*  获取相关shopId
             */
            Set<Long> shopIds = list.stream()
                    .map(PlatformShopSigningCategory::getShopId)
                    .collect(Collectors.toSet());
            log.warn("当前签约类目信息   :".concat(shopIds.toString()));
            List<ShopInfoVO> shopInfoByShopIdList = shopRpcService.getShopInfoByShopIdList(shopIds);
            String shopNames = shopInfoByShopIdList.stream()
                    .map(ShopInfoVO::getName)
                    .collect(Collectors.joining(", "));
            throw new GlobalException("当前类目已有店铺,或供应商签约，请先解除签约类目再删除：" + shopNames);
        }
    }
}
