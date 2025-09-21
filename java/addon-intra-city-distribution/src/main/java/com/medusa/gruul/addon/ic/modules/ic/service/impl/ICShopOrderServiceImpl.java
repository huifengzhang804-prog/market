package com.medusa.gruul.addon.ic.modules.ic.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.BooleanUtil;
import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson2.JSON;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.medusa.gruul.addon.ic.model.ICConstant;
import com.medusa.gruul.addon.ic.model.ICError;
import com.medusa.gruul.addon.ic.modules.ic.model.bo.ICCourier;
import com.medusa.gruul.addon.ic.modules.ic.model.bo.ICErrorHandler;
import com.medusa.gruul.addon.ic.modules.ic.model.bo.ICOpen;
import com.medusa.gruul.addon.ic.modules.ic.model.bo.ICOrderTimes;
import com.medusa.gruul.addon.ic.modules.ic.model.dto.ICErrorHandleDTO;
import com.medusa.gruul.addon.ic.modules.ic.model.dto.ICShopOrderPageDTO;
import com.medusa.gruul.addon.ic.modules.ic.model.enums.ICDeliveryType;
import com.medusa.gruul.addon.ic.modules.ic.model.vo.*;
import com.medusa.gruul.addon.ic.modules.ic.mp.dao.ICShopOrderDao;
import com.medusa.gruul.addon.ic.modules.ic.mp.entity.ICShopConfig;
import com.medusa.gruul.addon.ic.modules.ic.mp.entity.ICShopOrder;
import com.medusa.gruul.addon.ic.modules.ic.service.ICShopConfigService;
import com.medusa.gruul.addon.ic.modules.ic.service.ICShopOrderService;
import com.medusa.gruul.addon.ic.modules.opens.uupt.CoordinateTransform;
import com.medusa.gruul.addon.ic.modules.opens.uupt.UuptResponse;
import com.medusa.gruul.addon.ic.modules.opens.uupt.api.IUuptApiFactory;
import com.medusa.gruul.addon.ic.modules.opens.uupt.model.enums.OrderState;
import com.medusa.gruul.addon.ic.modules.opens.uupt.model.enums.PayTypeEnum;
import com.medusa.gruul.addon.ic.modules.opens.uupt.model.enums.SendType;
import com.medusa.gruul.addon.ic.modules.opens.uupt.model.enums.UrgentOrder;
import com.medusa.gruul.addon.ic.modules.opens.uupt.model.order.*;
import com.medusa.gruul.addon.ic.modules.opens.uupt.model.user.AccountParam;
import com.medusa.gruul.addon.ic.modules.opens.uupt.model.user.AccountResp;
import com.medusa.gruul.addon.ic.modules.uupt.util.UuptHelper;
import com.medusa.gruul.common.fastjson2.FastJson2;
import com.medusa.gruul.common.model.constant.CommonPool;
import com.medusa.gruul.common.model.resp.SystemCode;
import com.medusa.gruul.common.mp.model.BaseEntity;
import com.medusa.gruul.common.mp.model.SqlHelper;
import com.medusa.gruul.common.redis.annotation.Redisson;
import com.medusa.gruul.common.redis.util.RedisUtil;
import com.medusa.gruul.common.security.model.bean.SecureUser;
import com.medusa.gruul.common.security.model.enums.Roles;
import com.medusa.gruul.common.security.resource.helper.ISecurity;
import com.medusa.gruul.global.model.express.UserAddressDTO;
import com.medusa.gruul.order.api.enums.DeliverType;
import com.medusa.gruul.order.api.enums.ErrorHandlerStatus;
import com.medusa.gruul.order.api.enums.ICShopOrderStatus;
import com.medusa.gruul.order.api.model.ResetOrderStatusDTO;
import com.medusa.gruul.order.api.model.ic.ICStatus;
import com.medusa.gruul.order.api.rpc.OrderRpcService;
import com.medusa.gruul.service.uaa.api.rpc.UaaRpcService;
import com.medusa.gruul.service.uaa.api.vo.ShopUserInfoVO;
import com.medusa.gruul.shop.api.model.vo.ShopAddressVO;
import com.medusa.gruul.shop.api.rpc.ShopRpcService;
import com.vividsolutions.jts.geom.Point;
import io.vavr.Tuple;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;

/**
 * @author 张治保
 * @since 2024/8/27
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ICShopOrderServiceImpl implements ICShopOrderService {

    private final ICShopConfigService shopConfigService;
    private final ICShopOrderDao shopOrderDao;
    private final ShopRpcService shopRpcService;
    private final OrderRpcService orderRpcService;
    private final IUuptApiFactory uuptApiFactory;
    private final UaaRpcService uaaRpcService;

    /**
     * UU 跑腿的状态 转系统内的订单状态
     *
     * @param state UU 跑腿的状态
     * @return 系统内的订单状态
     */
    public static ICShopOrderStatus uuptStateToStatus(OrderState state) {
        return switch (state) {
            //异常单
            case CANCEL_1, CANCEL_2, CANCEL_3 -> ICShopOrderStatus.ERROR;
            case ORDER_CANCELLED, ORDERED -> ICShopOrderStatus.PENDING;
            case ORDER_ACCEPTED -> ICShopOrderStatus.TAKEN;
            case ARRIVED_FOR_PICK -> ICShopOrderStatus.ARRIVED_SHOP;
            case PICKED_UP -> ICShopOrderStatus.PICKUP;
            case ARRIVED, RECEIVED -> ICShopOrderStatus.DELIVERED;
        };
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void uuptCallback(OrderCallbackParam param) {
        String icNo = param.getOriginId();

        ICShopOrderStatus status = uuptStateToStatus(param.getState());
        log.debug("uupt \n回调订单信息：{}\n目标状态：{}", param, status);

        ICShopOrder order = shopOrderDao.lambdaQuery()
                .select(ICShopOrder::getShopId, ICShopOrder::getOrderNo, ICShopOrder::getCourier, ICShopOrder::getStatus)
                .eq(ICShopOrder::getType, ICDeliveryType.UUPT)
                .eq(ICShopOrder::getIcNo, icNo)
                .orderByDesc(BaseEntity::getCreateTime)
                .last(SqlHelper.SQL_LIMIT_1)
                .one();
        if (order == null) {
            log.error("uu跑腿回调未查询到配送单信息");
            return;
        }
        if (status == order.getStatus()) {
            //重复回调 不做处理
            log.debug("uu跑腿回调订单状态未变化：");
            return;
        }
        String orderNo = order.getOrderNo();
        //加锁执行
        RedisUtil.lockRun(
                RedisUtil.getLockKey(ICConstant.SHOP_ORDER_TAKE_LOCK_KEY, orderNo),
                () -> {
                    ICCourier courier = order.getCourier();
                    //是否需要更新配送员信息
                    String driverMobile = param.getDriverMobile();
                    boolean updateCourier = StrUtil.isNotBlank(driverMobile) && (courier == null || !driverMobile.equals(courier.getMobile()));
                    shopOrderDao.lambdaUpdate()
                            .set(ICShopOrder::getStatus, status)
                            .setSql(updateTimeSql(status, LocalDateTime.now()))
                            .set(updateCourier, ICShopOrder::getCourier, JSON.toJSONString(
                                    new ICCourier()
                                            .setName(param.getDriverName())
                                            .setMobile(driverMobile)
                                            .setAvatar(param.getDriverPhoto())
                            ))
                            //更新开放平台描述信息
                            .setSql(
                                    SqlHelper.renderJsonSetSql(
                                            "`open`",
                                            Tuple.of("uuptDesc", param.getStateText())
                                    )
                            )
                            .eq(ICShopOrder::getType, ICDeliveryType.UUPT)
                            .eq(ICShopOrder::getIcNo, icNo)
                            .orderByDesc(BaseEntity::getCreateTime)
                            .last(SqlHelper.SQL_LIMIT_1)
                            .update();
                    //如果是已送达 则订单状态更新为已收货 待评价
                    if (ICShopOrderStatus.DELIVERED == status) {
                        //把订单状态更新确认收货 待评价
                        orderRpcService.icSendDelayConfirmPackage(orderNo, order.getShopId());
                    }
                }
        );

    }

    @Override
    public IPage<ICShopOrder> page(SecureUser<?> secureUser, ICShopOrderPageDTO param) {
        ICShopOrderStatus status = param.getStatus();
        Set<String> icNos = param.getIcNos();
        ICShopOrderPageDTO page = shopOrderDao.lambdaQuery()
                .eq(ICShopOrder::getShopId, secureUser.getShopId())
                .eq(ICShopOrderStatus.TAKEN != status, ICShopOrder::getStatus, status)
                .in(ICShopOrderStatus.TAKEN == status, ICShopOrder::getStatus, ICShopOrderStatus.TAKEN, ICShopOrderStatus.ARRIVED_SHOP)
                .eq(ICShopOrderStatus.PENDING != status, ICShopOrder::getClerkUserId, secureUser.getId())
                .eq(ICShopOrder::getType, ICDeliveryType.SELF)
                .in(CollUtil.isNotEmpty(icNos), ICShopOrder::getIcNo, icNos)
                .ne(ICShopOrder::getStatus, ICShopOrderStatus.ERROR)
                .page(param);
        LocalDateTime now = LocalDateTime.now();
        page.getRecords()
                .forEach(
                        order -> {
                            ICOrderTimes times = order.getTimes();
                            LocalDateTime takeOrderTime = times.getTakeOrderTime();
                            // 没有取件时间不计算配送时间
                            if (takeOrderTime == null) {
                                return;
                            }
                            LocalDateTime endTime = switch (status) {
                                case DELIVERED -> times.getDeliveredTime();
                                case ERROR -> times.getErrorTime();
                                default -> now;
                            };
                            order.setDeliverSeconds(
                                    Duration.between(takeOrderTime, endTime).toSeconds()
                            );
                        }
                );
        return page;
    }

    @Override
    @Redisson(name = ICConstant.SHOP_ORDER_TAKE_LOCK_KEY, key = "#orderNo")
    public void takeOrder(SecureUser<?> secureUser, String orderNo) {
        Long shopId = secureUser.getShopId();
        ICShopOrder order = shopOrderDao.lambdaQuery()
                .select(ICShopOrder::getStatus)
                .eq(ICShopOrder::getType, ICDeliveryType.SELF)
                .eq(ICShopOrder::getOrderNo, orderNo)
                .eq(ICShopOrder::getShopId, shopId)
                .orderByDesc(BaseEntity::getCreateTime)
                .last(SqlHelper.SQL_LIMIT_1)
                .one();
        if (order == null) {
            throw SystemCode.DATA_NOT_EXIST.exception();
        }
        if (ICShopOrderStatus.ERROR == order.getStatus()) {
            throw ICError.IC_ORDER_ERROR.exception();
        }
        if (ICShopOrderStatus.PENDING != order.getStatus()) {
            throw ICError.ORDER_TAKEN.exception();
        }
        Long userId = secureUser.getId();
        //获取配送员信息
        ShopUserInfoVO shopUser;
        if (ISecurity.anyRole(Roles.ADMIN)) {
            shopUser = new ShopUserInfoVO().setNickname("管理员").setMobile(secureUser.getMobile());
        } else {
            shopUser = uaaRpcService.getShopUserDataByShopIdUserId(shopId, userId)
                    .getOrElse(() -> new ShopUserInfoVO().setNickname("").setMobile(secureUser.getMobile()));
        }
        shopOrderDao.lambdaUpdate()
                //状态更新未已接单
                .set(ICShopOrder::getStatus, ICShopOrderStatus.TAKEN)
                .set(ICShopOrder::getClerkUserId, userId)
                .setSql(updateTimeSql(ICShopOrderStatus.TAKEN, LocalDateTime.now()))
                .set(ICShopOrder::getCourier,
                        JSON.toJSONString(
                                new ICCourier()
                                        .setName(shopUser.getNickname())
                                        .setMobile(shopUser.getMobile())
                        )
                )
                .eq(ICShopOrder::getType, ICDeliveryType.SELF)
                .eq(ICShopOrder::getOrderNo, orderNo)
                .eq(ICShopOrder::getShopId, shopId)
                .eq(ICShopOrder::getStatus, ICShopOrderStatus.PENDING)
                .orderByDesc(BaseEntity::getCreateTime)
                .last(SqlHelper.SQL_LIMIT_1)
                .update();
    }

    @Override
    @Redisson(name = ICConstant.SHOP_ORDER_TAKE_LOCK_KEY, key = "#orderNo")
    public void offerOrder(SecureUser<?> secureUser, String orderNo) {
        ICShopOrder order = shopOrderDao.lambdaQuery()
                .select(ICShopOrder::getType, ICShopOrder::getStatus, ICShopOrder::getTimes)
                .eq(ICShopOrder::getClerkUserId, secureUser.getId())
                .eq(ICShopOrder::getOrderNo, orderNo)
                .eq(ICShopOrder::getShopId, secureUser.getShopId())
                .eq(ICShopOrder::getStatus, ICShopOrderStatus.TAKEN)
                .eq(ICShopOrder::getType, ICDeliveryType.SELF)
                .orderByDesc(BaseEntity::getCreateTime)
                .last(SqlHelper.SQL_LIMIT_1)
                .one();
        //如果不存在说明已不可取消
        if (order == null) {
            throw ICError.ORDER_CANNOT_OFFER.exception();
        }
        ICOrderTimes times = order.getTimes();
        shopOrderDao.lambdaUpdate()
                //状态更新未已接单
                .set(ICShopOrder::getStatus, ICShopOrderStatus.PENDING)
                .set(ICShopOrder::getClerkUserId, null)
                .set(
                        ICShopOrder::getTimes,
                        JSON.toJSONString(
                                new ICOrderTimes()
                                        .setShippingTime(times.getShippingTime())
                        )
                )
                .eq(ICShopOrder::getType, ICDeliveryType.SELF)
                .eq(ICShopOrder::getClerkUserId, secureUser.getId())
                .eq(ICShopOrder::getOrderNo, orderNo)
                .eq(ICShopOrder::getShopId, secureUser.getShopId())
                .eq(ICShopOrder::getStatus, ICShopOrderStatus.TAKEN)
                .update();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    @Redisson(name = ICConstant.SHOP_ORDER_TAKE_LOCK_KEY, key = "#orderNo")
    public void orderStatusNext(SecureUser<?> secureUser, String orderNo) {
        Long shopId = secureUser.getShopId();
        ICShopOrder order = shopOrderDao.lambdaQuery()
                .select(BaseEntity::getId, ICShopOrder::getStatus, ICShopOrder::getTimes)
                .eq(ICShopOrder::getType, ICDeliveryType.SELF)
                .eq(ICShopOrder::getClerkUserId, secureUser.getId())
                .eq(ICShopOrder::getOrderNo, orderNo)
                .eq(ICShopOrder::getShopId, shopId)
                .orderByDesc(BaseEntity::getCreateTime)
                .last(SqlHelper.SQL_LIMIT_1)
                .one();
        if (order == null) {
            throw ICError.ORDER_STATUS_CANNOT_PROCESS.exception();
        }
        ICShopOrderStatus status = order.getStatus();
        //目标状态
        ICShopOrderStatus targetStatus = switch (status) {
            case TAKEN -> ICShopOrderStatus.ARRIVED_SHOP;
            case ARRIVED_SHOP -> ICShopOrderStatus.PICKUP;
            case PICKUP -> ICShopOrderStatus.DELIVERED;
            default -> throw ICError.ORDER_STATUS_CANNOT_PROCESS.exception();
        };
        shopOrderDao.lambdaUpdate()
                .set(ICShopOrder::getStatus, targetStatus)
                .setSql(updateTimeSql(targetStatus, LocalDateTime.now()))
                .eq(ICShopOrder::getType, ICDeliveryType.SELF)
                .eq(ICShopOrder::getClerkUserId, secureUser.getId())
                .eq(ICShopOrder::getOrderNo, orderNo)
                .eq(ICShopOrder::getShopId, shopId)
                .orderByDesc(BaseEntity::getCreateTime)
                .last(SqlHelper.SQL_LIMIT_1)
                .update();
        //如果是已送达 订单更新为确认收货 并发送 延迟任务 确认收货
        if (ICShopOrderStatus.DELIVERED == targetStatus) {
            orderRpcService.icSendDelayConfirmPackage(orderNo, shopId);
        }
    }

    private String updateTimeSql(ICShopOrderStatus status, LocalDateTime time) {
        String timeFiled = status.getTimeFiled();
        if (StrUtil.isEmpty(timeFiled)) {
            return timeFiled;
        }
        //如果是待取货状态 则重置所有时间
        if (status == ICShopOrderStatus.PENDING) {
            return "times = '{\"+" + timeFiled + "+\":\"" + time.format(FastJson2.DATETIME_FORMATTER) + "\"}'";
        }
        return SqlHelper.renderJsonSetSql(
                "times",
                Tuple.of(
                        timeFiled,
                        time.format(FastJson2.DATETIME_FORMATTER)
                )
        );
    }

    private String updateErrorHandleTime(LocalDateTime time) {
        return SqlHelper.renderJsonSetSql(
                "times",
                Tuple.of("errorHandleTime", time.format(FastJson2.DATETIME_FORMATTER))
        );
    }

    @Override
    public List<DeliverType> deliverType(Long shopId) {
        ICShopConfig config = shopConfigService.config(shopId);
        if (config == null) {
            throw ICError.IC_UNACTIVATED.dataEx(shopId);
        }
        List<DeliverType> result = new ArrayList<>(2);
        boolean self = BooleanUtil.isTrue(config.getEnableSelf());
        if (self) {
            result.add(DeliverType.IC_MERCHANT);
        }
        boolean open = BooleanUtil.isTrue(config.getEnableOpen());
        if (open) {
            result.add(DeliverType.IC_OPEN);
        }
        //同时启用涉及排序问题 默认的配送方排在前边
        if (self && open) {
            ICDeliveryType defaultType = config.getDefaultType();
            if (ICDeliveryType.UUPT == defaultType) {
                return List.of(DeliverType.IC_OPEN, DeliverType.IC_MERCHANT);
            }
        }
        return result;
    }

    @Override
    @Redisson(name = ICConstant.SHOP_ORDER_TAKE_LOCK_KEY, batchParamName = "#orderNos", key = "#item")
    public DeliverPriceVO deliverPrice(Long shopId, Set<String> orderNos) {
        ICShopConfig config = shopConfigService.config(shopId);
        if (!config.getEnableOpen()) {
            throw ICError.UUPT_UNACTIVATED.exception();
        }
        String openid = UuptHelper.getOpenid(shopId);
        if (StrUtil.isEmpty(openid)) {
            throw ICError.UUPT_UNACTIVATED.exception();
        }
        //店铺地址
        ShopAddressVO shopAddress = shopRpcService.shopAddress(Set.of(shopId)).get(shopId);
        String fromAddress = shopAddress.fullAddress();
        //判断当前店铺城市是否开通
        if (!UuptHelper.cityIsOpen(shopAddress.city(), shopAddress.county(), uuptApiFactory)) {
            throw ICError.UUPT_CITY_UNACTIVATED.exception();
        }
        //店铺经纬度
        Point location = shopAddress.getLocation();
        double[] coordinate = CoordinateTransform.gcj02ToBD09(location.getX(), location.getY());
        BigDecimal fromLng = BigDecimal.valueOf(coordinate[0]);
        BigDecimal fromLat = BigDecimal.valueOf(coordinate[1]);
        //批量查询当前订单已存在的配送单记录
        List<ICShopOrder> shopOrderRecords = shopOrderDao.lambdaQuery()
                .select(ICShopOrder::getOrderNo, ICShopOrder::getStatus, BaseEntity::getCreateTime)
                .eq(ICShopOrder::getShopId, shopId)
                .in(ICShopOrder::getOrderNo, orderNos)
                //按创建时间倒叙排序
                .orderByDesc(BaseEntity::getCreateTime)
                .list();
        Map<String, ICShopOrder> orderLastDeliverMap = new HashMap<>(orderNos.size());
        for (ICShopOrder record : shopOrderRecords) {
            String orderNo = record.getOrderNo();
            //按创建时间倒叙排序 如果已经存在 说明当前数据不是最新的那条数据
            if (orderLastDeliverMap.containsKey(orderNo)) {
                continue;
            }
            //当前数据是最新的那条数据
            orderLastDeliverMap.put(orderNo, record);
        }
        //订单和运费对应 map
        Map<String, DeliverItemPriceVO> orderPriceMap = new HashMap<>(orderNos.size());
        //保存需要新增的店铺发货单信息 
        List<ICShopOrder> newShopOrders = new ArrayList<>(orderNos.size());
        //查询订单用户收货地址
        Map<String, UserAddressDTO> userAddressMap = orderRpcService.orderReceiverAddress(shopId, orderNos);

        for (String orderNo : orderNos) {
            //收货人地址 与经纬度
            UserAddressDTO receiver = userAddressMap.get(orderNo);
            if (receiver == null) {
                throw ICError.USER_ADDRESS_ISNULL.dataEx(orderNo);
            }

            //用户收货地址
            Point userLocation = receiver.getLocation();
            //用户定位 从gcj02 转成百度坐标系
            double[] userCoordinate = CoordinateTransform.gcj02ToBD09(userLocation.getX(), userLocation.getY());
            BigDecimal toLng = BigDecimal.valueOf(userCoordinate[0]);
            BigDecimal toLat = BigDecimal.valueOf(userCoordinate[1]);

            //生成新的配送单号
            String icNo = UuptHelper.icNo();
            //获取 UU 跑腿运单价格
            UuptResponse<OrderPriceResp> orderPriceResp = uuptApiFactory.order()
                    .orderPrice(
                            openid,
                            new OrderPriceParam()
                                    .setOriginId(icNo)
                                    .setUrgentOrder(UrgentOrder.NORMAL)
                                    .setSendType(SendType.SEND)
                                    .setFromAddress(fromAddress)
                                    .setFromLat(fromLat)
                                    .setFromLng(fromLng)
                                    .setToAddress(receiver.fullAddress())
                                    .setToLat(toLat)
                                    .setToLng(toLng)
                                    .setCityName(shopAddress.city())
                                    .setCountyName(shopAddress.county())
                    );
            if (!orderPriceResp.isSuccess()) {
                throw ICError.UUPT_RESP_ERROR.dataEx(orderPriceResp);
            }
            //已存在的订单
            ICShopOrder deliverOrder = orderLastDeliverMap.get(orderNo);
            OrderPriceResp orderPrice = orderPriceResp.getBody();
            DeliverItemPriceVO uuptPrice = new DeliverItemPriceVO()
                    .setTotal(UuptHelper.fenToHao(orderPrice.getTotalMoney()))
                    .setDiscount(UuptHelper.fenToHao(orderPrice.getTotalPriceOff()))
                    .setPay(UuptHelper.fenToHao(orderPrice.getNeedPayMoney()));

            orderPriceMap.put(orderNo, uuptPrice);
            //uupt 返回的重要数据
            ICOpen icOpen = new ICOpen()
                    .setUuptPriceToken(orderPrice.getPriceToken())
                    .setPriceTokenExpireTime(LocalDateTime.now().plusSeconds(orderPrice.getExpiresIn()))
                    .setUuptPrice(uuptPrice);
            if (deliverOrder != null) {
                // 存在的配送单 分为几种情况
                // 1. 订单仍处于PRICE_PADDING状态 可以直接更新掉
                // 2. 订单异常结束 新增
                // 3. 订单已送达 无法继续处理
                // 4. 订单处于流转状态 无法处理提示处于流转中
                ICShopOrderStatus status = deliverOrder.getStatus();
                //如果是价格计算后的状态则直接更新
                if (ICShopOrderStatus.PRICE_PADDING == status) {
                    shopOrderDao.lambdaUpdate()
                            .set(ICShopOrder::getReceiver, JSON.toJSONString(receiver))
                            .set(ICShopOrder::getIcNo, icNo)
                            .set(ICShopOrder::getOpen, JSON.toJSONString(icOpen))
                            .eq(ICShopOrder::getShopId, shopId)
                            .eq(ICShopOrder::getOrderNo, orderNo)
                            .orderByDesc(BaseEntity::getCreateTime)
                            .last(SqlHelper.SQL_LIMIT_1)
                            .update();
                    continue;
                }
                //如果是异常状态 则表示此次是才重新处理 直接新增即可
                if (ICShopOrderStatus.ERROR != status) {
                    //不是异常状态 已完成 抛出已完成异常 否则 提示订单已处理配送流程中
                    throw (
                            ICShopOrderStatus.DELIVERED == status
                                    ? ICError.IC_ORDER_FINISHED
                                    : ICError.IC_ORDER_PROCESSING
                    ).dataEx(orderNo);
                }
            }
            newShopOrders.add(
                    new ICShopOrder()
                            .setShopId(shopId)
                            .setType(ICDeliveryType.UUPT)
                            .setIcNo(icNo)
                            .setOrderNo(orderNo)
                            .setStatus(ICShopOrderStatus.PRICE_PADDING)
                            .setTimes(new ICOrderTimes())
                            .setReceiver(receiver)
                            .setSkus(List.of())
                            .setOpen(icOpen)
            );
        }
        //查询当前账户余额
        UuptResponse<AccountResp> balanceResp = uuptApiFactory.user()
                .account(openid, new AccountParam().setPayTypeEnum(PayTypeEnum.BALANCE_PAY));
        if (!balanceResp.isSuccess()) {
            throw ICError.UUPT_RESP_ERROR.dataEx(balanceResp);
        }
        if (CollUtil.isNotEmpty(newShopOrders)) {
            shopOrderDao.saveBatch(newShopOrders);
        }
        return new DeliverPriceVO()
                .setTotalPrice(orderPriceMap.values().stream().mapToLong(DeliverItemPriceVO::getPay).sum())
                .setOrderPriceMap(orderPriceMap)
                .setBalance(UuptHelper.fenToHao(balanceResp.getBody().getBalance()));
    }

    @Override
    public DeliveryInfoVO deliverInfo(boolean isUser, Long shopId, String orderNo) {
        List<ICShopOrder> deliverOrders = shopOrderDao.lambdaQuery()
                .select(
                        ICShopOrder::getStatus, ICShopOrder::getType,
                        ICShopOrder::getTimes, ICShopOrder::getRemark,
                        ICShopOrder::getCourier, ICShopOrder::getErrorHandler,
                        ICShopOrder::getReceiver, ICShopOrder::getPickupCode,
                        ICShopOrder::getOpen
                )
                .eq(ICShopOrder::getShopId, shopId)
                .eq(ICShopOrder::getOrderNo, orderNo)
                .ne(ICShopOrder::getStatus, ICShopOrderStatus.PRICE_PADDING)
                .orderByDesc(BaseEntity::getCreateTime)
                .last(isUser, SqlHelper.SQL_LIMIT_1)
                .list();
        if (CollUtil.isEmpty(deliverOrders)) {
            throw SystemCode.DATA_NOT_EXIST.exception();
        }
        LocalDateTime now = LocalDateTime.now();
        DeliveryInfoVO deliveryInfo = new DeliveryInfoVO()
                .setOrders(
                        deliverOrders.stream()
                                .map(
                                        order -> {
                                            ICOrderTimes times = order.getTimes();
                                            ICShopOrderStatus status = order.getStatus();
                                            ShopOrderInfo shopOrderInfo = new ShopOrderInfo()
                                                    .setStatus(status)
                                                    .setType(order.getType())
                                                    .setTimes(times)
                                                    .setRemark(order.getRemark())
                                                    .setCourier(order.getCourier())
                                                    .setErrorHandler(order.getErrorHandler())
                                                    .setPickupCode(order.getPickupCode());
                                            if (ICDeliveryType.UUPT == order.getType()) {
                                                ICOpen open = order.getOpen();
                                                shopOrderInfo.setStatusDesc(open == null ? null : open.getUuptDesc());
                                            }
                                            LocalDateTime takeOrderTime = times.getTakeOrderTime();
                                            if (takeOrderTime != null) {
                                                LocalDateTime endTime = switch (status) {
                                                    case DELIVERED -> times.getDeliveredTime();
                                                    case ERROR -> times.getErrorTime();
                                                    default -> now;
                                                };
                                                shopOrderInfo.setDeliverSeconds(
                                                        Duration.between(takeOrderTime, endTime)
                                                                .toSeconds()
                                                );
                                            }
                                            return shopOrderInfo;
                                        }

                                ).toList()
                );
        //获取最新的一个订单
        ICShopOrder order = deliverOrders.get(CommonPool.NUMBER_ZERO);
        return deliveryInfo.setReceiverLocation(order.getReceiver().getLocation());
    }

    @Override
    @Redisson(name = ICConstant.SHOP_ORDER_TAKE_LOCK_KEY, key = "#orderNo")
    public CourierVO courier(Long shopId, String orderNo) {
        String openid = UuptHelper.getOpenid(shopId);
        ICShopOrder order = shopOrderDao.lambdaQuery()
                .select(ICShopOrder::getStatus, ICShopOrder::getIcNo, ICShopOrder::getCourier)
                .eq(ICShopOrder::getOrderNo, orderNo)
                .eq(ICShopOrder::getType, ICDeliveryType.UUPT)
                .orderByDesc(BaseEntity::getCreateTime)
                .last(SqlHelper.SQL_LIMIT_1)
                .one();
        //不存在 
        // 或 不处于进行中（不处于进行中的订单 不需要查询配送员位置）
        // 或 配送方不是 UU 跑腿
        if (order == null) {
            throw SystemCode.DATA_NOT_EXIST.exception();
        }
        ICShopOrderStatus status = order.getStatus();
        //配送单已完结 提示已完结 不需要查询配送员位置
        if (ICShopOrderStatus.DELIVERED == status) {
            throw ICError.IC_ORDER_FINISHED.exception();
        }
        //如果未发货、未接单 则提示 接单后才能查询
        if (ICShopOrderStatus.PRICE_PADDING == status || ICShopOrderStatus.PENDING == status) {
            throw ICError.IC_ORDER_NOT_PICKUP.exception();
        }
        //如果配送单异常则提示
        if (ICShopOrderStatus.ERROR == status) {
            throw ICError.IC_ORDER_ERROR.exception();
        }
        String icNo = order.getIcNo();
        //跑男定位 缓存起来
        // UU 跑腿 订单信息 每 30 秒刷新一次，额外调用没有意义
        return RedisUtil.getCacheMap(
                CourierVO.class,
                () -> {
                    //其它状态可正常查询
                    UuptResponse<OrderDetailResp> response = uuptApiFactory.order()
                            .orderDetail(openid, new OrderKey().setOriginId(icNo));
                    if (!response.isSuccess()) {
                        throw ICError.UUPT_RESP_ERROR.dataEx(response);
                    }
                    OrderDetailResp orderDetail = response.getBody();

                    //当前订单目标状态
                    OrderState state = orderDetail.getState();
                    ICShopOrderStatus targetStatus = uuptStateToStatus(state);
                    //订单配送员信息
                    ICCourier courier = order.getCourier();
                    if (courier == null) {
                        courier = new ICCourier();
                    }
                    //todo UU 跑腿沙箱环境 不会更新订单状态
                    //todo 所以 如果是测试环境 则自己mock模拟数据 
                    if (uuptApiFactory.config().isTest()) {
                        //目的地经纬度
                        return new CourierVO()
                                .setCourier(courier)
                                .setDistance(CommonPool.UNIT_CONVERSION_HUNDRED)
                                .setLocation(
                                        //出发点和目的地中心点 作为配送员位置
                                        CourierVO.center(
                                                CourierVO.ofBd09(orderDetail.getToLocation()),
                                                CourierVO.ofBd09(orderDetail.getFromLocation())
                                        )
                                )
                                .setExpectTime(LocalDateTime.now().plusMinutes(CommonPool.NUMBER_FIVE));
                    }
                    //如果目标状态和当前订单的状态不一致 则更新订单信息
                    if (targetStatus != status) {
                        //如果不是同一个配送员
                        String driverMobile = orderDetail.getDriverMobile();
                        //通过手机号判断
                        boolean updateDriver = StrUtil.isNotBlank(driverMobile) && !driverMobile.equals(courier.getMobile());
                        if (updateDriver) {
                            courier.setName(orderDetail.getDriverName())
                                    .setMobile(orderDetail.getDriverMobile());
                        }
                        shopOrderDao.lambdaUpdate()
                                .set(ICShopOrder::getStatus, targetStatus)
                                .setSql(updateTimeSql(targetStatus, LocalDateTime.now()))
                                .set(updateDriver, ICShopOrder::getCourier, JSON.toJSONString(courier))
                                //更新开放平台描述信息
                                .setSql(SqlHelper.renderJsonSetSql("`open`", Tuple.of("uuptDesc", state.getDesc())))
                                .eq(ICShopOrder::getOrderNo, orderNo)
                                .orderByDesc(BaseEntity::getCreateTime)
                                .last(SqlHelper.SQL_LIMIT_1)
                                .update();
                    }
                    return new CourierVO()
                            .setCourier(courier)
                            .setDistance(orderDetail.getDistance())
                            .setLocation(CourierVO.ofBd09(orderDetail.getDriverLastLoc()))
                            .setExpectTime(orderDetail.getExpectedArriveTime());
                },
                // 15秒过期 uupt 订单详情接口 30 秒内信息不更新，30 秒外才会变化
                Duration.ofSeconds(15),
                ICConstant.SHOP_ORDER_COURIER_KEY,
                shopId,
                orderNo
        );
    }

    @Override
    public Map<String, ICStatus> icOrderStatus(Set<String> orderNos, boolean handledErrorToPending) {
        if (CollUtil.isEmpty(orderNos)) {
            return Map.of();
        }
        Map<String, ICStatus> result = new HashMap<>(orderNos.size());
        List<ICShopOrder> orders = shopOrderDao.query()
                .select("order_no", "`status`", "`open`->> '$.uuptDesc' AS statusDesc", "error_handler IS NOT NULL AS errorHandled")
                .ne("`status`", ICShopOrderStatus.PRICE_PADDING.getValue())
                .in("order_no", orderNos)
                .orderByDesc("create_time")
                .list();
        for (ICShopOrder order : orders) {
            String orderNo = order.getOrderNo();
            if (result.containsKey(orderNo)) {
                continue;
            }
            ICShopOrderStatus status = order.getStatus();
            if (handledErrorToPending && ICShopOrderStatus.ERROR == status && BooleanUtil.isTrue(order.getErrorHandled())) {
                status = ICShopOrderStatus.PRICE_PADDING;
            }
            ICStatus icStatus = new ICStatus()
                    .setStatus(status)
                    .setStatusDesc(order.getStatusDesc());
            result.put(orderNo, icStatus);
        }
        return result;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    @Redisson(name = ICConstant.SHOP_ORDER_TAKE_LOCK_KEY, key = "#orderNo")
    public void errorHandle(Long shopId, ICErrorHandleDTO param) {
        String orderNo = param.getOrderNo();
        ICShopOrder order = shopOrderDao.lambdaQuery()
                .select(ICShopOrder::getStatus, ICShopOrder::getErrorHandler)
                .eq(ICShopOrder::getShopId, shopId)
                .eq(ICShopOrder::getOrderNo, orderNo)
                .orderByDesc(BaseEntity::getCreateTime)
                .last(SqlHelper.SQL_LIMIT_1)
                .one();
        if (order == null) {
            throw SystemCode.DATA_NOT_EXIST.exception();
        }
        ICShopOrderStatus orderStatus = order.getStatus();
        if (ICShopOrderStatus.ERROR != orderStatus) {
            throw SystemCode.DATA_NOT_EXIST.exception();
        }
        if (order.getErrorHandler() != null) {
            throw SystemCode.DATA_EXISTED.exception();
        }
        ErrorHandlerStatus handlerStatus = param.getStatus();
        boolean isDelivered = ErrorHandlerStatus.DELIVERED == handlerStatus;
        LocalDateTime now = LocalDateTime.now();
        boolean success = shopOrderDao.lambdaUpdate()
                //更新错误处理信息
                .set(ICShopOrder::getErrorHandler, JSON.toJSONString(
                        new ICErrorHandler()
                                .setStatus(handlerStatus)
                                .setDesc(param.getDesc())
                ))
                //如果处理状态为已送达 则更新订单状态为已送达
                .set(isDelivered, ICShopOrder::getStatus, ICShopOrderStatus.DELIVERED)
                .setSql(updateErrorHandleTime(now))
                .setSql(isDelivered, updateTimeSql(ICShopOrderStatus.DELIVERED, now))
                .eq(ICShopOrder::getShopId, shopId)
                .eq(ICShopOrder::getOrderNo, orderNo)
                .orderByDesc(BaseEntity::getCreateTime)
                .last(SqlHelper.SQL_LIMIT_1)
                .update();
        SystemCode.DATA_NOT_EXIST.falseThrow(success);
        if (isDelivered) {
            //已送达 发送延迟确认收货 消息
            orderRpcService.icSendDelayConfirmPackage(orderNo, shopId);
            return;
        }
        //非送达状态 则重置订单状态
        //1. 重置为代发货状态
        //2. 重置为订单关闭且退款状态
        orderRpcService.resetOrderStatus(
                new ResetOrderStatusDTO()
                        .setOrderNo(orderNo)
                        .setShopId(shopId)
                        .setStatus(handlerStatus)
        );
    }
}
