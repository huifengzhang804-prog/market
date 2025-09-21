package com.medusa.gruul.addon.integral.service.impl;

import cn.hutool.core.util.BooleanUtil;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.StrUtil;
import com.medusa.gruul.addon.integral.model.bo.IntegralOrderDetailInfoBO;
import com.medusa.gruul.addon.integral.model.constant.IntegralConstant;
import com.medusa.gruul.addon.integral.model.dto.IntegralOrderDTO;
import com.medusa.gruul.addon.integral.model.dto.IntegralReceiverDTO;
import com.medusa.gruul.addon.integral.model.enums.IntegralOrderRabbit;
import com.medusa.gruul.addon.integral.model.enums.IntegralOrderSourceType;
import com.medusa.gruul.addon.integral.model.enums.IntegralOrderStatus;
import com.medusa.gruul.addon.integral.model.enums.IntegralProductStatus;
import com.medusa.gruul.addon.integral.mp.entity.IntegralOrder;
import com.medusa.gruul.addon.integral.mp.entity.IntegralOrderReceiver;
import com.medusa.gruul.addon.integral.mp.entity.IntegralProduct;
import com.medusa.gruul.addon.integral.mp.service.IIntegralOrderPaymentService;
import com.medusa.gruul.addon.integral.mp.service.IIntegralOrderReceiverService;
import com.medusa.gruul.addon.integral.mp.service.IIntegralOrderService;
import com.medusa.gruul.addon.integral.mp.service.IIntegralProductService;
import com.medusa.gruul.addon.integral.properties.IntegralOrderProperties;
import com.medusa.gruul.addon.integral.service.CommonIntegralProductService;
import com.medusa.gruul.addon.integral.service.CreateIntegralOrderService;
import com.medusa.gruul.addon.integral.util.IntegralOrderUtil;
import com.medusa.gruul.addon.integral.util.IntegralUtil;
import com.medusa.gruul.common.fastjson2.FastJson2;
import com.medusa.gruul.common.model.constant.CommonPool;
import com.medusa.gruul.common.model.enums.ChangeType;
import com.medusa.gruul.common.model.message.DataChangeMessage;
import com.medusa.gruul.common.model.resp.SystemCode;
import com.medusa.gruul.common.redis.util.RedisUtil;
import com.medusa.gruul.common.security.model.bean.SecureUser;
import com.medusa.gruul.common.security.resource.helper.ISecurity;
import com.medusa.gruul.common.system.model.ISystem;
import com.medusa.gruul.common.system.model.model.Platform;
import com.medusa.gruul.global.model.exception.GlobalException;
import com.medusa.gruul.order.api.entity.OrderTimeout;
import com.medusa.gruul.service.uaa.api.rpc.UaaRpcService;
import com.medusa.gruul.service.uaa.api.vo.UserInfoVO;
import com.medusa.gruul.user.api.enums.UserRabbit;
import com.medusa.gruul.user.api.enums.integral.GainIntegralType;
import com.medusa.gruul.user.api.model.dto.integral.IntegralChangeDTO;
import com.medusa.gruul.user.api.rpc.UserRpcService;
import io.vavr.control.Try;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executor;

/**
 * 创建积分商城订单
 *
 * @author shishuqian
 * date 2023/2/1
 * time 15:45
 **/
@Slf4j
@Service
@RequiredArgsConstructor
public class CreateIntegralOrderServiceImpl implements CreateIntegralOrderService {

    private final CommonIntegralProductService commonIntegralProductService;
    private final UaaRpcService uaaRpcService;
    private final UserRpcService userRpcService;
    private final Executor integralExecutor;
    private final IIntegralProductService integralProductService;
    private final RabbitTemplate rabbitTemplate;
    private final IIntegralOrderService iIntegralOrderService;
    private final IIntegralOrderReceiverService iIntegralOrderReceiverService;
    private final IIntegralOrderPaymentService iIntegralOrderPaymentService;
    private final RedissonClient redissonClient;
    private final IntegralOrderProperties orderTimeOutProperties;

    @Override
    @SneakyThrows
    @Transactional(rollbackFor = Exception.class)
    public String create(IntegralOrderDTO integralOrderDTO, SecureUser<?> secureUser) {
        //用户id
        Long buyerId = secureUser.getId();
        //获取订单的基本信息
        IntegralOrder integralOrder = this.getOrder(buyerId, integralOrderDTO);
        //重置收货人信息
        IntegralOrderReceiver receiver = integralOrder.getIntegralOrderReceiver();
        this.resetEmptyOrderReceiver(secureUser.getMobile(), integralOrder.getBuyerNickname(), receiver);
        //执行扣库存等必要处理流程
        Try.run(() -> this.tryLockStock(integralOrder, integralOrderDTO))
                .onFailure(
                        exception -> {
                            //创建失败，删除订单缓存
                            IntegralOrderUtil.deleteIntegralOrderCache(buyerId, integralOrder.getNo());
                        }
                )
                .get();

        return integralOrder.getNo();
    }


    /**
     * 重置收货人资料 如果收货人对应信息为空 则使用当前登录的用户资料
     *
     * @param loginUserMobile 登录用户手机号
     * @param buyerNickname   买家昵称
     * @param orderReceiver   当前订单收货地址信息
     */
    private void resetEmptyOrderReceiver(String loginUserMobile, String buyerNickname, IntegralOrderReceiver orderReceiver) {
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


    @Override
    public IntegralOrder getOrder(Long userId, IntegralOrderDTO integralOrderDTO) {

        IntegralOrder integralOrder = new IntegralOrder();
        Platform platform = ISystem.platformMust();
        //订单号 = JF23 + 15位随机数
        //雪花算法生成id的后15位
        String snowflakeId = IdUtil.getSnowflakeNextIdStr();
        String id = snowflakeId.substring(snowflakeId.length() - 15);
        //订单号
        String orderNo = IntegralConstant.INTEGRAL_ORDER_NO_PREFIX + id;

        //积分订单来源
        IntegralOrderSourceType source = integralOrderDTO.getSource();

        integralOrder.setSource(source)
                .setNo(orderNo)
                .setBuyerId(userId)
                .setBuyerRemark(integralOrderDTO.getBuyerRemark())
                .setStatus(IntegralOrderStatus.UNPAID)
                .setProductId(integralOrderDTO.getProductId())
                .setNum(integralOrderDTO.getNum())
                .setAffiliationPlatform(platform);

        try {
            //多线程异步设置买家收获地址，买家信息，商品信息
            CompletableFuture.allOf(
                    this.assembleTasks(integralOrder, integralOrderDTO.getReceiver())
            ).get();
        } catch (InterruptedException | ExecutionException exception) {
            Throwable cause = exception.getCause();
            if (cause instanceof GlobalException) {
                throw (GlobalException) cause;
            }
            if (cause instanceof RuntimeException) {
                throw (RuntimeException) cause;
            }
            throw new RuntimeException(exception);
        }

        return integralOrder;
    }


    @SuppressWarnings("unchecked")
    @Override
    public void tryLockStock(IntegralOrder integralOrder, IntegralOrderDTO integralOrderDTO) {

        //从redis中获取可用的库存
        IntegralProduct integralProductInfo = this.commonIntegralProductService.getIntegralProductInfo(integralOrderDTO.getProductId());

        if (integralProductInfo == null) {
            throw new GlobalException(SystemCode.PARAM_VALID_ERROR_CODE, "该积分商品不存在");
        }
        // 运费* 10000
        integralOrder.setFreightPrice(integralProductInfo.getFreightPrice().longValue() * CommonPool.UNIT_CONVERSION_TEN_THOUSAND);
        integralOrder.setSalePrice(integralProductInfo.getSalePrice());
        //当前积分商品的可用库存
        Integer availableStock = integralProductInfo.getStock();
        //积分商品购买数量
        Integer buyNum = integralOrderDTO.getNum();
        if (availableStock <= 0 || availableStock < buyNum) {
            throw new GlobalException(SystemCode.PARAM_VALID_ERROR_CODE, "可用库存不足");
        }
        //积分商品在redis缓存中的key
        String cacheKey = IntegralUtil.integralProductCacheKey(integralOrderDTO.getProductId());

        //判断缓存值是否存在，存在更新缓存和数据库，不存在更新数据库
        IntegralProduct integralProductCache = RedisUtil.getCacheMap(cacheKey, IntegralProduct.class);

        if (integralProductCache != null) {
            //减redis中的库存，增加销量
            List<Object> results = RedisUtil.executePipelined(
                    //减库存
                    operations ->
                            operations.opsForHash().increment(cacheKey, IntegralConstant.INTEGRAL_PRODUCT_STOCK, -buyNum)
            );

            Long increment = (Long) results.get(CommonPool.NUMBER_ZERO);

            if (increment < 0) {
                //库存不足，回滚库存和销量
                RedisUtil.executePipelined(
                        //加回库存
                        operations ->
                                operations.opsForHash().increment(cacheKey, IntegralConstant.INTEGRAL_PRODUCT_STOCK, buyNum)
                );
                throw new GlobalException(SystemCode.PARAM_VALID_ERROR_CODE, "库存不足！");
            }
        }

        //扣除用户积分 如果
        boolean success = this.userRpcService.incrementUserIntegralTotal(integralOrder.getBuyerId(), integralOrder.getPrice(), true);
        if (!success) {
            //积分不足，扣除失败，回滚redis的库存
            RedisUtil.executePipelined(
                    //加回库存
                    operations ->
                            operations.opsForHash().increment(cacheKey, IntegralConstant.INTEGRAL_PRODUCT_STOCK, buyNum)
            );
            throw new GlobalException(SystemCode.PARAM_VALID_ERROR_CODE, "您的积分不足，无法兑换商品");
        }

        //如果是包邮且纯积分订单，修改订单状态  并插入支付数据
        if (integralOrder.getFreightPrice() == 0 && integralOrder.getSalePrice() == 0) {
            integralOrder.setStatus(IntegralOrderStatus.PAID)
                    .setPayTime(LocalDateTime.now());
            this.iIntegralOrderPaymentService.saveZeroPaymentInfo(integralOrder);
        }

        //缓存订单信息
        IntegralOrderUtil.setCacheIntegralOrder(integralOrder);

        //数据库扣除库存  发送rabbitmq消息  使用redisson更新数据库信息
        this.rabbitTemplate.convertAndSend(
                IntegralOrderRabbit.INTEGRAL_ORDER_CREATE.exchange(),
                IntegralOrderRabbit.INTEGRAL_ORDER_CREATE.routingKey(),
                new IntegralOrderDetailInfoBO()
                        .setBuyerId(integralOrder.getBuyerId())
                        .setIntegralOrderNo(integralOrder.getNo())
        );

        DataChangeMessage dataChangeMessage = new DataChangeMessage();
        dataChangeMessage.setUserId(integralOrder.getBuyerId());
        dataChangeMessage.setValue(integralOrder.getPrice());
        dataChangeMessage.setChangeType(ChangeType.REDUCE);
        dataChangeMessage.setExtendInfo(GainIntegralType.ORDER_CONSUMPTION.name());

        //发一个积分变动的消息  减积分明细
        this.rabbitTemplate.convertAndSend(
                UserRabbit.USER_INTEGRAL_CHANGE.exchange(),
                UserRabbit.USER_INTEGRAL_CHANGE.routingKey(),
                new IntegralChangeDTO().setNo(integralOrder.getNo())
                        .setDataChangeMessage(dataChangeMessage)
        );

    }

    @SuppressWarnings("unchecked")
    private CompletableFuture<Void>[] assembleTasks(IntegralOrder integralOrder, IntegralReceiverDTO receiver) {
        CompletableFuture<Void>[] tasks = new CompletableFuture[4];
        //设置收获地址
        tasks[0] = CompletableFuture.runAsync(
                () -> {
                    IntegralOrderReceiver integralOrderReceiver = new IntegralOrderReceiver();
                    integralOrderReceiver.setOrderNo(integralOrder.getNo());
                    if (receiver != null) {
                        integralOrderReceiver.setName(receiver.getName())
                                .setMobile(receiver.getMobile())
                                .setArea(receiver.getArea())
                                .setAddress(receiver.getAddress());
                    }
                    integralOrder.setIntegralOrderReceiver(integralOrderReceiver);
                },
                this.integralExecutor
        );

        //rpc获取买家昵称
        tasks[1] = CompletableFuture.runAsync(
                () -> {
                    //设置用户昵称
                    UserInfoVO userInfo = this.uaaRpcService.getUserDataByUserId(integralOrder.getBuyerId()).getOrElseThrow(() -> new GlobalException(SystemCode.PARAM_VALID_ERROR_CODE, "当前用户信息不存在"));
                    integralOrder.setBuyerNickname(ISecurity.generateNickName(userInfo.getNickname(), integralOrder.getBuyerId()).getOrNull());
                },
                this.integralExecutor
        );

        //设置购买的积分商品的信息
        //在设置之前 需要先校验一下库存
        tasks[2] = CompletableFuture.runAsync(
                () -> {

                    //获取商品信息
                    IntegralProduct integralProduct = this.commonIntegralProductService
                            .getIntegralProductInfo(integralOrder.getProductId());

                    //校验商品
                    if (integralProduct == null || IntegralProductStatus.SELL_ON != integralProduct.getStatus()) {
                        throw new GlobalException(SystemCode.PARAM_VALID_ERROR_CODE, "商品不可用");
                    }

                    //检查库存
                    if (integralOrder.getNum() > integralProduct.getStock()) {
                        throw new GlobalException(SystemCode.PARAM_VALID_ERROR_CODE, "商品库存不足");
                    }

                    //获取积分商品的信息
                    integralOrder.setProductName(integralProduct.getName())
                            .setPrice(integralProduct.getIntegralPrice())
                            .setImage(integralProduct.getPic())
                            .setSalePrice(integralProduct.getSalePrice())
                            .setProductType(integralProduct.getProductType())
                            //积分商品的运费是小数 *10000 存储在订单中
                            .setFreightPrice(integralProduct.getFreightPrice().multiply(BigDecimal.valueOf(CommonPool.UNIT_CONVERSION_TEN_THOUSAND)).longValue());

                    //rpc获取用户积分数据，检查用户积分是否足够
                    Long userIntegral = this.userRpcService.getUserIntegralTotal(integralOrder.getBuyerId());

                    if (integralOrder.getNum() * integralOrder.getPrice() > userIntegral) {
                        throw new GlobalException(SystemCode.PARAM_VALID_ERROR_CODE, "您的积分不足，无法兑换商品");
                    }

                },
                this.integralExecutor
        );
        //订单超时时间
        tasks[3] = CompletableFuture.runAsync(
                () -> integralOrder.setTimeout(
                        new OrderTimeout()
                                .setPayTimeout(
                                        orderTimeOutProperties.getTimeout().getTimeout()
                                )
                                .setConfirmTimeout(
                                        FastJson2.convert(
                                                RedisUtil.getCacheObject(IntegralConstant.INTEGRAL_TIME_OUT),
                                                Long.class
                                        )
                                )
                ),
                this.integralExecutor
        );

        return tasks;
    }


    /**
     * 1.从redis缓存中获取订单的信息
     * 2.删除redis中的缓存
     * 3.发送订单延迟自动取消mq
     * 4.将订单信息保存到数据库，更新库存
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void saveIntegralOrder2DbBatch(IntegralOrderDetailInfoBO integralOrderDetailInfoBO) {
        Long buyerId = integralOrderDetailInfoBO.getBuyerId();
        String integralOrderNo = integralOrderDetailInfoBO.getIntegralOrderNo();

        //1、从redis中获取订单信息
        IntegralOrder integralOrder = IntegralOrderUtil.getCacheIntegralOrder(buyerId,
                integralOrderNo);
        //订单已过期
        if (integralOrder == null) {
            log.error("订单已过期，订单号:{}", integralOrderNo);
            return;
        }
        //2、发送订单自动取消mq
        rabbitTemplate.convertAndSend(
                IntegralOrderRabbit.INTEGRAL_ORDER_AUTO_PAID_TIMEOUT_CLOSE.exchange(),
                IntegralOrderRabbit.INTEGRAL_ORDER_AUTO_PAID_TIMEOUT_CLOSE.routingKey(),
                new IntegralOrderDetailInfoBO()
                        .setBuyerId(integralOrder.getBuyerId())
                        .setIntegralOrderNo(integralOrder.getNo()),
                message -> {
                    message.getMessageProperties().setHeader(MessageProperties.X_DELAY, integralOrder.getTimeout().getPayTimeoutMills());
                    return message;
                }
        );
        //3、保存数据到数据库
        this.iIntegralOrderService.save(integralOrder);
        //保存订单收货数据
        this.iIntegralOrderReceiverService.save(integralOrder.getIntegralOrderReceiver());
        //4、更新库存和销量
        this.updateIntegralProductDBStockAndSalesVolume(integralOrder, true);
        //5、删除redis中的缓存
        IntegralOrderUtil.deleteIntegralOrderCache(buyerId, integralOrderNo);
    }


    @Override
    public void updateIntegralProductDBStockAndSalesVolume(IntegralOrder integralOrder, boolean isReduce) {
        //库存要增加/减少的数量
        Integer num = integralOrder.getNum();

        RLock lock = redissonClient.getLock(IntegralConstant.INTEGRAL_PRODUCT_UPDATE_STOCK);
        lock.lock();
        try {
            this.integralProductService.lambdaUpdate()
                    .eq(IntegralProduct::getId, integralOrder.getProductId())
                    //更新销量
                    .setSql(
                            StrUtil.format(IntegralConstant.SQL_REALITY_SALES_VOLUME_INCREMENT_SQL_TEMPLATE, isReduce ? num : -num)
                    )
                    //更新库存
                    .setSql(
                            StrUtil.format(IntegralConstant.SQL_STOCK_INCREMENT_SQL_TEMPLATE, isReduce ? -num : num)
                    )
                    .update();

        } finally {
            lock.unlock();
        }
    }


    @Override
    public boolean orderCreation(String orderNo) {
        return BooleanUtil.isFalse(
                RedisUtil.getRedisTemplate()
                        .hasKey(RedisUtil.key(IntegralConstant.INTEGRAL_ORDER_CACHE_KEY, ISecurity.userMust().getId(), orderNo))
        );
    }

    @Override
    public Long getOvertime() {
        Long time = FastJson2.convert(
                RedisUtil.getCacheObject(IntegralConstant.INTEGRAL_TIME_OUT),
                Long.class
        );

        if (time == null) {
            time = 3 * 24 * 60 * 60L;
            RedisUtil.setCacheObject(IntegralConstant.INTEGRAL_TIME_OUT, time);
        }
        return time;
    }

    @Override
    public void updateOvertime(Long time) {
        RedisUtil.setCacheObject(IntegralConstant.INTEGRAL_TIME_OUT, time);
    }
}
