package com.medusa.gruul.addon.integral.service.impl;

import cn.hutool.core.text.StrPool;
import cn.hutool.core.util.StrUtil;
import com.medusa.gruul.addon.integral.model.dto.IntegralProductDTO;
import com.medusa.gruul.addon.integral.model.dto.IntegralProductFixDTO;
import com.medusa.gruul.addon.integral.model.enums.IntegralProductStatus;
import com.medusa.gruul.addon.integral.mp.entity.IntegralProduct;
import com.medusa.gruul.addon.integral.mp.service.IIntegralProductService;
import com.medusa.gruul.addon.integral.service.ManageIntegralProductService;
import com.medusa.gruul.addon.integral.util.IntegralUtil;
import com.medusa.gruul.common.model.constant.CommonPool;
import com.medusa.gruul.common.model.resp.SystemCode;
import com.medusa.gruul.common.mp.model.BaseEntity;
import com.medusa.gruul.common.redis.util.RedisUtil;
import com.medusa.gruul.global.model.exception.GlobalException;
import com.medusa.gruul.goods.api.model.enums.ProductType;
import io.vavr.control.Option;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 管理端积分商品数据实现层
 *
 * @author xiaoq
 * @Description 管理端积分商品数据实现层
 * @date 2023-02-01 09:27
 */
@Service
@RequiredArgsConstructor
public class ManageIntegralProductServiceImpl implements ManageIntegralProductService {
    private final IIntegralProductService integralProductService;

    /**
     * 积分商品发布
     *
     * @param integralProduct 积分商品dto
     */
    @Override
    public void issueIntegralProduct(IntegralProductDTO integralProduct) {
        IntegralProduct integralProductInfo = integralProduct.coverIntegralProduct();
        integralProductInfo.setStatus(IntegralProductStatus.SELL_ON);
        integralProductInfo.setPic(StrUtil.split(integralProduct.getAlbumPics(), StrPool.COMMA).get(CommonPool.NUMBER_ZERO));
        SystemCode.DATA_ADD_FAILED.falseThrow(
                integralProductService.save(integralProductInfo)
        );
    }

    /**
     * 积分商品批量删除 (延时双删)
     *
     * @param ids 积分商品ids
     */
    @Override
    public void delIntegralProduct(Set<Long> ids) {
        // 获取删除redis得 keys
        Set<String> collect = ids.stream().map(IntegralUtil::integralProductCacheKey).collect(Collectors.toSet());
        List<IntegralProduct> products = integralProductService.lambdaQuery()
                .select(IntegralProduct::getId)
                .eq(IntegralProduct::getStatus, IntegralProductStatus.SELL_OFF)
                .in(IntegralProduct::getId, ids)
                .list();
        if (products.size() != ids.size()) {
            throw new GlobalException(SystemCode.PARAM_VALID_ERROR_CODE, "只能删除下架的商品");
        }
        SystemCode.DATA_DELETE_FAILED.falseThrow(
                RedisUtil.doubleDeletion(
                        () -> integralProductService.removeByIds(ids),
                        () -> RedisUtil.delete(collect)
                )
        );
    }

    /**
     * 积分商品修改
     *
     * @param integralProduct 积分商品dto
     */
    @Override
    public void updateIntegralProduct(IntegralProductDTO integralProduct) {
        IntegralProduct oldIntegralProduct = Option.of(
                integralProductService.lambdaQuery()
                        .select(
                                IntegralProduct::getId,
                                IntegralProduct::getStatus,
                                IntegralProduct::getFreightPrice,
                                IntegralProduct::getProductType
                        )
                        .eq(BaseEntity::getId, integralProduct.getId())
                        .one()
        ).getOrElseThrow(
                () -> new GlobalException(SystemCode.DATA_NOT_EXIST_CODE, "积分商品不存在")
        );
        IntegralProduct newIntegralProduct = integralProduct.coverIntegralProduct();
        newIntegralProduct.setId(integralProduct.getId());
        newIntegralProduct.setPic(StrUtil.split(newIntegralProduct.getAlbumPics(), StrPool.COMMA).get(CommonPool.NUMBER_ZERO));
        // 不可变动项
        newIntegralProduct.setProductType(oldIntegralProduct.getProductType());
        if (oldIntegralProduct.getProductType() == ProductType.VIRTUAL_PRODUCT) {
            newIntegralProduct.setFreightPrice(oldIntegralProduct.getFreightPrice());
        }
        //预防下架积分商品直接上架
        newIntegralProduct.setStatus(oldIntegralProduct.getStatus());
        SystemCode.DATA_UPDATE_FAILED.falseThrow(
                RedisUtil.doubleDeletion(
                        () -> integralProductService.updateById(newIntegralProduct),
                        () -> RedisUtil.delete(IntegralUtil.integralProductCacheKey(integralProduct.getId()))
                )
        );
    }

    /**
     * 积分商品信息增量修改
     *
     * @param integralProductFix 修改内容
     */
    @Override
    public void updateProductIncrement(IntegralProductFixDTO integralProductFix) {
        IntegralProduct integralProduct = Option.of(
                integralProductService.lambdaQuery()
                        .select(IntegralProduct::getId, IntegralProduct::getStock, IntegralProduct::getSalePrice)
                        .eq(IntegralProduct::getId, integralProductFix.getId())
                        .one()
        ).getOrElseThrow(() -> new GlobalException(SystemCode.DATA_NOT_EXIST_CODE, "被修改的积分商品不存在"));
        validProductIncrement(integralProduct, integralProductFix);
        RedisUtil.doubleDeletion(
                () -> integralProductService.updateProductIncrement(integralProductFix),
                () -> RedisUtil.delete(IntegralUtil.integralProductCacheKey(integralProduct.getId()))
        );
    }

    /**
     * 积分商品批量上下架
     *
     * @param ids    积分商品ids
     * @param status 积分商品修改状态
     */
    @Override
    public void updateIntegralProductStatus(Set<Long> ids, IntegralProductStatus status) {
        // 获取删除redis得 keys
        Set<String> collect = ids.stream().map(IntegralUtil::integralProductCacheKey).collect(Collectors.toSet());
        Boolean flag = RedisUtil.doubleDeletion(
                () -> integralProductService.lambdaUpdate()
                        .in(BaseEntity::getId, ids)
                        .set(IntegralProduct::getStatus, status)
                        .update(),
                () -> RedisUtil.delete(collect)
        );
        if (!flag) {
            throw new GlobalException(SystemCode.PARAM_VALID_ERROR_CODE, "商品批量上下架失败");
        }
    }

    /**
     * 积分商品增量修改参数校验
     *
     * @param integralProduct    积分商品信息
     * @param integralProductFix 积分商品需要修改的信息
     */
    private void validProductIncrement(IntegralProduct integralProduct, IntegralProductFixDTO integralProductFix) {
        if (integralProductFix.getStock() != null) {
            int stock = integralProduct.getStock() + integralProductFix.getStock();
            SystemCode.PARAM_VALID_ERROR.falseThrow(stock >= 0);
        }
        if (integralProduct.getSalePrice() != null) {
            SystemCode.PARAM_VALID_ERROR.falseThrow(integralProductFix.getSalePrice() != null && integralProductFix.getSalePrice() > 0);
        } else {
            SystemCode.PARAM_VALID_ERROR.falseThrow(integralProductFix.getSalePrice() != null);
        }
        if (integralProductFix.getIntegralPrice() == null && StrUtil.isBlank(integralProductFix.getName()) && integralProductFix.getSalePrice() == null) {
            throw new GlobalException(SystemCode.PARAM_VALID_ERROR_CODE, "修改信息不能为空");
        }

    }
}
