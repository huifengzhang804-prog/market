package com.medusa.gruul.search.service.controller;

import cn.hutool.core.collection.CollUtil;
import com.medusa.gruul.common.log.annotation.Log;
import com.medusa.gruul.common.model.resp.Result;
import com.medusa.gruul.common.security.model.bean.SecureUser;
import com.medusa.gruul.common.security.resource.helper.ISecurity;
import com.medusa.gruul.common.system.model.ISystem;
import com.medusa.gruul.common.system.model.model.ClientType;
import com.medusa.gruul.search.service.es.entity.EsProductEntity;
import com.medusa.gruul.search.service.model.SearchParam;
import com.medusa.gruul.search.service.model.dto.SuggestDTO;
import com.medusa.gruul.search.service.model.vo.HistoriesAndHotWordsVO;
import com.medusa.gruul.search.service.model.vo.SearchBrandDetailVO;
import com.medusa.gruul.search.service.model.vo.SearchBrandInitialVO;
import com.medusa.gruul.search.service.model.vo.ShopProductSalesTopVO;
import com.medusa.gruul.search.service.service.BrandQueryService;
import com.medusa.gruul.search.service.service.EsProductService;
import com.medusa.gruul.search.service.service.ProductSearchHistoryService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.dromara.easyes.core.biz.EsPageInfo;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * 商品检索控制器
 *
 * @author 张治保
 * date 2022/12/1
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/search/product")
public class ProductSearchController {

    private final EsProductService esProductService;
    private final ProductSearchHistoryService productSearchHistoryService;
    private final BrandQueryService brandQueryService;

    /**
     * 分页查询商品信息
     *
     * @param param 分页查询参数
     */
    @PostMapping
    public Result<EsPageInfo<EsProductEntity>> search(@RequestBody @Valid SearchParam param) {
        //如果是店铺查询 自动设置店铺id
        ISecurity.match()
                .ifAnyShopAdmin((secureUser) -> param.setShopId(secureUser.getShopId()));
        //查询商品
        return Result.ok(esProductService.search(ClientType.CONSUMER == ISystem.clientTypeMust(), param));
    }

    /**
     * 搜索建议
     *
     * @return 搜索建议
     */
    @GetMapping("/suggest")
    public Result<List<EsProductEntity>> suggest(SuggestDTO suggest) {
        return Result.ok(
                esProductService.suggest(suggest)
        );
    }

    /**
     * 查询搜索历史与热词
     *
     * @return 搜索历史与关键词
     */
    @Log("搜索历史与关键词")
    @GetMapping("/hhw")
    public Result<HistoriesAndHotWordsVO> historiesAndHotWords() {
        return Result.ok(
                productSearchHistoryService.historiesAndHotWords(ISecurity.userOpt().map(SecureUser::getId))
        );
    }

    /**
     * 清空查询历史
     */
    @Log("清空查询历史")
    @DeleteMapping("/history")
    public Result<Void> deleteHistory() {
        ISecurity.userOpt()
                .peek(secureUser -> productSearchHistoryService.deleteHistoryByUserId(secureUser.getId()));
        return Result.ok();
    }

    /**
     * 根据品牌id集合查询品牌和首字母
     *
     * @param brandIds 品牌集合
     * @return 品牌名称首字母信息
     */
    @Log("根据品牌id集合查询品牌和首字母")
    @PostMapping("/brandInitialList")
    public Result<Map<String, List<SearchBrandInitialVO>>> getBrandInitialList(@RequestBody List<Long> brandIds) {
        return Result.ok(
                brandQueryService.getBrandInitialList(brandIds)
        );
    }

    /**
     * 查询品牌详情
     *
     * @param brandId 品牌id
     * @return 品牌详情
     */
    @Log("查询品牌详情")
    @GetMapping("/brandDetail/{brandId}")
    public Result<SearchBrandDetailVO> getBrandDetailById(@PathVariable Long brandId) {
        return Result.ok(
                brandQueryService.getBrandDetailById(brandId)
        );
    }

    /**
     * 根据类目id/三级分类id查询品牌
     *
     * @param parentCategoryId 类目id/三级分类id
     * @return 品牌和首字母
     */
    @Log("根据类目id/三级分类id查询品牌")
    @GetMapping("/brandInitial/{parentCategoryId}")
    public Result<Map<String, List<SearchBrandInitialVO>>> getBrandInitial(@PathVariable Long parentCategoryId) {
        return Result.ok(
                brandQueryService.getBrandInitial(parentCategoryId)
        );
    }

    /**
     * 获取商家销量最高的6个商品
     *
     * @param shopIds 商家id集合
     * @return 商家销量最高的6个商品
     */
    @Log("获取商家销量最高的6个商品")
    @PostMapping("/top6")
    public Result<List<ShopProductSalesTopVO>> getShopProductSalesTop(@RequestBody List<Long> shopIds) {
        if (CollUtil.isEmpty(shopIds)){
            return Result.ok();
        }
        return Result.ok(
                esProductService.getShopProductSalesTop(shopIds)
        );
    }

}
