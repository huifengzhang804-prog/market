package com.medusa.gruul.addon.supplier.mp.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.map.MapUtil;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.medusa.gruul.addon.supplier.model.dto.SupplierNewProductCountDTO;
import com.medusa.gruul.addon.supplier.model.param.SupplierProductParam;
import com.medusa.gruul.addon.supplier.model.param.SupplierProductParamNoPage;
import com.medusa.gruul.addon.supplier.model.param.SupplyListParam;
import com.medusa.gruul.addon.supplier.model.vo.SupplierProductListVO;
import com.medusa.gruul.addon.supplier.mp.entity.SupplierGoods;
import com.medusa.gruul.addon.supplier.mp.mapper.SupplierGoodsMapper;
import com.medusa.gruul.addon.supplier.mp.service.ISupplierGoodsService;
import com.medusa.gruul.addon.supplier.utils.SupplierUtil;
import com.medusa.gruul.common.model.base.ShopProductKey;
import com.medusa.gruul.common.model.resp.SystemCode;
import com.medusa.gruul.common.module.app.shop.ShopMode;
import com.medusa.gruul.common.mp.model.BaseEntity;
import com.medusa.gruul.common.mp.model.SqlHelper;
import com.medusa.gruul.common.mp.model.TenantShop;
import com.medusa.gruul.goods.api.entity.Product;
import com.medusa.gruul.goods.api.model.dto.CategorySigningCustomDeductionRationMqDTO;
import com.medusa.gruul.goods.api.model.param.AuditProductParam;
import com.medusa.gruul.goods.api.model.vo.AuditProductVO;
import com.medusa.gruul.storage.api.bo.StSvBo;
import io.vavr.Tuple;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.session.ExecutorType;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 供应商商品数据实现层
 *
 * @author xiaoq
 * @Description SupplierGoodsServiceImpl.java
 * @date 2023-07-17 09:36
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class SupplierGoodsServiceImpl extends ServiceImpl<SupplierGoodsMapper, SupplierGoods> implements ISupplierGoodsService {

    private final RedissonClient redissonClient;
    private final SqlSessionFactory sqlSessionFactory;

    @Override
    public IPage<SupplierProductListVO> getSupplierProductList(SupplierProductParam supplierProductParam) {
        return this.baseMapper.querySupplierProductList(supplierProductParam);
    }

    @Override
    public IPage<SupplierProductListVO> getSupplyListByPlatformCategory(SupplyListParam supplyListParam) {
        return this.baseMapper.querySupplyListByPlatformCategory(supplyListParam);
    }

    @Override
    public IPage<SupplierProductListVO> getProductStockBaseList(SupplierProductParam supplierProductParam) {
        return this.baseMapper.queryProductStockBaseList(supplierProductParam);
    }

    @Override
    public Map<ShopProductKey, Product> getProductBatch(Set<ShopProductKey> shopProductKeys) {
        List<Product> products = this.baseMapper.queryProductBatch(shopProductKeys);
        if (CollUtil.isEmpty(products)) {
            return Collections.emptyMap();
        }
        return products.stream().collect(Collectors.toMap(
                product -> new ShopProductKey().setShopId(product.getShopId()).setProductId(product.getId()),
                product -> product
        ));
    }

    @Override
    public List<SupplierGoods> getSupplierGoods(Set<ShopProductKey> shopProductKeys) {
        return this.baseMapper.getSupplierGoods(shopProductKeys);
    }

    /**
     * 更新商品名称，包含已删除商品
     *
     * @param supplierGoods 商品信息
     * @return 结果
     */
    @Override
    public int updateSupplierGoodById(SupplierGoods supplierGoods) {
        return baseMapper.updateSupplierGoodById(supplierGoods);
    }

    /**
     * 按照{@code dto}统计供应商新增商品数量
     *
     * @param dto {@link SupplierNewProductCountDTO}
     * @return 新增的商品数量
     */
    @Override
    public Integer countNewProduct(SupplierNewProductCountDTO dto) {
        return this.baseMapper.countSupplierNewProduct(dto);
    }

    /**
     * 统计所有供应商新增商品数量
     *
     * @return {@link Integer}
     */
    @Override
    public Integer countAllSupplierNewProduct() {
        return this.baseMapper.countAllSupplierNewProduct();
    }

    /**
     * 统计所有供应商违规商品
     *
     * @return {@link Integer}
     */
    @Override
    public Integer countIrregularityProduct() {
        return this.baseMapper.countIrregularityProduct();
    }

    @Override
    public IPage<AuditProductVO> getSupplierAuditProduct(AuditProductParam auditProductParam) {
        return this.baseMapper.querySupplierAuditProduct(auditProductParam);
    }

    @Override
    public Integer illegalCount(SupplierProductParamNoPage supplierProductParam) {
        return getBaseMapper().illegalCount(supplierProductParam);
    }

    @Override
    public void updateProductStock(Map<ShopProductKey, StSvBo> mergeResult) {
        log.debug("批量更新 sku 库存：{}", mergeResult);
        if (MapUtil.isEmpty(mergeResult)) {
            return;
        }
        //批量锁
        RLock multiLock = redissonClient.getMultiLock(
                mergeResult.keySet().stream()
                        .map(SupplierUtil::generateRedissonLockKey)
                        .map(redissonClient::getLock)
                        .toArray(RLock[]::new)
        );
        //加锁
        multiLock.lock();
        try {
            //批量执行器
            SqlSession sqlSession = sqlSessionFactory.openSession(ExecutorType.BATCH);
            //批量更新数据库库存数据 并归还库存和销量
            try {
                String updateStockSql = "total_stock = total_stock + {0}";
                SupplierGoodsMapper mapper = sqlSession.getMapper(SupplierGoodsMapper.class);
                for (Map.Entry<ShopProductKey, StSvBo> entry : mergeResult.entrySet()) {
                    ShopProductKey key = entry.getKey();
                    StSvBo value = entry.getValue();
                    long stock = value.getStock();
                    if (stock == 0) {
                        continue;
                    }
                    TenantShop.disable(
                            () -> mapper.update(
                                    Wrappers.lambdaUpdate(SupplierGoods.class)
                                            .eq(SupplierGoods::getId, key.getProductId())
                                            .eq(SupplierGoods::getShopId, key.getShopId())
                                            .setSql(updateStockSql, stock)
                            )
                    );
                }
                sqlSession.commit();
            } catch (Exception exception) {
                log.error("商品总库存更新出错", exception);
                sqlSession.rollback();
                throw SystemCode.DATA_UPDATE_FAILED.exception();
            } finally {
                sqlSession.close();
            }
        } finally {
            //释放锁
            multiLock.unlock();
        }
    }

    @Override
    public void updateProductCategoryDeductionRation(CategorySigningCustomDeductionRationMqDTO dto) {
        if (!ShopMode.SUPPLIER.equals(dto.getShopMode())) {
            //如果更新类目的是店铺 则供应商不处理 退出
            return;
        }

        log.info("更新供应商商品类目扣率:{}", dto);

        Long shopId = dto.getShopId();
        Set<Long> productIds = TenantShop.disable(
                        () -> this.query()
                                .select("id")
                                .eq("shop_id", shopId)
                                .eq("product->'$.extra.platformCategory.two'", dto.getSecondCategoryId())
                                .list()
                ).stream()
                .map(BaseEntity::getId)
                .collect(Collectors.toSet());
        if (CollUtil.isEmpty(productIds)) {
            return;
        }

        TenantShop.disable(() ->
                this.lambdaUpdate()
                        .setSql(
                                SqlHelper.renderJsonSetSql(
                                        "product",

                                        Tuple.of("extra.customDeductionRatio", dto.getDeductionRation())
                                )
                        )
                        .eq(SupplierGoods::getShopId, shopId)
                        .in(BaseEntity::getId, productIds)
                        .update());


    }

}
