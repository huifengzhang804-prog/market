package com.medusa.gruul.search.service.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import com.medusa.gruul.common.security.model.bean.SecureUser;
import com.medusa.gruul.common.security.resource.helper.ISecurity;
import com.medusa.gruul.global.config.GlobalAppProperties;
import com.medusa.gruul.global.model.enums.Mode;
import com.medusa.gruul.global.model.exception.GlobalException;
import com.medusa.gruul.search.api.enums.BrandStatus;
import com.medusa.gruul.search.service.model.vo.SearchBrandDetailVO;
import com.medusa.gruul.search.service.model.vo.SearchBrandInitialVO;
import com.medusa.gruul.search.service.mp.entity.SearchBrand;
import com.medusa.gruul.search.service.mp.entity.SearchBrandFollow;
import com.medusa.gruul.search.service.mp.service.IPlatformCategoryService;
import com.medusa.gruul.search.service.mp.service.IProductCategoryService;
import com.medusa.gruul.search.service.mp.service.ISearchBrandFollowService;
import com.medusa.gruul.search.service.mp.service.ISearchBrandService;
import com.medusa.gruul.search.service.service.BrandQueryService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.*;


@Service
@RequiredArgsConstructor
public class BrandQueryServiceImpl implements BrandQueryService {

    private final ISearchBrandService brandService;
    private final ISearchBrandFollowService brandFollowService;
    private final GlobalAppProperties globalAppProperties;
    private final IPlatformCategoryService platformCategoryService;
    private final IProductCategoryService productCategoryService;


    /**
     * 查询品牌详情
     *
     * @param brandId 品牌id
     * @return 品牌详情
     */
    @Override
    public SearchBrandDetailVO getBrandDetailById(Long brandId) {
        SecureUser secureUser = ISecurity.userOpt().getOrNull();
        SearchBrand brand = brandService.lambdaQuery()
                .eq(SearchBrand::getId, brandId)
                .one();
        if (brand.getStatus() == BrandStatus.SELL_OFF) {
            throw new GlobalException("品牌已下架");
        }
        // 关注人数
        Long count = brandFollowService.lambdaQuery()
                .eq(SearchBrandFollow::getBrandId, brandId)
                .count();
        boolean isFollow = false;
        // 关注状态
        if (secureUser != null) {
            isFollow = brandFollowService.lambdaQuery()
                    .eq(SearchBrandFollow::getUserId, secureUser.getId())
                    .eq(SearchBrandFollow::getBrandId,brandId)
                    .exists();
        }
        SearchBrandDetailVO searchBrandDetailVO = new SearchBrandDetailVO();
        searchBrandDetailVO.setId(brand.getId().toString())
                .setBrandName(brand.getBrandName())
                .setBrandLogo(brand.getBrandLogo())
                .setBrandDesc(brand.getBrandDesc())
                .setFollowers(count)
                .setIsFollow(isFollow);
        List<SearchBrandDetailVO.CategoryVO> categoryVos = null;
        Long parentCategoryId = brand.getParentCategoryId();
        if (Mode.B2B2C == globalAppProperties.getMode()) {
            categoryVos = platformCategoryService.getPlatformCategory(parentCategoryId);
        }
        if (Mode.B2C == globalAppProperties.getMode()) {
            categoryVos = productCategoryService.getProductCategory(parentCategoryId);
        }
        searchBrandDetailVO.setCategoryVos(categoryVos);
        return searchBrandDetailVO;
    }

    /**
     * 根据品牌id查询品牌和首字母
     *
     * @param brandIdList 品牌集合
     * @return 品牌名称首字母信息
     */
    @Override
    public Map<String, List<SearchBrandInitialVO>> getBrandInitialList(List<Long> brandIdList) {
        List<SearchBrandInitialVO> brandInitialList = brandService.getBrandInitialList(brandIdList, null);
        if (CollectionUtils.isEmpty(brandInitialList)) {
            return Collections.emptyMap();
        }
        return getBrandInitialMap(brandInitialList, brandIdList, null);
    }


    /**
     * 根据类目id/三级分类id查询品牌
     *
     * @param parentCategoryId 类目id/三级分类id
     * @return 品牌和首字母
     */
    @Override
    public Map<String, List<SearchBrandInitialVO>> getBrandInitial(Long parentCategoryId) {
        List<SearchBrand> searchBrands = brandService.lambdaQuery()
                .select(SearchBrand::getId, SearchBrand::getBrandName)
                .eq(SearchBrand::getParentCategoryId, parentCategoryId)
                .eq(SearchBrand::getStatus, BrandStatus.SELL_ON)
                .orderByDesc(SearchBrand::getSort)
                .list();
        if (CollUtil.isEmpty(searchBrands)) {
            return Collections.emptyMap();
        }
        List<SearchBrandInitialVO> brandInitial = brandService.getBrandInitialList(null, parentCategoryId);
        return getBrandInitialMap(brandInitial, null, parentCategoryId);

    }

    private Map<String, List<SearchBrandInitialVO>> getBrandInitialMap(List<SearchBrandInitialVO> brandInitialList, List<Long> brandIdList, Long parentCategoryId) {
        Map<String, List<SearchBrandInitialVO>> map = new HashMap<>();
        brandInitialList.forEach(brandInitial -> {
            String brandName = brandInitial.getBrandName();
            String brandIds = brandInitial.getBrandIds();
            List<SearchBrandInitialVO> searchBrandInitials = map.computeIfAbsent(brandInitial.getSearchInitials(), (key) -> new ArrayList<>());
            if (brandName.contains(StrUtil.COMMA)) {
                List<String> brandNames = StrUtil.split(brandName, StrUtil.COMMA);
                List<String> splitBrandIds = StrUtil.split(brandIds, StrUtil.COMMA);
                for (int i = 0; i < brandNames.size(); i++) {
                    searchBrandInitials.add(new SearchBrandInitialVO()
                            .setBrandId(splitBrandIds.get(i))
                            .setBrandName(brandNames.get(i)));
                }
            } else {
                searchBrandInitials.add(new SearchBrandInitialVO()
                        .setBrandName(brandName)
                        .setBrandId(brandIds));
            }
        });
        List<SearchBrandInitialVO> allBrands = brandService.lambdaQuery()
                .select(SearchBrand::getId, SearchBrand::getBrandName)
                .in(CollUtil.isNotEmpty(brandIdList), SearchBrand::getId, brandIdList)
                .eq(parentCategoryId != null, SearchBrand::getParentCategoryId, parentCategoryId)
                .eq(SearchBrand::getStatus, BrandStatus.SELL_ON)
                .orderByDesc(SearchBrand::getSort)
                .list().stream()
                .map(searchBrand -> new SearchBrandInitialVO().setBrandId(searchBrand.getId().toString())
                        .setBrandName(searchBrand.getBrandName()))
                .toList();
        map.put("allBrands", allBrands);
        return map;
    }
}
