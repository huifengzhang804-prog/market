package com.medusa.gruul.order.service.modules.order.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.map.MapUtil;
import cn.hutool.core.util.BooleanUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.medusa.gruul.common.model.base.ActivityShopProductSkuKey;
import com.medusa.gruul.common.model.base.ShopProductKey;
import com.medusa.gruul.common.model.base.ShopProductSkuKey;
import com.medusa.gruul.common.model.constant.CommonPool;
import com.medusa.gruul.common.model.enums.DistributionMode;
import com.medusa.gruul.common.model.enums.OrderType;
import com.medusa.gruul.common.model.enums.SellType;
import com.medusa.gruul.common.module.app.afs.AfsStatus;
import com.medusa.gruul.common.mp.config.MybatisPlusConfig;
import com.medusa.gruul.common.mp.model.TenantShop;
import com.medusa.gruul.common.security.model.bean.SecureUser;
import com.medusa.gruul.common.system.model.ISystem;
import com.medusa.gruul.common.system.model.model.Platform;
import com.medusa.gruul.global.model.helper.CompletableTask;
import com.medusa.gruul.global.model.o.FormInput;
import com.medusa.gruul.goods.api.entity.Product;
import com.medusa.gruul.goods.api.model.dto.*;
import com.medusa.gruul.goods.api.model.enums.ProductStatus;
import com.medusa.gruul.goods.api.rpc.Good2OrderRpcService;
import com.medusa.gruul.order.api.addon.activity.ActivityParam;
import com.medusa.gruul.order.api.addon.activity.ActivityResp;
import com.medusa.gruul.order.api.entity.*;
import com.medusa.gruul.order.api.enums.ItemStatus;
import com.medusa.gruul.order.api.enums.OrderStatus;
import com.medusa.gruul.order.api.enums.PackageStatus;
import com.medusa.gruul.order.api.enums.ShopOrderStatus;
import com.medusa.gruul.order.api.helper.OrderHelper;
import com.medusa.gruul.order.api.model.ItemExtra;
import com.medusa.gruul.order.service.model.bo.MemberOrder;
import com.medusa.gruul.order.service.model.dto.OrderShopsDTO;
import com.medusa.gruul.order.service.model.dto.ProductDTO;
import com.medusa.gruul.order.service.model.dto.ReceiverDTO;
import com.medusa.gruul.order.service.model.dto.ShopPackageDTO;
import com.medusa.gruul.order.service.model.enums.OrderError;
import com.medusa.gruul.order.service.modules.order.addon.OrderAddonSupporter;
import com.medusa.gruul.order.service.modules.order.service.GenerateOrderService;
import com.medusa.gruul.order.service.modules.order.service.OrderConfigService;
import com.medusa.gruul.order.service.mp.service.IShopOrderItemService;
import com.medusa.gruul.order.service.util.OrderUtil;
import com.medusa.gruul.storage.api.entity.StorageSku;
import com.medusa.gruul.storage.api.enums.LimitType;
import com.medusa.gruul.storage.api.enums.StockType;
import com.medusa.gruul.storage.api.rpc.StorageRpcService;
import com.medusa.gruul.user.api.model.UserDataVO;
import com.medusa.gruul.user.api.model.vo.MemberAggregationInfoVO;
import com.medusa.gruul.user.api.rpc.UserRpcService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.Executor;
import java.util.stream.Collectors;

/**
 * 组装订单数据 服务类
 *
 * @author 张治保
 * date 2022/6/13
 */
@Service
@RequiredArgsConstructor
public class GenerateOrderServiceImpl implements GenerateOrderService {

    private final Executor globalExecutor;
    private final StorageRpcService storageRpcService;
    private final UserRpcService userRpcService;
    private final IShopOrderItemService shopOrderItemService;
    private final OrderAddonSupporter orderAddonSupporter;
    private final OrderConfigService orderConfigService;
    private final Good2OrderRpcService good2OrderRpcService;

    /**
     * 组装订单数据
     *
     * @param loginUser  当前登录的用户资料
     * @param orderShops 订单请求参数
     * @return 组装后的订单数据
     */
    @Override
    public MemberOrder getOrder(SecureUser<?> loginUser, OrderShopsDTO orderShops) {
        Order order = new Order();
        //平台类型
        Platform platform = ISystem.platformMust();
        //生成订单号
        String orderNo = OrderHelper.orderNo();
        //设置订单基本信息
        order.setDistributionMode(orderShops.getDistributionMode())
                .setSource(orderShops.getSource())
                .setBuyerId(loginUser.getId())
                .setPlatform(platform)
                .setNo(orderNo)
                .setStatus(OrderStatus.UNPAID)
                .setType(orderShops.getOrderType())
                .setActivityId(orderShops.activityId())
                .setIsPriority(Boolean.FALSE)
                .setExtra(orderShops.getExtra())
                .setCreateTime(LocalDateTime.now());

        //设置线程安全的 店铺订单列表
        //组装店铺订单与店铺订单商品任务
        MemberOrder memberOrder = new MemberOrder()
                .setOrder(order)
                .setPc(Platform.PC == ISystem.platformMust());
        //执行任务
        Collection<ShopOrder> shopOrders = new ConcurrentLinkedQueue<>();
        CompletableTask.getOrThrowException(CompletableFuture.allOf(this.assembleTasks(orderShops, memberOrder, shopOrders)));
        //店铺订单渲染
        order.setShopOrders(new ArrayList<>(shopOrders));
        //重置收货人资料
        this.resetEmptyOrderReceiver(loginUser.getMobile(), order.getBuyerNickname(), order.getOrderReceiver());
        //活动价
        Map<ShopProductSkuKey, Long> skuKeyPriceMap = order.getActivityResp().getSkuKeyPriceMap();
        if (CollUtil.isEmpty(skuKeyPriceMap)) {
            return memberOrder;
        }
        //根据活动价设置订单商品价格
        shopOrders.stream()
                .flatMap(shopOrder -> shopOrder.getShopOrderItems().stream())
                .forEach(
                        shopOrderItem -> {
                            Long price = skuKeyPriceMap.get(shopOrderItem.shopProductSkuKey());
                            if (price != null) {
                                long activitySalePrice = Math.max(price + shopOrderItem.getExtra().getAttributeMoney(), 0);
                                shopOrderItem.setSalePrice(activitySalePrice);
                                shopOrderItem.setDealPrice(activitySalePrice);
                            }
                        }
                );

        return memberOrder;
    }

    /**
     * 重置收货人资料 如果收货人对应信息为空 则使用当前登录的用户资料
     *
     * @param loginUserMobile 登录用户手机号
     * @param buyerNickname   买家昵称
     * @param orderReceiver   当前订单收货地址信息
     */
    private void resetEmptyOrderReceiver(String loginUserMobile, String buyerNickname, OrderReceiver orderReceiver) {
        //重置 收货人资料
        //如果为空则使用当前登录的用户资料
        //收货人
        if (StrUtil.isEmpty(orderReceiver.getName())) {
            orderReceiver.setName(buyerNickname);
        }
        //收货人手机号
        if (StrUtil.isEmpty(orderReceiver.getMobile())) {
            orderReceiver.setMobile(loginUserMobile);
        }
    }

    /**
     * 生成 店铺商品 sku id组成的key集合
     *
     * @param orderShops 订单店铺信息
     * @return 店铺商品 sku id组成的key集合
     */
    private Map<ShopProductSkuKey, Integer> skuKeyMap(OrderShopsDTO orderShops) {
        Map<ShopProductSkuKey, Integer> skuKeyMap = new HashMap<>(CommonPool.NUMBER_FIFTEEN);
        for (ShopPackageDTO shopPackage : orderShops.getShopPackages()) {
            Long shopId = shopPackage.getId();
            for (ProductDTO product : shopPackage.getProducts()) {
                skuKeyMap.put(
                        new ShopProductSkuKey()
                                .setShopId(shopId)
                                .setProductId(product.getId())
                                .setSkuId(product.getSkuId()),
                        product.getNum()
                );
            }
        }
        return skuKeyMap;
    }

    /**
     * 检查订单活动配置
     *
     * @param orderShops 订单店铺信息
     * @param order      订单信息
     * @return 活动配置
     */
    private ActivityResp activityResp(OrderShopsDTO orderShops, Order order) {
        OrderType orderType = orderShops.getOrderType();
        ActivityParam activityParam = orderShops.getActivity();
        if (activityParam == null || OrderType.COMMON == orderType) {
            return new ActivityResp().commonOrder();
        }
        activityParam
                .setOrderNo(order.getNo())
                .setUserId(order.getBuyerId())
                .setSkuKeyMap(skuKeyMap(orderShops));
        //是否是预计算 预计算则不进行事务 处理 只做查询
        return orderShops.getBudget() ? orderAddonSupporter.activityBudget(orderType, activityParam) : orderAddonSupporter.activity(orderType, activityParam);
    }

    /**
     * 设置超时时间节点   设置收货地址
     */
    private void timeoutSettingAndReceiverAddress(OrderShopsDTO orderShops, Order order) {

        ActivityResp activityResp = this.activityResp(orderShops, order);

        order.setActivityResp(activityResp);
        order.getExtra().putAll(activityResp.getExtra());
        //系统支付超时配置
        OrderTimeout timeout = orderConfigService.timeout();
        Duration payTimeout = activityResp.getStackable().getPayTimeout();
        if (payTimeout != null) {
            timeout.setPayTimeout(payTimeout.getSeconds());
        }
        order.setTimeout(timeout)
                .setOrderReceiver(
                        this.getOrderReceiver(order.getNo(), orderShops.getReceiver())
                );
    }

    /**
     * 查询会员信息
     */
    private void memberInfo(Long buyerId, Order order, MemberOrder memberOrder) {
        if (memberOrder.isPc()) {
            UserDataVO userDataVO = userRpcService.userData(buyerId);
            if (userDataVO == null) {
                throw OrderError.USER_INFO_NOT_COMPLETE.exception();
            }
            order.setBuyerNickname(userDataVO.getNickname());
            order.setBuyerAvatar(userDataVO.getAvatar());
            return;
        }
        //查询会员信息
        MemberAggregationInfoVO topMemberCardInfo = userRpcService.getTopMemberCardInfo(buyerId);
        order.setBuyerNickname(topMemberCardInfo.getUserNickname());
        order.setBuyerAvatar(topMemberCardInfo.getUserHeadPortrait());
        memberOrder.setMember(topMemberCardInfo);
    }

    /**
     * 组装 异步任务
     *
     * @param orderShops  店铺订单参数
     * @param memberOrder 会员订单
     * @return 所有异步任务
     */
    @SuppressWarnings("unchecked")
    private CompletableFuture<Void>[] assembleTasks(OrderShopsDTO orderShops, MemberOrder memberOrder, Collection<ShopOrder> shopOrders) {
        Order order = memberOrder.getOrder();
        Long buyerId = order.getBuyerId();
        List<ShopPackageDTO> shopPackages = orderShops.getShopPackages();
        DistributionMode distributionMode = orderShops.getDistributionMode();
        CompletableFuture<Void>[] tasks = new CompletableFuture[shopPackages.size() + 2];

        //设置超时时间节点   设置收货地址
        tasks[0] = CompletableFuture.runAsync(() -> timeoutSettingAndReceiverAddress(orderShops, order), globalExecutor);
        //设置买家昵称并设置 并收集会员信息
        tasks[1] = CompletableFuture.runAsync(() -> memberInfo(buyerId, order, memberOrder), globalExecutor);
        //组装 店铺订单信息 与 订单商品信息 任务 数据
        Set<Long> shopIds = shopPackages.stream().map(ShopPackageDTO::getId).collect(Collectors.toSet());

        //批量获取商品信息
        Map<Long, Map<Long, Product>> shopProductMap = good2OrderRpcService.productBatch(
                shopPackages.stream()
                        .flatMap(
                                shopPackage -> {
                                    Long shopId = shopPackage.getId();
                                    return shopPackage.getProducts()
                                            .stream()
                                            .map(product -> new ShopProductKey(shopId, product.getId()));
                                }
                        )
                        .collect(Collectors.toSet())
        );
        //获取店铺订单表单
        Map<Long, OrderForm> shopOrderFormMap = orderConfigService.orderForms(shopIds);
        boolean isBudget = orderShops.getBudget();
        for (int index = 0; index < shopPackages.size(); index++) {
            int currentIndex = index;
            int currentIdx = index + 1;
            tasks[index + 2] = CompletableFuture.runAsync(
                    () -> {
                        ShopPackageDTO shopPackage = shopPackages.get(currentIndex);
                        Long shopId = shopPackage.getId();
                        shopOrders.add(
                                //校验店铺订单表单
                                this.getShopOrder(
                                        shopProductMap.get(shopId),
                                        //预计算忽略表单校验
                                        isBudget ? null : this.validShopRemarkForm(shopOrderFormMap.get(shopId), shopId, shopPackage.getRemark()),
                                        buyerId,
                                        currentIdx,
                                        order,
                                        shopPackage,
                                        distributionMode
                                )
                        );
                    },
                    this.globalExecutor
            );
        }
        return tasks;
    }

    @Override
    public OrderReceiver getOrderReceiver(String orderNo, ReceiverDTO receiver) {
        OrderReceiver orderReceiver = new OrderReceiver()
                .setOrderNo(orderNo);
        if (receiver == null) {
            return orderReceiver
                    .setName(StrUtil.EMPTY)
                    .setMobile(StrUtil.EMPTY)
                    .setArea(List.of())
                    .setAddress(StrUtil.EMPTY);
        }
        return orderReceiver
                .setName(receiver.getName())
                .setMobile(receiver.getMobile())
                .setLocation(receiver.getLocation())
                .setArea(receiver.getArea())
                .setAddress(receiver.getAddress());
    }

    /**
     * 店铺备注表单校验
     *
     * @param shopId     店铺id
     * @param userRemark 用户填写的备注信息
     * @return 校验通过的 店铺订单表单详情
     */
    private ShopOrder.Remark validShopRemarkForm(OrderForm orderForm, Long shopId, Map<String, String> userRemark) {
        boolean notify = BooleanUtil.isTrue(orderForm.getOrderNotify());
        ShopOrder.Remark remark = new ShopOrder.Remark()
                .setOrderNotify(notify);
        /* 店铺表单设置
         */
        List<FormInput> customForms = orderForm.getCustomForm();
        //如果未开启 店铺表单 或者 店铺表单为空 则直接返回
        if (CollUtil.isEmpty(customForms)) {
            return remark;
        }
        List<ShopOrder.RemarkItem> remarkItems = new ArrayList<>();
        for (FormInput customForm : customForms) {
            String key = customForm.getKey();
            String value = userRemark.get(key);
            //表单校验未通过
            if (!customForm.valid(value)) {
                throw OrderError.SHOP_ORDER_FORM_VALID_FAIL.dataEx(new ShopProductKey().setShopId(shopId));
            }
            if (StrUtil.isEmpty(value)) {
                continue;
            }
            remarkItems.add(
                    new ShopOrder.RemarkItem()
                            .setKey(key)
                            .setValue(value.trim())
                            .setType(customForm.getType())
            );
        }
        return remark.setItems(remarkItems);
    }

    @Override
    public ShopOrder getShopOrder(
            Map<Long, Product> productMap,
            ShopOrder.Remark remark,
            Long userId,
            int index,
            Order order,
            ShopPackageDTO shopPackage,
            DistributionMode distributionMode
    ) {
        Long shopId = shopPackage.getId();
        if (MapUtil.isEmpty(productMap)) {
            throw OrderError.GOODS_NOT_AVAILABLE.dataEx(new ShopProductKey(shopId, null));
        }
        ShopOrder shopOrder = new ShopOrder();
        //是否是预计算，预计算时店铺备注模板为空
        shopOrder.setNo(OrderHelper.shopOrderNo(order.getNo(), index))
                .setStatus(ShopOrderStatus.OK)
                .setShopId(shopId)
                .setOrderNo(order.getNo())
                .setShopName(shopPackage.getName())
                .setShopLogo(shopPackage.getLogo())
                .setRemark(remark)
                .setCreateTime(order.getCreateTime());
        // remark 不为空表示不是价格预计算
        //非价格预算 且 如果是同城配送 或者 门店自提 生成取件码
        if (remark != null && (DistributionMode.INTRA_CITY_DISTRIBUTION == distributionMode
                || DistributionMode.SHOP_STORE == distributionMode)) {
            shopOrder.setExtra(
                    new ShopOrder.Extra()
                            .setPickupCode(OrderUtil.pickupCode(shopId, distributionMode))
            );
        } else {
            shopOrder.setExtra(ShopOrder.Extra.EMPTY);
        }

        shopOrder.setShopOrderItems(
                this.getShopOrderItems(
                        productMap,
                        userId,
                        shopOrder,
                        shopPackage.getProducts(),
                        order.getType(),
                        order.getActivityId(),
                        distributionMode)
        );
        return shopOrder;
    }

    @Override
    public List<ShopOrderItem> getShopOrderItems(
            Map<Long, Product> productMap,
            Long userId,
            ShopOrder shopOrder,
            List<ProductDTO> products,
            OrderType activityType,
            Long activityId,
            DistributionMode distributionMode
    ) {
        //是否是预计算，预计算时店铺备注模板为空
        boolean isBudget = shopOrder.getRemark() == null;
        Long shopId = shopOrder.getShopId();
        String orderNo = shopOrder.getOrderNo();
        //收集店铺订单商品项
        List<ShopOrderItem> items = new ArrayList<>(CommonPool.NUMBER_FIFTEEN);
        //缓存商品购买数量
        Map<Long, Integer> productNumMap = new HashMap<>(CommonPool.NUMBER_FIFTEEN);
        for (ProductDTO product : products) {
            Long productId = product.getId();
            Product productInfo = productMap.get(productId);
            //校验商品
            if (productInfo == null || ProductStatus.SELL_ON != productInfo.getStatus()) {
                throw OrderError.GOODS_NOT_AVAILABLE.dataEx(new ShopProductKey(shopId, productId));
            }
            //商品校验 并获取sku信息
            StorageSku storageSku = validProduct(
                    isBudget,
                    userId,
                    activityType,
                    activityId,
                    shopId,
                    product,
                    productNumMap.compute(productId, (k, v) -> {
                                //校验是否支持改配送方式
                                if (!productInfo.isSupportDistributionMode(distributionMode)) {
                                    throw OrderError.NOT_SUPPORT_DISTRIBUTION_MODE.dataEx(new ShopProductKey(shopId, productId));
                                }
                                return v == null ? product.getNum() : v + product.getNum();
                            }
                    )
            );
            // sku 原售价
            Long price = storageSku.getSalePrice();
            ProductExtraDTO extra = productInfo.getExtra();
            if (SellType.CONSIGNMENT == productInfo.getSellType()) {
                this.adjustConsignmentPricing(extra.getConsignmentPriceSetting(), storageSku);
            }
            //原始规格
            List<String> specs = new ArrayList<>(CollUtil.emptyIfNull(storageSku.getSpecs()));
            // 商品属性价格
            long attributeAmount = this.attributeAmount(extra, product.getProductFeaturesValue(), specs);
            //原售价
            price = Math.max(price + attributeAmount, 0);
            //实际售价
            long salePrice = Math.max(storageSku.getSalePrice() + attributeAmount, 0);
            //渲染数据
            ShopOrderItem shopOrderItem = (ShopOrderItem) new ShopOrderItem()
                    .setStatus(ItemStatus.OK)
                    .setShopId(shopId)
                    .setOrderNo(orderNo)
                    .setPackageStatus(PackageStatus.WAITING_FOR_DELIVER)
                    .setAfsStatus(AfsStatus.NONE)
                    .setProductId(productId)
                    .setProductName(productInfo.getName())
                    .setProductType(productInfo.getProductType())
                    .setServices(productInfo.getServiceIds())
                    .setSkuId(product.getSkuId())
                    .setSpecs(specs)
                    .setNum(product.getNum())
                    .setWeight(storageSku.getWeight())
                    .setFreightTemplateId(productInfo.getFreightTemplateId())
                    .setFreightPrice(0L)
                    .setSupplierId(productInfo.getSupplierId())
                    .setSellType(productInfo.getSellType())
                    .setExtra(
                            new ItemExtra()
                                    //服务费 分润比率
                                    .setProfitRate(ObjectUtil.defaultIfNull(extra.getCustomDeductionRatio(), CommonPool.NUMBER_ZERO).intValue())
                                    //商品属性价
                                    .setAttributeMoney(attributeAmount)
                                    //sku 原价
                                    .setSkuPracticalSalePrice(price)
                                    //服务费 供应商分润比率
                                    .setSupplierProfitRate(ObjectUtil.defaultIfNull(extra.getSupplierCustomDeductionRatio(), CommonPool.NUMBER_ZERO).intValue())
                    )
                    .setSalePrice(salePrice)
                    .setDealPrice(salePrice)
                    .setFixPrice(0L)
                    .setCreateTime(shopOrder.getCreateTime());
            //sku图片不存在则使用商品主图
            String image = storageSku.getImage();
            shopOrderItem.setImage(StrUtil.isEmpty(image) ? productInfo.getPic() : image);
            shopOrderItem.setId(MybatisPlusConfig.IDENTIFIER_GENERATOR.nextId(shopOrderItem).longValue());
            items.add(shopOrderItem);
        }
        return items;
    }

    /**
     * 计算当前商品属性价格
     *
     * @param productExtra     商品额外信息
     * @param userSelectedForm 用户选择的商品属性表单
     * @param specs            商品规格
     * @return 商品属性价格
     */
    private long attributeAmount(ProductExtraDTO productExtra, Map<Long, Set<Long>> userSelectedForm, List<String> specs) {
        Set<ProductFeaturesValueDTO> productFeaturesValueSet = productExtra.checkProductAttributes(userSelectedForm);
        if (CollUtil.isEmpty(productFeaturesValueSet)) {
            return CommonPool.NUMBER_ZERO;
        }
        long amount = CommonPool.NUMBER_ZERO;
        for (ProductFeaturesValueDTO productFeaturesValue : productFeaturesValueSet) {
            for (FeatureValueDTO value : productFeaturesValue.getFeatureValues()) {
                long price = value.getSecondValue();
                //追加商品属性到规格列表中
                specs.add(OrderHelper.attr2Spec(value.getFirstValue(), price));
                amount += price;
            }
        }
        return amount;
    }

    /**
     * 商品与sku校验
     *
     * @param isBudget   是否是预算
     * @param shopId     店铺id
     * @param product    购买的商品与数量
     * @param activityId 活动id
     * @return 商品与sku元组
     */
    private StorageSku validProduct(
            boolean isBudget,
            Long userId,
            OrderType activityType,
            Long activityId,
            Long shopId,
            ProductDTO product,
            int productCount
    ) {
        Long productId = product.getId();
        Long skuId = product.getSkuId();
        Integer num = product.getNum();
        ActivityShopProductSkuKey shopProductSkuKey = new ActivityShopProductSkuKey().setSkuId(skuId);
        shopProductSkuKey.setProductId(productId)
                .setShopId(shopId)
                .setActivityType(activityType)
                .setActivityId(activityId);
        //校验sku
        StorageSku storageSku = storageRpcService.getProductSku(shopProductSkuKey);
        if (storageSku == null) {
            throw OrderError.GOODS_NOT_AVAILABLE.dataEx(shopProductSkuKey);
        }
        //如果是预计算 啧不检查库存 与限购
        if (isBudget) {
            return storageSku;
        }
        //校验库存
        if (StockType.UNLIMITED != storageSku.getStockType() && num > storageSku.getStock()) {
            throw OrderError.GOODS_STOCK_NOT_ENOUGH.dataEx(shopProductSkuKey);
        }
        //检查限购
        this.checkLimit(userId, storageSku.getLimitType(), storageSku.getLimitNum(), num, productCount, shopProductSkuKey);
        return storageSku;
    }

    /**
     * 校验限购
     *
     * @param userId            用户id
     * @param limitType         限购类型
     * @param limitNum          限购数量
     * @param num               购买数量
     * @param productCount      当前订单该商品购买的数量统计
     * @param shopProductSkuKey 商品sku的关键key
     */
    private void checkLimit(Long userId, LimitType limitType, int limitNum, int num, int productCount, ActivityShopProductSkuKey shopProductSkuKey) {
        if (LimitType.UNLIMITED == limitType) {
            return;
        }
        if (limitNum <= 0) {
            throw OrderError.GOODS_OUT_OF_LIMIT.dataEx(shopProductSkuKey);
        }
        long boughtCount = 0;
        int currentCount = 0;
        if (LimitType.PRODUCT_LIMITED == limitType) {
            boughtCount = TenantShop.disable(() -> this.shopOrderItemService.getProductBoughtNumCount(userId, shopProductSkuKey.toShopProductKey()));
            currentCount = productCount;
        } else if (LimitType.SKU_LIMITED == limitType) {
            boughtCount = TenantShop.disable(() -> this.shopOrderItemService.getProductSkuBoughtNumCount(userId, shopProductSkuKey.toShopProductSkuKey()));
            currentCount = num;
        }
        if ((boughtCount + currentCount) > limitNum) {
            throw OrderError.GOODS_OUT_OF_LIMIT.dataEx(shopProductSkuKey);
        }
    }


    /**
     * 代销商品调整价格
     *
     * @param consignmentPriceSetting 商品代销信息
     * @param sku                     sku信息
     */
    private void adjustConsignmentPricing(ConsignmentPriceSettingDTO consignmentPriceSetting, StorageSku sku) {
        // 代销商品重新处理逻辑
        if (consignmentPriceSetting == null) {
            return;
        }
        ConsignmentPriceDTO consignmentPrice = consignmentPriceSetting.singlePrice(sku.getSalePrice());
        sku.setSalePrice(consignmentPrice.getSalePrice());
        sku.setPrice(consignmentPrice.getPrice());
    }

}
