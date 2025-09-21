package com.medusa.gruul.addon.distribute.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.medusa.gruul.addon.distribute.model.dto.ProductBindDTO;
import com.medusa.gruul.addon.distribute.model.dto.ProductQueryDTO;
import com.medusa.gruul.addon.distribute.model.enums.DistributionStatus;
import com.medusa.gruul.addon.distribute.mp.entity.DistributeProduct;
import com.medusa.gruul.addon.distribute.service.DistributeProductHandleService;
import com.medusa.gruul.common.idem.annotation.Idem;
import com.medusa.gruul.common.log.annotation.Log;
import com.medusa.gruul.common.model.base.ShopProductKey;
import com.medusa.gruul.common.model.resp.Result;
import com.medusa.gruul.common.security.model.bean.SecureUser;
import com.medusa.gruul.common.security.resource.helper.ISecurity;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 分销商品控制器
 *
 * @author 张治保 date 2022/11/14
 */
@RestController
@RequiredArgsConstructor
@Validated
@PreAuthorize("@S.shopPerm('distribution:Index')")
@RequestMapping("/distribute/product")
public class DistributeProductController {

    private final DistributeProductHandleService distributeProductHandleService;

    /**
     * 新增分销商品绑定
     *
     * @param productBind 绑定信息
     * @return void
     */
    @Log("商品绑定分销")
    @PostMapping
    public Result<Void> newProduct(@RequestBody @Valid ProductBindDTO productBind) {
        distributeProductHandleService.newProduct(ISecurity.userMust().getShopId(), productBind);
        return Result.ok();
    }

    /**
     * 修改分销商品绑定关系
     *
     * @param productBind 绑定信息
     * @return void
     */
    @Log("修改分销商品绑定关系")
    @PutMapping("/{bindId}")
    public Result<Void> editProduct(@PathVariable Long bindId, @RequestBody @Valid ProductBindDTO productBind) {
        distributeProductHandleService.editProduct(ISecurity.userMust().getShopId(), bindId, productBind);
        return Result.ok();
    }

    /**
     * 重新分销
     *
     * @param bindId 分销商品绑定关系id
     */
    @Idem(500)
    @Log("重新分销")
    @PutMapping("/again/{bindId}")
    public Result<Void> againDistribute(@PathVariable Long bindId) {
        distributeProductHandleService.againDistribute(ISecurity.userMust().getShopId(), bindId);
        return Result.ok();
    }

    /**
     * 批量取消分销商品
     *
     * @param bindIds 分销商品绑定关系id集合
     * @return void
     */
    @Log("批量取消分销商品")
    @PutMapping("/cancel/{bindIds}")
    @PreAuthorize("@S.anyPerm('distribution:index','distribution:Index')")
    public Result<Void> cancelBatch(@PathVariable @NotNull @Size(min = 1) Set<Long> bindIds) {
        distributeProductHandleService.cancelBatch(ISecurity.userMust().getShopId(), bindIds);
        return Result.ok();
    }

    /**
     * 批量删除分销商品
     *
     * @param bindIds 分销商品绑定关系id集合
     * @return void
     */
    @Log("批量删除分销商品")
    @DeleteMapping("/{bindIds}")
    public Result<Void> deleteBatch(@PathVariable @NotNull @Size(min = 1) Set<Long> bindIds) {
        distributeProductHandleService.deleteBatch(ISecurity.userMust().getShopId(), bindIds);
        return Result.ok();
    }

    /**
     * 分页查询分销商品
     *
     * @param query 分销商品查询条件
     * @return 分销商品分页数据
     */
    @Log("分页查询分销商品")
    @PostMapping("/page")
    @PreAuthorize("permitAll()")
    public Result<IPage<DistributeProduct>> productPage(@RequestBody ProductQueryDTO query) {
        ISecurity.match()
                .ifUser(secureUser -> query.setDistributionStatus(DistributionStatus.IN_DISTRIBUTION))
                .ifAnyShopAdmin(secureUser -> query.setShopId(secureUser.getShopId()));
        return Result.ok(
                distributeProductHandleService.productPage(query)
        );
    }

    /**
     * 查询商品预计佣金
     *
     * @param key 商品key {@link ShopProductKey}
     * @return 商品预计佣金
     */
    @Log("查询商品预计佣金")
    @GetMapping("/{shopId}/{productId}/bonus")
    @PreAuthorize("permitAll()")
    public Result<Long> productBonus(@Valid ShopProductKey key) {
        return Result.ok(
                distributeProductHandleService.productPrecompute(
                        key,
                        ISecurity.userOpt().map(SecureUser::getId).getOrNull()
                )
        );
    }

}
