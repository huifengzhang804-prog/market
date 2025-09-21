package com.medusa.gruul.order.service.modules.deliver.service.impl;

import cn.binarywang.wx.miniapp.api.WxMaOrderShippingService;
import cn.binarywang.wx.miniapp.api.WxMaService;
import cn.binarywang.wx.miniapp.api.impl.WxMaOrderShippingServiceImpl;
import cn.binarywang.wx.miniapp.bean.shop.request.shipping.*;
import cn.binarywang.wx.miniapp.bean.shop.response.WxMaOrderShippingInfoBaseResponse;
import cn.binarywang.wx.miniapp.bean.shop.response.WxMaOrderShippingIsTradeManagedResponse;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.date.DatePattern;
import cn.hutool.core.util.BooleanUtil;
import cn.hutool.core.util.DesensitizedUtil;
import cn.hutool.core.util.NumberUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.http.HttpUtil;
import com.alibaba.fastjson2.JSONArray;
import com.medusa.gruul.common.fastjson2.FastJson2;
import com.medusa.gruul.common.model.constant.CommonPool;
import com.medusa.gruul.common.model.enums.DistributionMode;
import com.medusa.gruul.common.model.enums.PayType;
import com.medusa.gruul.common.module.app.afs.AfsStatus;
import com.medusa.gruul.common.mp.IManualTransaction;
import com.medusa.gruul.common.mp.config.MybatisPlusConfig;
import com.medusa.gruul.common.mp.model.TenantShop;
import com.medusa.gruul.common.redis.annotation.Redisson;
import com.medusa.gruul.common.redis.util.RedisUtil;
import com.medusa.gruul.common.system.model.ISystem;
import com.medusa.gruul.common.system.model.model.Platform;
import com.medusa.gruul.common.wechat.WechatProperties;
import com.medusa.gruul.global.model.exception.GlobalException;
import com.medusa.gruul.global.model.helper.AmountCalculateHelper;
import com.medusa.gruul.order.api.constant.OrderConstant;
import com.medusa.gruul.order.api.entity.*;
import com.medusa.gruul.order.api.enums.ItemStatus;
import com.medusa.gruul.order.api.enums.PackageStatus;
import com.medusa.gruul.order.api.model.OrderPackageKeyDTO;
import com.medusa.gruul.order.api.model.wechat.logistics.DeliveryMode;
import com.medusa.gruul.order.api.model.wechat.logistics.LogisticsType;
import com.medusa.gruul.order.api.model.wechat.logistics.OrderUploadType;
import com.medusa.gruul.order.api.model.wechat.vo.DeliveryVO;
import com.medusa.gruul.order.api.model.wechat.vo.MiniAppDeliveryVO;
import com.medusa.gruul.order.service.model.dto.OrderDeliveryItemDTO;
import com.medusa.gruul.order.service.model.enums.OrderError;
import com.medusa.gruul.order.service.modules.afs.service.OrderAfsService;
import com.medusa.gruul.order.service.modules.deliver.service.DeliverService;
import com.medusa.gruul.order.service.mp.service.*;
import com.medusa.gruul.order.service.mq.service.OrderRabbitService;
import com.medusa.gruul.order.service.util.OrderUtil;
import com.medusa.gruul.payment.api.entity.PaymentInfo;
import com.medusa.gruul.payment.api.rpc.PaymentRpcService;
import io.vavr.Tuple;
import io.vavr.Tuple2;
import io.vavr.Tuple3;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me.chanjar.weixin.common.error.WxErrorException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.util.*;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Supplier;
import java.util.stream.Collectors;


/**
 * @author 张治保
 * date 2022/8/1
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class DeliverServiceImpl implements DeliverService {

    private static final String EXPRESS_COMPANY_URL = "https://api.weixin.qq.com/cgi-bin/express/delivery/open_msg/get_delivery_list?access_token=";
    private final IOrderService orderService;
    private final OrderAfsService orderAfsService;
    private final IShopOrderService shopOrderService;
    private final OrderRabbitService orderRabbitService;
    private final IShopOrderItemService shopOrderItemService;
    private final IShopOrderPackageService shopOrderPackageService;
    private final IOrderDiscountItemService orderDiscountItemService;
    private final WxMaService wxMaService;
    private final WechatProperties wechatProperties;
    private PaymentRpcService paymentRpcService;

    @Autowired
    @Lazy
    public void setPaymentRpcService(PaymentRpcService paymentRpcService) {
        this.paymentRpcService = paymentRpcService;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public BigDecimal splitShopOrderItems(ShopOrderPackage shopOrderPackage, OrderPackageKeyDTO orderPackageKey, List<OrderDeliveryItemDTO> orderDeliveryItem) {
        AtomicReference<BigDecimal> weight = new AtomicReference<>(BigDecimal.ZERO);
        List<Tuple2<ShopOrderItem, List<OrderDiscountItem>>> itemAndDiscounts = orderDeliveryItem.stream()
                .map(item -> perItemUpdate(weight, orderPackageKey, item))
                .toList();

        List<ShopOrderItem> splitItems = new ArrayList<>();
        List<OrderDiscountItem> discountItems = new ArrayList<>();
        itemAndDiscounts.forEach(
                tuple2 -> {
                    ShopOrderItem item = tuple2._1();
                    if (item == null) {
                        return;
                    }
                    splitItems.add(item);
                    List<OrderDiscountItem> currentDiscountItems = tuple2._2();
                    if (CollUtil.isNotEmpty(currentDiscountItems)) {
                        discountItems.addAll(currentDiscountItems);
                    }
                }
        );
        if (CollUtil.isNotEmpty(splitItems)) {
            TenantShop.disable(() -> shopOrderItemService.saveBatch(splitItems));
        }
        if (CollUtil.isNotEmpty(discountItems)) {
            TenantShop.disable(() -> orderDiscountItemService.saveOrUpdateBatch(discountItems));
        }
        return weight.get();
    }


    @Override
    @Transactional(rollbackFor = Exception.class)
    @Redisson(value = OrderConstant.ORDER_PACKAGE_LOCK, key = "#orderPackageKey.orderNo+':'+#orderPackageKey.shopId")
    public void packageConfirm(boolean isSystem, OrderPackageKeyDTO orderPackageKey, PackageStatus nextStatus) {
        String orderNo = orderPackageKey.getOrderNo();
        Long shopId = orderPackageKey.getShopId();
        Long buyerId = orderPackageKey.getBuyerId();
        //主订单
        Order order = orderService.lambdaQuery()
                .select(Order::getBuyerId, Order::getStatus, Order::getTimeout, Order::getDistributionMode,Order::getPlatform)
                .eq(Order::getNo, orderNo)
                .eq(buyerId != null, Order::getBuyerId, buyerId)
                .one();
        if (checkOrder(isSystem, order, () -> order.getStatus().isClosed())) {
            return;
        }
        ShopOrder shopOrder = TenantShop.disable(
                () -> shopOrderService.lambdaQuery()
                        .select(ShopOrder::getStatus, ShopOrder::getExtra)
                        .eq(ShopOrder::getOrderNo, orderNo)
                        .eq(ShopOrder::getShopId, shopId)
                        .one()
        );
        //如果是同城配送 并且未送达 则不允许确认收货
        if (
                DistributionMode.INTRA_CITY_DISTRIBUTION == order.getDistributionMode()
                        && !BooleanUtil.isTrue(shopOrder.getExtra().getIcReceivable())
        ) {
            throw new GlobalException("ic order cannot confirm yet.");
        }
        //店铺订单
        if (checkOrder(isSystem, shopOrder, () -> shopOrder.getStatus().isClosed())) {
            return;
        }
        List<ShopOrderItem> shopOrderItems = this.checkShopOrderPackageAndItems(isSystem, orderNo, shopId);
        if (CollUtil.isEmpty(shopOrderItems)) {
            return;
        }
        //更新商品包裹状态
        LocalDateTime confirmTime = LocalDateTime.now();
        boolean success = ISystem.shopId(
                shopId,
                () -> shopOrderItemService.lambdaUpdate()
                        .set(ShopOrderItem::getAfsStatus, AfsStatus.NONE)
                        .set(ShopOrderItem::getPackageStatus, nextStatus)
                        .set(ShopOrderItem::getUpdateTime, LocalDateTime.now())
                        .eq(ShopOrderItem::getOrderNo, orderNo)
                        .eq(ShopOrderItem::getStatus, ItemStatus.OK)
                        .eq(ShopOrderItem::getPackageStatus, PackageStatus.WAITING_FOR_RECEIVE)
                        .update()
        );
        if (!success) {
            return;
        }
        //更新包裹状态
        ISystem.shopId(
                shopId,
                () -> shopOrderPackageService.lambdaUpdate()
                        .set(ShopOrderPackage::getStatus, nextStatus)
                        .set(ShopOrderPackage::getConfirmTime, confirmTime)
                        .eq(ShopOrderPackage::getOrderNo, orderNo)
                        .eq(ShopOrderPackage::getStatus, PackageStatus.WAITING_FOR_RECEIVE)
                        .update()
        );
        //如果存在售后状态 并且售后可关闭 则直接关闭售后 如不存在 则直接回滚
        orderAfsService.closeAfsIfClosable(isSystem, shopOrderItems, (isSystem ? "系统" : "买家") + "确认收货自动关闭售后");
        IManualTransaction.afterCommit(
                () -> orderRabbitService.sendDelayEvaluate(orderPackageKey, order.getTimeout().getCommentTimeoutMills(), shopId)

        );


    }

    /**
     * 小程序订单 发货申请
     *
     * @param orderPackageKeyDTO 订单包裹关键 key
     */
    @Override
    public void miniAppOrderDeliver(OrderPackageKeyDTO orderPackageKeyDTO) throws Exception {
        WxMaOrderShippingService wxMaOrderShippingService = new WxMaOrderShippingServiceImpl(wxMaService);
        WxMaOrderShippingIsTradeManagedResponse tradeManagedResponse = wxMaOrderShippingService.isTradeManaged(wechatProperties.getAppId());
        Boolean tradeManaged = tradeManagedResponse.getTradeManaged();
        //判断小程序是否已开通发货信息管理服务
        if (!tradeManaged) {
            log.error("APPID【{}】小程序未开通发货信息管理服务,订单ID：{}，包裹ID{}",
                    wechatProperties.getAppId(),
                    orderPackageKeyDTO.getOrderNo(),
                    orderPackageKeyDTO.getPackageId());
            throw new GlobalException();
        }
        //支付信息
        PaymentInfo paymentInfo = paymentRpcService.getPaymentInfo(orderPackageKeyDTO.getOrderNo());
        if (OrderUploadType.UPLOAD_SHIPPING_INFO == orderPackageKeyDTO.getOrderUploadType()) {
            //发货信息录入
            miniAppUploadShippingInfo(wxMaOrderShippingService, orderPackageKeyDTO, paymentInfo);
        } else if (OrderUploadType.UPLOAD_COMBINED_SHIPPING_INFO == orderPackageKeyDTO.getOrderUploadType()) {
            //发货信息合单录入
            miniAppUploadCombinedShippingInfo(wxMaOrderShippingService, orderPackageKeyDTO, paymentInfo);
        }
    }

    /**
     * 小程序订单 退货发货录入
     *
     * @param shopOrderItem afsNo
     * @throws Exception 微信接口异常
     */
    @Override
    public void miniAppOrderReturnGoodsDeliver(ShopOrderItem shopOrderItem) throws Exception {
        //根据售后单号，获取退货订单信息
        List<ShopOrderItem> shopOrderItems = TenantShop.disable(() -> shopOrderItemService.lambdaQuery()
                .eq(ShopOrderItem::getAfsNo, shopOrderItem.getAfsNo())
                .list());
        String orderNo = shopOrderItems.get(CommonPool.NUMBER_ZERO).getOrderNo();
        Order order = orderService.lambdaQuery()
                .eq(Order::getNo, orderNo)
                .one();
        DistributionMode distribution = order.getDistributionMode();


        OrderPackageKeyDTO orderPackageKeyDTO = new OrderPackageKeyDTO();
        orderPackageKeyDTO.setOrderNo(order.getNo());
        orderPackageKeyDTO.setOrderUploadType(
                DistributionMode.EXPRESS == distribution
                        ? OrderUploadType.UPLOAD_COMBINED_SHIPPING_INFO
                        : OrderUploadType.UPLOAD_SHIPPING_INFO
        );
        orderPackageKeyDTO.setLogisticsType(OrderUtil.getLogisticsType(distribution));
        orderPackageKeyDTO.setDeliveryMode(
                DistributionMode.EXPRESS == distribution
                        ? DeliveryMode.SPLIT_DELIVERY
                        : DeliveryMode.UNIFIED_DELIVERY
        );
        miniAppOrderDeliver(orderPackageKeyDTO);
    }

    /**
     * 获取微信快递公司
     *
     * @return 快递公司
     */
    public Map<String, List<DeliveryVO>> getExpressCompany() {
        String deliveryArray = RedisUtil.getCache(
                () -> RedisUtil.getCacheObject(OrderConstant.ORDER_MINI_DELIVER_CACHE_KEY),
                () -> {
                    try {
                        String url = EXPRESS_COMPANY_URL.concat(wxMaService.getAccessToken());
                        String responseJson = HttpUtil.post(url, "{}");
                        MiniAppDeliveryVO result = FastJson2.convert(responseJson, MiniAppDeliveryVO.class);
                        if (result.getCode() != 0) {
                            throw new GlobalException();
                        } else {
                            return JSONArray.from(result.getDeliveryList()).toString();
                        }
                    } catch (Exception e) {
                        throw new GlobalException();
                    }
                },
                Duration.ofHours(CommonPool.NUMBER_EIGHT),
                OrderConstant.ORDER_MINI_DELIVER_CACHE_KEY
        );
        List<DeliveryVO> deliveryVOList = JSONArray.parseArray(deliveryArray, DeliveryVO.class);

        return deliveryVOList.stream()
                .filter(Objects::nonNull)
                .collect(Collectors.groupingBy(DeliveryVO::getDeliveryName));
    }

    /**
     * 发货信息录入
     *
     * @param wxMaOrderShippingService 发货信息管理接口
     */
    private void miniAppUploadShippingInfo(WxMaOrderShippingService wxMaOrderShippingService,
                                           OrderPackageKeyDTO orderPackageKeyDTO,
                                           PaymentInfo paymentInfo) throws WxErrorException {
        WxMaOrderShippingInfoUploadRequest wxMaOrderShippingInfo = new WxMaOrderShippingInfoUploadRequest();
        //上传物流信息的订单
        OrderKeyBean orderKeyBean = getOrderKeyBean(paymentInfo);
        Tuple3<List<ShopOrderItem>, Map<Long, List<ShopOrderItem>>, List<ShopOrderPackage>> shippingInfo = getShippingInfo(orderPackageKeyDTO);
        List<ShopOrderItem> shopOrderItemList = shippingInfo._1;
        //全部退货，未发货发，微信小程序物流录入全部退款的订单，一定要上传物流信息，这个bug解决了，这里以后可以直接return
        if (shopOrderItemList.isEmpty()) {
            shopOrderItemList = TenantShop.disable(() -> shopOrderItemService.lambdaQuery()
                    .eq(ShopOrderItem::getOrderNo, orderPackageKeyDTO.getOrderNo())
                    .list());
        }
        uploadShippingInfo(wxMaOrderShippingService, orderPackageKeyDTO, paymentInfo, wxMaOrderShippingInfo, orderKeyBean, shopOrderItemList);
    }

    /**
     * 发货信息录入
     *
     * @param wxMaOrderShippingService 发货信息管理接口
     */
    private void uploadShippingInfo(WxMaOrderShippingService wxMaOrderShippingService,
                                    OrderPackageKeyDTO orderPackageKeyDTO,
                                    PaymentInfo paymentInfo,
                                    WxMaOrderShippingInfoUploadRequest wxMaOrderShippingInfo,
                                    OrderKeyBean orderKeyBean,
                                    List<ShopOrderItem> shopOrderItemList) throws WxErrorException {
        List<ShippingListBean> shippingList = new ArrayList<>();
        //物流模式：同城配送、虚拟商品、用户自提
        String itemDesc = getItemDesc(shopOrderItemList);
        //物流信息 商品名称 限制最大120
        shippingList.add(
                ShippingListBean.builder().itemDesc(itemDesc.length() > CommonPool.NUMBER_ONE_HUNDRED_TWENTY
                        ? itemDesc.substring(CommonPool.NUMBER_ZERO, CommonPool.NUMBER_ONE_HUNDRED_TWENTY)
                        : itemDesc).build()
        );
        wxMaOrderShippingInfo.setOrderKey(orderKeyBean);
        //物流信息列表
        wxMaOrderShippingInfo.setShippingList(shippingList);
        //物流模式：1-快递 2-同城配送 3-虚拟商品 4-用户自提
        wxMaOrderShippingInfo.setLogisticsType(orderPackageKeyDTO.getLogisticsType().getValue());
        //发货模式：1-统一发货 2-分拆发货
        wxMaOrderShippingInfo.setDeliveryMode(orderPackageKeyDTO.getDeliveryMode().getValue());
        //分拆模式必填：是否已全部发货 false-否 true-是
        wxMaOrderShippingInfo.setIsAllDelivered(DeliveryMode.SPLIT_DELIVERY == orderPackageKeyDTO.getDeliveryMode() ? orderPackageKeyDTO.getIsAllDelivered() : null);
        //上传时间 RFC 3339 格式 如 2022-12-15T13:29:35.120+08:00
        wxMaOrderShippingInfo.setUploadTime(ZonedDateTime.now().format(DatePattern.UTC_MS_WITH_XXX_OFFSET_FORMAT.getDateTimeFormatter()));
        //支付者 openid
        wxMaOrderShippingInfo.setPayer(new PayerBean(paymentInfo.getOpenId()));
        //发货信息录入
        WxMaOrderShippingInfoBaseResponse response = wxMaOrderShippingService.upload(wxMaOrderShippingInfo);
        log.debug("微信小程序发货信息录入返回：{}，订单号：{}， 包裹ID：{}", response, orderPackageKeyDTO.getOrderNo(), orderPackageKeyDTO.getPackageId());
    }

    /**
     * 发货信息合单录入
     *
     * @param wxMaOrderShippingService 发货信息管理接口
     */
    private void miniAppUploadCombinedShippingInfo(WxMaOrderShippingService wxMaOrderShippingService,
                                                   OrderPackageKeyDTO orderPackageKeyDTO,
                                                   PaymentInfo paymentInfo) throws WxErrorException {
        //需要上传物流信息的订单
        OrderKeyBean orderKeyBean = getOrderKeyBean(paymentInfo);
        Tuple3<List<ShopOrderItem>, Map<Long, List<ShopOrderItem>>, List<ShopOrderPackage>> shippingInfo = getShippingInfo(orderPackageKeyDTO);
        List<ShopOrderItem> shopOrderItemList = shippingInfo._1;
        //快递发货 全部未发退货，上传无需物流，微信小程序物流录入全部退款的订单，一定要上传物流信息，这个bug解决了，这里以后可以直接return
        if (shopOrderItemList.isEmpty()) {
            shopOrderItemList = TenantShop.disable(() -> shopOrderItemService.lambdaQuery()
                    .eq(ShopOrderItem::getOrderNo, orderPackageKeyDTO.getOrderNo())
                    .list());
            List<ShopOrderItem> filterStatusShopOrderItems = shopOrderItemList
                    .stream()
                    .filter(shopOrderItem -> shopOrderItem.getStatus() == ItemStatus.CLOSED)
                    .collect(Collectors.toList());
            if (CollUtil.isEmpty(filterStatusShopOrderItems)) {
                log.warn("代表没有需要发送发货录入的商品");
                return;
            }
            WxMaOrderShippingInfoUploadRequest wxMaOrderShippingInfo = new WxMaOrderShippingInfoUploadRequest();
            orderPackageKeyDTO.setLogisticsType(LogisticsType.VIRTUAL_PRODUCT);
            orderPackageKeyDTO.setDeliveryMode(DeliveryMode.UNIFIED_DELIVERY);
            uploadShippingInfo(wxMaOrderShippingService, orderPackageKeyDTO, paymentInfo, wxMaOrderShippingInfo, orderKeyBean, shopOrderItemList);
            return;
        }

        WxMaOrderCombinedShippingInfoUploadRequest request = new WxMaOrderCombinedShippingInfoUploadRequest();
        //子单物流详情List
        List<WxMaOrderCombinedShippingInfoUploadRequest.SubOrderBean> subOrderBeans = new ArrayList<>();
        //子单物流信息列表
        List<ShippingListBean> shippingList = new ArrayList<>();
        //设置物流信息
        setShippingList(shippingInfo, shippingList);
        subOrderBeans.add(
                WxMaOrderCombinedShippingInfoUploadRequest.SubOrderBean.builder()
                        .orderKey(orderKeyBean)
                        //发货模式：1-统一发货 2-分拆发货
                        .deliveryMode(orderPackageKeyDTO.getDeliveryMode().getValue())
                        //物流模式：1-快递 2-同城配送 3-虚拟商品 4-用户自提
                        .logisticsType(orderPackageKeyDTO.getLogisticsType().getValue())
                        //分拆模式必填：是否已全部发货 false-否 true-是
                        .isAllDelivered(true)
                        .shippingList(shippingList).build()
        );
        request.setOrderKey(orderKeyBean);
        request.setSubOrders(subOrderBeans);
        request.setUploadTime(ZonedDateTime.now().format(DatePattern.UTC_MS_WITH_XXX_OFFSET_FORMAT.getDateTimeFormatter()));
        request.setPayer(new PayerBean(paymentInfo.getOpenId()));
        //发货信息合单录入
        WxMaOrderShippingInfoBaseResponse response = wxMaOrderShippingService.upload(request);
        log.debug("微信小程序发货信息合单录入返回：{}，订单号：{}， 包裹ID：{}", response, orderPackageKeyDTO.getOrderNo(), orderPackageKeyDTO.getPackageId());
    }

    /**
     * 获取商品信息名称
     *
     * @param items 店铺订单明细
     */
    private String getItemDesc(List<ShopOrderItem> items) {
        return items.stream()
                .map(ShopOrderItem::getProductName)
                .collect(Collectors.joining(StrUtil.COMMA));
    }

    /**
     * 订单包裹信息
     *
     * @param orderPackageKeyDTO 订单包裹关键key
     */
    private Tuple3<List<ShopOrderItem>, Map<Long, List<ShopOrderItem>>, List<ShopOrderPackage>> getShippingInfo(OrderPackageKeyDTO orderPackageKeyDTO) {
        //店铺订单待收货中，未在退货流程中
        List<ShopOrderItem> shopOrderItems = TenantShop.disable(() -> shopOrderItemService.lambdaQuery()
                .eq(ShopOrderItem::getOrderNo, orderPackageKeyDTO.getOrderNo())
                .eq(ShopOrderItem::getPackageStatus, PackageStatus.WAITING_FOR_RECEIVE)
                .in(ShopOrderItem::getAfsStatus, AfsStatus.NONE, AfsStatus.REFUND_REJECT, AfsStatus.RETURN_REFUND_REJECT, AfsStatus.BUYER_CLOSED, AfsStatus.SYSTEM_CLOSED)
                .list());
        //非快递发货，不需要获取物流信息
        if (LogisticsType.EXPRESS != orderPackageKeyDTO.getLogisticsType()) {
            return Tuple.of(shopOrderItems, new HashMap<>(CommonPool.NUMBER_ONE), new ArrayList<>());
        }
        Set<Long> packageIds = shopOrderItems.stream().map(ShopOrderItem::getPackageId).collect(Collectors.toSet());
        //包裹信息为空，不去获取物流信息
        if (CollUtil.isEmpty(packageIds)) {
            return Tuple.of(shopOrderItems, new HashMap<>(CommonPool.NUMBER_ONE), new ArrayList<>());
        }
        //根据包裹id 分组 ShopOrderItem
        Map<Long, List<ShopOrderItem>> shopOrderItemsMap = shopOrderItems.stream()
                .collect(Collectors.groupingBy(ShopOrderItem::getPackageId));
        //包裹信息
        List<ShopOrderPackage> shopOrderPackages = TenantShop.disable(() -> shopOrderPackageService.lambdaQuery()
                .in(ShopOrderPackage::getId, packageIds).list());
        return Tuple.of(shopOrderItems, shopOrderItemsMap, shopOrderPackages);
    }

    /**
     * 设置物流信息
     *
     * @param shippingInfo 店铺订单、包裹信息
     * @param shippingList 微信物流信息列表
     */
    private void setShippingList(Tuple3<List<ShopOrderItem>, Map<Long, List<ShopOrderItem>>,
            List<ShopOrderPackage>> shippingInfo, List<ShippingListBean> shippingList) {
        for (ShopOrderPackage shopOrderPackage : shippingInfo._3) {
            String itemDesc = getItemDesc(shippingInfo._2.get(shopOrderPackage.getId()));
            //子单物流信息列表
            shippingList.add(
                    //子单物流信息
                    ShippingListBean.builder()
                            .trackingNo(shopOrderPackage.getExpressNo())
                            .expressCompany(getExpressCompany().get(shopOrderPackage.getExpressCompanyName()).get(CommonPool.NUMBER_ZERO).getDeliveryId())
                            //商品信息，限制120字
                            .itemDesc(itemDesc.length() > CommonPool.NUMBER_ONE_HUNDRED_TWENTY
                                    ? itemDesc.substring(0, CommonPool.NUMBER_ONE_HUNDRED_TWENTY)
                                    : itemDesc)
                            .contact(
                                    //联系方式
                                    ContactBean.builder()
                                            .receiverContact(
                                                    DesensitizedUtil.mobilePhone(shopOrderPackage.getReceiverMobile())
                                            ).build()
                            ).build()
            );
        }
    }

    /**
     * 需要上传物流信息的订单
     *
     * @param paymentInfo 支付信息
     * @return 需要上传物流信息的订单
     */
    private OrderKeyBean getOrderKeyBean(PaymentInfo paymentInfo) {
        //上传物流信息的订单
        OrderKeyBean orderKeyBean = new OrderKeyBean();
        //订单号类型 2：微信支付单号
        orderKeyBean.setOrderNumberType(CommonPool.NUMBER_TWO);
        //微信支付订单号
        orderKeyBean.setTransactionId(paymentInfo.getTransactionId());
        return orderKeyBean;
    }

    /**
     * 检查订单是否存在 与检查订单是否已关闭
     */
    private boolean checkOrder(boolean isSystem, Object order, Supplier<Boolean> closeCondition) {
        boolean isNull = Objects.isNull(order);
        boolean isClosed = closeCondition.get();
        if (isSystem) {
            return isNull || isClosed;
        }
        OrderError.ORDER_NOT_EXIST.trueThrow(isNull);
        OrderError.ORDER_CLOSED.trueThrow(isClosed);
        return false;
    }

    /**
     * 检查是否有未发货商品
     *
     * @param isSystem 是否系统调用
     * @param orderNo  订单号
     * @param shopId   店铺id
     * @return 未发货商品item集合
     */
    private List<ShopOrderItem> checkShopOrderPackageAndItems(boolean isSystem, String orderNo, Long shopId) {
        //检查订单商品是否有未发货商品
        Boolean hasWaitingForDeliverItems = TenantShop.disable(
                () -> shopOrderItemService.lambdaQuery()
                        .eq(ShopOrderItem::getOrderNo, orderNo)
                        .eq(ShopOrderItem::getShopId, shopId)
                        .eq(ShopOrderItem::getStatus, ItemStatus.OK)
                        .eq(ShopOrderItem::getPackageStatus, PackageStatus.WAITING_FOR_DELIVER)
                        .exists()
        );
        if (BooleanUtil.isTrue(hasWaitingForDeliverItems)) {
            if (isSystem) {
                return Collections.emptyList();
            }
            throw OrderError.CONTAIN_NOT_WAIT_DELIVER_GOODS_NOT_CONFIRM.exception();
        }
        List<ShopOrderItem> shopOrderItems = ISystem.shopId(
                shopId,
                () -> shopOrderItemService.lambdaQuery()
                        .eq(ShopOrderItem::getOrderNo, orderNo)
                        .eq(ShopOrderItem::getShopId, shopId)
                        .eq(ShopOrderItem::getStatus, ItemStatus.OK)
                        .eq(ShopOrderItem::getPackageStatus, PackageStatus.WAITING_FOR_RECEIVE)
                        .list()
        );
        if (CollUtil.isEmpty(shopOrderItems)) {
            return Collections.emptyList();
        }
        //检查售后状态不能更改物流状态的
        if (shopOrderItems.stream().anyMatch(shopOrderItem -> !shopOrderItem.getAfsStatus().isCanChangePackageStatus())) {
            if (isSystem) {
                return Collections.emptyList();
            }
            throw OrderError.AFS_CANNOT_CLOSE.exception();
        }
        return shopOrderItems;

    }


    private Tuple2<ShopOrderItem, List<OrderDiscountItem>> perItemUpdate(AtomicReference<BigDecimal> weight, OrderPackageKeyDTO orderPackageKey, OrderDeliveryItemDTO item) {
        String orderNo = orderPackageKey.getOrderNo();
        Long shopId = orderPackageKey.getShopId();
        Long packageId = orderPackageKey.getPackageId();
        Long itemId = item.getItemId();
        Integer num = item.getNum();
        ShopOrderItem shopOrderItem;
        int numLeft;
        int preNum;
        while (true) {
            shopOrderItem = TenantShop.disable(
                    () -> shopOrderItemService.lambdaQuery()
                            .eq(ShopOrderItem::getOrderNo, orderNo)
                            .eq(ShopOrderItem::getShopId, shopId)
                            .eq(ShopOrderItem::getId, itemId)
                            .eq(ShopOrderItem::getPackageStatus, PackageStatus.WAITING_FOR_DELIVER)
                            .isNull(ShopOrderItem::getPackageId)
                            .one()
            );
            if (shopOrderItem == null) {
                throw OrderError.CONTAIN_NOT_WAIT_DELIVER_GOODS.dataEx(orderPackageKey);
            }
            if (ItemStatus.OK != shopOrderItem.getStatus()) {
                throw OrderError.ORDER_CLOSED.dataEx(orderPackageKey);
            }
            AfsStatus afsStatus = shopOrderItem.getAfsStatus();
            if (StrUtil.isNotBlank(shopOrderItem.getAfsNo()) && !afsStatus.isCanChangePackageStatus()) {
                throw OrderError.AFS_CANNOT_CLOSE.dataEx(orderPackageKey);
            }
            preNum = shopOrderItem.getNum();
            numLeft = preNum - num;
            if (numLeft < 0) {
                throw OrderError.SPLIT_NUM_ERROR.exception(orderPackageKey);
            }
            boolean success = TenantShop.disable(
                    () -> shopOrderItemService.lambdaUpdate()
                            .set(ShopOrderItem::getPackageId, packageId)
                            .set(ShopOrderItem::getAfsStatus, AfsStatus.NONE)
                            .set(ShopOrderItem::getPackageStatus, PackageStatus.WAITING_FOR_RECEIVE)
                            .set(ShopOrderItem::getUpdateTime, LocalDateTime.now())
                            .set(ShopOrderItem::getNum, num)
                            .eq(ShopOrderItem::getPackageStatus, PackageStatus.WAITING_FOR_DELIVER)
                            .eq(ShopOrderItem::getOrderNo, orderNo)
                            .eq(ShopOrderItem::getShopId, shopId)
                            .eq(ShopOrderItem::getId, itemId)
                            .isNull(ShopOrderItem::getPackageId)
                            .update()
            );
            if (success) {
                weight.set(NumberUtil.add(NumberUtil.mul(shopOrderItem.getWeight(), num)));
                break;
            }
        }
        orderAfsService.closeAfsIfClosable(Boolean.TRUE, Collections.singletonList(shopOrderItem), "商家发货，自动关闭售后");
        ShopOrderItem newItem = newItem(numLeft, shopOrderItem);
        return Tuple.of(newItem, newItem == null ? null : newDiscountItems(preNum, shopOrderItem.getId(), newItem));

    }

    private List<OrderDiscountItem> newDiscountItems(int preNum, Long preItemId, ShopOrderItem newItem) {
        Long shopId = newItem.getShopId();
        List<OrderDiscountItem> discountItems = orderDiscountItemService.getItem(newItem.getOrderNo(), shopId, preItemId);
        List<OrderDiscountItem> newDiscountItems = new ArrayList<>(CommonPool.NUMBER_THIRTY);
        int num = newItem.getNum();
        Long itemId = newItem.getId();
        discountItems.forEach(
                discountItem -> {
                    long discountAmount = discountItem.getDiscountAmount();
                    Long avgDiscount = AmountCalculateHelper.avgAmount(discountAmount, preNum, RoundingMode.DOWN);
                    long newDiscountAmount = avgDiscount * num;
                    discountItem.setDiscountAmount(discountAmount - newDiscountAmount);
                    newDiscountItems.add(discountItem);
                    newDiscountItems.add(
                            new OrderDiscountItem()
                                    .setDiscountId(discountItem.getDiscountId())
                                    .setShopId(shopId)
                                    .setItemId(itemId)
                                    .setDiscountAmount(newDiscountAmount)
                    );

                }
        );
        return newDiscountItems;
    }

    private ShopOrderItem newItem(int numLeft, ShopOrderItem shopOrderItem) {
        if (numLeft <= 0) {
            return null;
        }
        ShopOrderItem item = new ShopOrderItem().setStatus(ItemStatus.OK)
                .setOrderNo(shopOrderItem.getOrderNo())
                .setShopId(shopOrderItem.getShopId())
                .setProductId(shopOrderItem.getProductId())
                .setPackageId(null)
                .setPackageStatus(PackageStatus.WAITING_FOR_DELIVER)
                .setAfsNo(null)
                .setAfsStatus(AfsStatus.NONE)
                .setProductName(shopOrderItem.getProductName())
                .setSkuId(shopOrderItem.getSkuId())
                .setSpecs(shopOrderItem.getSpecs())
                .setNum(numLeft)
                .setImage(shopOrderItem.getImage())
                .setWeight(shopOrderItem.getWeight())
                .setFreightTemplateId(shopOrderItem.getFreightTemplateId())
                .setFreightPrice(0L)
                .setSalePrice(shopOrderItem.getSalePrice())
                .setDealPrice(shopOrderItem.getDealPrice())
                .setFixPrice(0L)
                .setSellType(shopOrderItem.getSellType())
                .setSupplierId(shopOrderItem.getSupplierId())
                .setExtra(shopOrderItem.getExtra())
                .setServices(shopOrderItem.getServices());
        item.setId(MybatisPlusConfig.IDENTIFIER_GENERATOR.nextId(item).longValue());
        return item;

    }

}
