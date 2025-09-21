package com.medusa.gruul.addon.supplier.modules.order.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.medusa.gruul.addon.supplier.model.dto.*;
import com.medusa.gruul.addon.supplier.model.enums.OrderStatus;
import com.medusa.gruul.addon.supplier.model.vo.OrderStorageVO;
import com.medusa.gruul.addon.supplier.model.vo.PublishProductGetVO;
import com.medusa.gruul.addon.supplier.model.vo.PublishProductVO;
import com.medusa.gruul.addon.supplier.mp.entity.SupplierOrder;
import com.medusa.gruul.addon.supplier.mp.entity.SupplierOrderPackage;

import java.math.BigDecimal;
import java.util.List;
import java.util.Set;

/**
 * @author 张治保
 * date 2023/7/24
 */
public interface SupplierOrderHandleService {

    /**
     * 分页查询供应商订单
     *
     * @param query 查询条件
     * @return 分页查询结果
     */
    IPage<SupplierOrder> orderPage(OrderQueryPageDTO query);

    /**
     * 查询订单详情
     *
     * @param query 查询条件
     * @return 订单详情信息
     */
    SupplierOrder order(OrderDetailQueryDTO query);


    /**
     * 更新订单状态 根据当前状态更新为下一个状态
     *
     * @param isSystem      是否是系统操作
     * @param orderNos      订单号集合
     * @param currentStatus 当前状态
     * @param nextStatus    更新后的状态
     */
    void updateOrderStatus(boolean isSystem, Set<String> orderNos, OrderStatus currentStatus, OrderStatus nextStatus);

    /**
     * 订单支付
     *
     * @param shopId   店铺id
     * @param orderPay 订单支付信息
     */
    void orderPay(Long shopId, OrderPayDTO orderPay);

    /**
     * 订单支付审核
     *
     * @param supplierId 供应商id
     * @param audit      审核结果
     */
    void orderPayAudit(Long supplierId, OrderPayAuditDTO audit);

    /**
     * 关闭订单
     *
     * @param orderMatch  订单匹配条件
     * @param closeStatus 订单关闭目标状态
     */
    void closeOrder(OrderMatchQueryDTO orderMatch, OrderStatus closeStatus);

    /**
     * 订单发货
     *
     * @param supplierId 供应商id
     * @param delivery   发货信息
     */
    void orderDelivery(Long supplierId, OrderDeliveryDTO delivery);

    /**
     * 订单批量发货
     *
     * @param supplierId 供应商 id
     * @param deliveries 发货信息
     */
    void orderDeliveryBatch(Long supplierId, Set<OrderDeliveryDTO> deliveries);

    /**
     * 订单拆分发货 并获取商品总重量
     *
     * @param orderNo   订单号
     * @param packageId 包裹id
     * @param items     订单item发货信息
     * @return 商品总重量
     */
    BigDecimal splitDeliveryItems(String orderNo, Long packageId, Set<OrderDeliveryItemDTO> items);


    /**
     * 查询订单已发货包裹列表
     *
     * @param deliveryQuery 查询条件
     * @return 包裹列表
     */
    List<SupplierOrderPackage> deliveryPackages(OrderMatchQueryDTO deliveryQuery);


    /**
     * 查询商品入库详情
     *
     * @param query 查询条件
     * @return 入库详情
     */
    OrderStorageVO storageQuery(OrderMatchQueryDTO query);

    /**
     * 订单入库
     *
     * @param storage 入库数据详情
     */
    void storage(OrderStorageDTO storage);

    /**
     * 订单完成
     *
     * @param orderMatch 订单匹配条件
     */
    void orderComplete(OrderMatchQueryDTO orderMatch);


    /**
     * 订单批量备注
     *
     * @param remark 备注信息
     */
    void orderRemarkBatch(OrderRemarkBatchDTO remark);

    /**
     * 待发布商品分页查询
     *
     * @param query 查询条件
     * @return 分页查询结果
     */
    IPage<PublishProductVO> publishPage(PublishProductQueryDTO query);

    /**
     * 查询待发布商品信息
     *
     * @param publishProductGet 查询条件
     * @return 待发布商品信息
     */
    PublishProductGetVO publishProductGet(PublishProductGetDTO publishProductGet);
}
