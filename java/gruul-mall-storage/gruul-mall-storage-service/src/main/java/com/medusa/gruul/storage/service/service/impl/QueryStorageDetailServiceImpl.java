package com.medusa.gruul.storage.service.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.medusa.gruul.carrier.pigeon.api.rpc.PigeonChatStatisticsRpcService;
import com.medusa.gruul.common.model.base.ShopProductKey;
import com.medusa.gruul.common.model.constant.CommonPool;
import com.medusa.gruul.common.model.enums.OrderType;
import com.medusa.gruul.common.mp.model.BaseEntity;
import com.medusa.gruul.common.mp.model.TenantShop;
import com.medusa.gruul.common.security.resource.helper.ISecurity;
import com.medusa.gruul.common.system.model.ISystem;
import com.medusa.gruul.common.system.model.model.ClientType;
import com.medusa.gruul.goods.api.entity.Product;
import com.medusa.gruul.goods.api.rpc.GoodsRpcService;
import com.medusa.gruul.overview.api.enums.ExportDataType;
import com.medusa.gruul.overview.api.model.DataExportRecordDTO;
import com.medusa.gruul.overview.api.rpc.DataExportRecordRpcService;
import com.medusa.gruul.storage.api.bo.SupplierOrderBO;
import com.medusa.gruul.storage.api.entity.StorageSku;
import com.medusa.gruul.storage.service.addon.StorageAddonSupporter;
import com.medusa.gruul.storage.service.model.constant.StorageConstant;
import com.medusa.gruul.storage.service.model.param.StorageDetailParam;
import com.medusa.gruul.storage.service.mp.entity.StorageDetail;
import com.medusa.gruul.storage.service.mp.service.IStorageDetailService;
import com.medusa.gruul.storage.service.mp.service.IStorageSkuService;
import com.medusa.gruul.storage.service.service.QueryStorageDetailService;
import com.medusa.gruul.storage.service.service.task.AbstractStorageDetailExportTask;
import com.medusa.gruul.storage.service.service.task.ShopStorageDetailExportTask;
import com.medusa.gruul.storage.service.service.task.SupplierStorageDetailExportTask;
import com.medusa.gruul.user.api.rpc.UserRpcService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Executor;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

/**
 * QueryStorageDetailServiceImpl.java
 *
 * @author xiaoq
 * @Description QueryStorageDetailServiceImpl.java
 * @date 2023-07-27 14:42
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class QueryStorageDetailServiceImpl implements QueryStorageDetailService {
    private final IStorageDetailService storageDetailService;
    private final StorageAddonSupporter storageAddonSupporter;
    private final GoodsRpcService goodsRpcService;
    private final DataExportRecordRpcService dataExportRecordRpcService;
    private final Executor globalExecutor;
    private final PigeonChatStatisticsRpcService pigeonChatStatisticsRpcService;
    private final IStorageSkuService storageSkuService;
    private UserRpcService userRpcService;


    @Override
    public IPage<StorageDetail> getStorageDetailList(StorageDetailParam storageDetailParam) {
        //查询采购订单号，转订单号为主单号
        AtomicReference<List<SupplierOrderBO>> orderList = new AtomicReference<>();
        if (StrUtil.isNotEmpty(storageDetailParam.getOrderNo())
                && storageDetailParam.getOrderNo().contains(StorageConstant.SUPPLIER_NO_PREFIX)) {
            orderList.set(TenantShop.disable(() -> storageAddonSupporter.getOrderByMainNo(storageDetailParam.getOrderNo(), null)));
            if (CollUtil.isNotEmpty(orderList.get())) {
                storageDetailParam.setOrderNo(orderList.get().get(CommonPool.NUMBER_ZERO).getNo());
            }
        }
        IPage<StorageDetail> storageDetailIPage = storageDetailService.lambdaQuery()
                .eq(StorageDetail::getIsShow, Boolean.TRUE)
                .eq(StorageDetail::getShopId, storageDetailParam.getShopId())
                .eq(storageDetailParam.getProductId() != null,
                        StorageDetail::getProductId, storageDetailParam.getProductId())
                .eq(storageDetailParam.getId() != null,
                        BaseEntity::getId, storageDetailParam.getId())
                .eq(storageDetailParam.getSellType() != null,
                        StorageDetail::getSellType, storageDetailParam.getSellType())
                .eq(storageDetailParam.getStockChangeType() != null,
                        StorageDetail::getStockChangeType, storageDetailParam.getStockChangeType())
                .eq(StrUtil.isNotBlank(storageDetailParam.getOrderNo()),
                        StorageDetail::getOrderNo, storageDetailParam.getOrderNo())
                .and(
                        storageDetailParam.getIsOutbound() != null,
                        (Boolean.TRUE.equals(storageDetailParam.getIsOutbound()) ?
                                // Compare with StockChangeNum if getIsOutbound() is true
                                queryWrapper -> queryWrapper.lt(StorageDetail::getStockChangeNum, 0) :
                                // Compare with StockChangeNum if getIsOutbound() is false
                                queryWrapper -> queryWrapper.gt(StorageDetail::getStockChangeNum, 0)
                        )
                )
                .like(StrUtil.isNotBlank(storageDetailParam.getProductName()),
                        StorageDetail::getProductName, storageDetailParam.getProductName())
                .between(storageDetailParam.getStartTime() != null && storageDetailParam.getEndTime() != null,
                        BaseEntity::getCreateTime, storageDetailParam.getStartTime(), storageDetailParam.getEndTime())
                .in(CollectionUtil.isNotEmpty(storageDetailParam.getExportIds()), StorageDetail::getId, storageDetailParam.getExportIds())
                .orderByDesc(BaseEntity::getCreateTime)
                .orderByDesc(BaseEntity::getId)
                .page(new Page<>(storageDetailParam.getCurrent(), storageDetailParam.getSize()));
        Set<Long> skuIds = storageDetailIPage.getRecords().stream().map(StorageDetail::getSkuId).collect(Collectors.toSet());
        if (CollectionUtil.isNotEmpty(skuIds)) {
            Map<Long, StorageSku> collect = storageSkuService.lambdaQuery()
                    .select(StorageSku::getId, StorageSku::getStockType)
                    .in(StorageSku::getId, skuIds)
                    .eq(StorageSku::getActivityType, OrderType.COMMON)
                    .eq(StorageSku::getActivityId, CommonPool.NUMBER_ZERO)
                    .list()
                    .stream().collect(Collectors.toMap(StorageSku::getId, x -> x, (x, y) -> x));
            storageDetailIPage.getRecords().forEach(storageDetail -> {
                StorageSku storageSku = collect.get(storageDetail.getSkuId());
                if (storageSku != null) {
                    storageDetail.setSkuStockType(storageSku.getStockType());
                }
            });
        }
        if (StrUtil.isNotEmpty(storageDetailParam.getOrderNo())
                && storageDetailParam.getOrderNo().contains(StorageConstant.SUPPLIER_NO_PREFIX)) {
            storageDetailIPage.getRecords().forEach(storageDetail -> {
                if (CollUtil.isNotEmpty(orderList.get())) {
                    storageDetail.setOrderNo(orderList.get().get(CommonPool.NUMBER_ZERO).getNo());
                }
            });
            return storageDetailIPage;
        }
        //采购主订单
        Set<String> orderNos = storageDetailIPage.getRecords().stream()
                .filter(storageDetail -> StrUtil.isNotEmpty(storageDetail.getOrderNo())
                        && storageDetail.getOrderNo().startsWith(StorageConstant.SUPPLIER_MAIN_NO_PREFIX))
                .map(StorageDetail::getOrderNo).collect(Collectors.toSet());
        if (CollUtil.isNotEmpty(orderNos)) {
            //根据主单号，查询采购主订单SUP单号
            List<SupplierOrderBO> orders = TenantShop.disable(() -> storageAddonSupporter.getOrderByMainNo(null, orderNos));
            Map<String, List<SupplierOrderBO>> collect = orders.stream().collect(
                    Collectors.groupingBy(SupplierOrderBO::getMainNo, Collectors.toList()));
            storageDetailIPage.getRecords().forEach(storageDetail -> {
                List<SupplierOrderBO> supplierOrders = collect.get(storageDetail.getOrderNo());
                if (CollUtil.isNotEmpty(supplierOrders)) {
                    storageDetail.setOrderNo(supplierOrders.get(CommonPool.NUMBER_ZERO).getNo());
                }
            });
        }

        Set<ShopProductKey> shopProductKeys = storageDetailIPage.getRecords().stream()
                .map(x -> {
                    ShopProductKey key = new ShopProductKey();
                    key.setShopId(x.getShopId());
                    key.setProductId(x.getProductId());
                    return key;
                }).collect(Collectors.toSet());
        Map<ShopProductKey, Product> productListBatch = null;
        if (ISystem.clientTypeMust().equals(ClientType.SHOP_CONSOLE)) {
            productListBatch = goodsRpcService.getProductBatch(shopProductKeys);
        } else {
            productListBatch = storageAddonSupporter.getSupplierProductBatch(shopProductKeys);
        }


        for (StorageDetail record : storageDetailIPage.getRecords()) {
            Product product = productListBatch.get(new ShopProductKey(record.getShopId(), record.getProductId()));
            record.setProduct(product);
        }

        return storageDetailIPage;

    }

    @Override
    public void export(StorageDetailParam storageDetailParam) {
        ExportDataType exportDataType = ExportDataType.STORAGE_DETAIL;
        DataExportRecordDTO dto = new DataExportRecordDTO();
        dto.setExportUserId(ISecurity.userMust().getId()).setDataType(exportDataType)
                .setShopId(ISystem.shopIdMust())
                .setUserPhone(ISecurity.userMust().getMobile());
        //RPC保存导出记录
        Long exportRecordId = dataExportRecordRpcService.saveExportRecord(dto);
        AbstractStorageDetailExportTask task;
        if (ClientType.SHOP_CONSOLE.equals(ISystem.clientTypeMust())) {
            task = new ShopStorageDetailExportTask(pigeonChatStatisticsRpcService,
                    dataExportRecordRpcService, userRpcService,
                    this, storageDetailParam, exportRecordId);
        } else {
            task = new SupplierStorageDetailExportTask(pigeonChatStatisticsRpcService,
                    dataExportRecordRpcService, userRpcService,
                    this, storageDetailParam, exportRecordId);
        }
        globalExecutor.execute(task);
    }
}
