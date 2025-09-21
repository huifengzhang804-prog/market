package com.medusa.gruul.payment.service.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.date.DatePattern;
import cn.hutool.core.io.resource.ResourceUtil;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONArray;
import com.baomidou.mybatisplus.extension.conditions.query.LambdaQueryChainWrapper;
import com.egzosn.pay.wx.api.WxConst;
import com.github.binarywang.wxpay.bean.applyment.ApplymentStateQueryResult;
import com.github.binarywang.wxpay.bean.applyment.WxPayApplyment4SubCreateRequest;
import com.github.binarywang.wxpay.bean.applyment.WxPayApplymentCreateResult;
import com.github.binarywang.wxpay.bean.applyment.enums.ApplymentStateEnum;
import com.github.binarywang.wxpay.bean.applyment.enums.SalesScenesTypeEnum;
import com.github.binarywang.wxpay.bean.ecommerce.CombineTransactionsRequest;
import com.github.binarywang.wxpay.bean.ecommerce.CombineTransactionsResult;
import com.github.binarywang.wxpay.bean.ecommerce.enums.TradeTypeEnum;
import com.github.binarywang.wxpay.bean.media.ImageUploadResult;
import com.github.binarywang.wxpay.bean.notify.WxPayNotifyV3Response;
import com.github.binarywang.wxpay.bean.notify.WxPayRefundNotifyV3Result;
import com.github.binarywang.wxpay.bean.profitsharing.Receiver;
import com.github.binarywang.wxpay.bean.profitsharing.request.ProfitSharingReceiverV3Request;
import com.github.binarywang.wxpay.bean.profitsharing.request.ProfitSharingRequest;
import com.github.binarywang.wxpay.bean.profitsharing.request.ProfitSharingUnfreezeV3Request;
import com.github.binarywang.wxpay.bean.profitsharing.result.ProfitSharingOrderAmountQueryV3Result;
import com.github.binarywang.wxpay.bean.profitsharing.result.ProfitSharingResult;
import com.github.binarywang.wxpay.bean.request.WxPayRefundV3Request;
import com.github.binarywang.wxpay.bean.result.WxPayRefundV3Result;
import com.github.binarywang.wxpay.exception.WxPayException;
import com.github.binarywang.wxpay.service.WxPayService;
import com.github.binarywang.wxpay.service.impl.Applyment4SubServiceImpl;
import com.medusa.gruul.common.model.constant.CommonPool;
import com.medusa.gruul.common.model.enums.PayType;
import com.medusa.gruul.common.model.pay.Transaction;
import com.medusa.gruul.common.model.resp.SystemCode;
import com.medusa.gruul.common.mp.IManualTransaction;
import com.medusa.gruul.common.redis.annotation.Redisson;
import com.medusa.gruul.common.redis.util.RedisUtil;
import com.medusa.gruul.common.security.model.enums.SecureCodes;
import com.medusa.gruul.common.system.model.model.Platform;
import com.medusa.gruul.global.config.GlobalAppProperties;
import com.medusa.gruul.global.model.constant.Services;
import com.medusa.gruul.global.model.enums.ReceiverType;
import com.medusa.gruul.payment.api.entity.PaymentInfo;
import com.medusa.gruul.payment.api.entity.PaymentRecord;
import com.medusa.gruul.payment.api.entity.PaymentRefund;
import com.medusa.gruul.payment.api.enums.*;
import com.medusa.gruul.payment.api.model.dto.PayNotifyResultDTO;
import com.medusa.gruul.payment.api.model.dto.PaymentInfoDTO;
import com.medusa.gruul.payment.api.model.dto.RefundNotifyResultDTO;
import com.medusa.gruul.payment.api.model.dto.RefundRequestDTO;
import com.medusa.gruul.payment.api.model.param.*;
import com.medusa.gruul.payment.service.model.PayConst;
import com.medusa.gruul.payment.service.model.enums.PaymentError;
import com.medusa.gruul.payment.service.mp.entity.MerchantSub;
import com.medusa.gruul.payment.service.mp.entity.PaymentNotifyRecord;
import com.medusa.gruul.payment.service.mp.entity.PaymentReceiver;
import com.medusa.gruul.payment.service.mp.entity.PaymentSharingRecord;
import com.medusa.gruul.payment.service.mp.service.*;
import com.medusa.gruul.payment.service.properties.PaymentProperty;
import com.medusa.gruul.payment.service.service.WxServiceService;
import com.medusa.gruul.service.uaa.api.rpc.UaaRpcService;
import com.medusa.gruul.service.uaa.api.vo.UserInfoVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.context.annotation.Lazy;

import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * 微信服务商接口实现
 *
 * @author 张治保
 * date 2023/6/3
 * <a href="https://pay.weixin.qq.com/wiki/doc/apiv3_partner/apis/chapter11_1_1.shtml">微信服务商接口文档</a>
 */
@Slf4j
@Service
@RequiredArgsConstructor
@ConditionalOnProperty(prefix = "gruul.pay", name = "enable-service-mode", havingValue = "true")
public class WxServiceEnableServiceImpl implements WxServiceService {

    private static final String RETRY_ERROR_CODE = "SYSTEM_ERROR";
    private static final DateTimeFormatter TIME_FORMATTER = DatePattern.UTC_WITH_XXX_OFFSET_FORMAT.getDateTimeFormatter();

    private final WxPayService wxPayService;
    private  UaaRpcService uaaRpcService;
    private final RabbitTemplate rabbitTemplate;
    private final IMerchantSubService merchantSubService;
    private final IPaymentInfoService paymentInfoService;
    private final IPaymentRecordService paymentRecordService;
    private final IPaymentRefundService paymentRefundService;
    private final IPaymentReceiverService paymentReceiverService;
    private final IPaymentNotifyRecordService paymentNotifyRecordService;
    private final IPaymentSharingRecordService paymentSharingRecordService;
    private final PaymentProperty paymentProperty;
    private final GlobalAppProperties globalAppProperties;
    private WxServiceService wxServiceService;


    @Lazy
    @Autowired
    public void setUaaRpcService(UaaRpcService uaaRpcService) {
        this.uaaRpcService = uaaRpcService;
    }



    @Override
    @Transactional(rollbackFor = Exception.class)
    public Object combineOrder(CombineOrderParam param, Map<Long, String> shopSubMchIdMap) {
        CombineTransactionsRequest request = new CombineTransactionsRequest();
        request.setCombineAppid(paymentProperty.getWxServiceConf().getAppid());
        request.setCombineMchid(paymentProperty.getWxServiceConf().getMchId());
        request.setNotifyUrl(wxPayService.getConfig().getNotifyUrl());
        //总订单号
        request.setCombineOutTradeNo(param.getOrderNo());
        //场景信息
        CombineTransactionsRequest.SceneInfo sceneInfo = new CombineTransactionsRequest.SceneInfo();
        sceneInfo.setPayerClientIp(param.getConsumer().getClientIp());
        TradeTypeEnum tradeType = getTradeTypeEnum(param.getConsumer().getPlatform());
        //h5支付需要传入 h5_info
        if (TradeTypeEnum.MWEB == tradeType) {
            CombineTransactionsRequest.H5Info h5Info = new CombineTransactionsRequest.H5Info();
            h5Info.setType("Wap");
            sceneInfo.setH5Info(h5Info);
        }
        //app支付需要使用 微信开发平台的appid
        if (TradeTypeEnum.APP == tradeType) {
            request.setCombineAppid(paymentProperty.getWxServiceConf().getOpenAppid());
        }
        if (TradeTypeEnum.JSAPI == tradeType) {
            CombineTransactionsRequest.CombinePayerInfo combinePayerInfo = new CombineTransactionsRequest.CombinePayerInfo();
            combinePayerInfo.setOpenid(param.getConsumer().getOpenId());
            request.setCombinePayerInfo(combinePayerInfo);
        }
        request.setSceneInfo(sceneInfo);
        request.setSubOrders(subOrders(param, shopSubMchIdMap));
        //交易开始时间
        ZonedDateTime startTime = ZonedDateTime.now();
        request.setTimeStart(startTime.format(TIME_FORMATTER));
        request.setTimeExpire(startTime.plusSeconds(param.getSeconds()).format(TIME_FORMATTER));
        //保存支付记录
        this.savePaymentInfo(param, request);
        try {
            return wxPayService.getEcommerceService().combineTransactions(tradeType, request);
        } catch (WxPayException exception) {
            log.error("微信服务商合单支付失败", exception);
            throw PaymentError.COMBINE_ORDER_ERROR.dataEx(JSON.toJSON(exception));
        }
    }

    private void savePaymentInfo(CombineOrderParam param, CombineTransactionsRequest request) {
        PaymentInfo paymentInfo = new PaymentInfo()
                .setShopId(0L)
                .setOpenId(param.getConsumer().getOpenId())
                .setUserId(param.getConsumer().getUserId())
                .setAppId(request.getCombineAppid())
                .setOrderNum(param.getOrderNo())
                .setPayType(param.getPayType())
                .setPayPlatform(param.getConsumer().getPlatform())
                .setExchange(param.getExchange())
                .setRouteKey(param.getRouteKey())
                .setFeeType(param.getSubOrders().get(CommonPool.NUMBER_ZERO).getCurrency())
                .setTotalFee(param.getSubOrders().stream().map(CombineOrderParam.SubOrder::getTotalAmount).reduce(0L, Long::sum))
                .setSeconds(param.getSeconds().toString())
                .setTerminalIp(param.getConsumer().getClientIp())
                .setBusinessNotifyUrl(request.getNotifyUrl())
                .setThirdPartyNotifyStatus(NotifyStatus.UNFINISHED)
                .setBusinessNotifyStatus(NotifyStatus.UNFINISHED)
                .setTradeStatus(TradeStatus.PENDING_PAYMENT)
                .setThirdPartyNotifyNumber(CommonPool.NUMBER_ZERO)
                .setBusinessParams(JSON.toJSONString(param.toPaymentInfo()));
        paymentInfoService.save(paymentInfo);
        PaymentRecord record = new PaymentRecord();
        record.setPaymentId(paymentInfo.getId())
                .setRequestParams(JSON.toJSONString(param))
                .setSendParam(JSON.toJSONString(request));
        paymentRecordService.save(record);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public String combinePayNotify(CombineTransactionsResult notifyResult) {
        //检查是否是支付成功通知
        List<CombineTransactionsResult.SubOrders> subOrders = notifyResult.getSubOrders();
        String tradeState = subOrders.get(CommonPool.NUMBER_ZERO).getTradeState();
        if (!WxConst.SUCCESS.equals(tradeState)) {
            return WxPayNotifyV3Response.success("成功");
        }
        String orderNo = notifyResult.getCombineOutTradeNo();
        //支付记录
        PaymentInfo paymentInfo = paymentInfoService.lambdaQuery()
                .eq(PaymentInfo::getOrderNum, orderNo)
                .eq(PaymentInfo::getBusinessNotifyStatus, NotifyStatus.UNFINISHED)
                .one();
        if (paymentInfo == null) {
            return WxPayNotifyV3Response.success("成功");
        }
        paymentInfo.setThirdPartyNotifyNumber(paymentInfo.getThirdPartyNotifyNumber() + 1)
                .setThirdPartyNotifyStatus(NotifyStatus.ACCOMPLISH)
                .setBusinessNotifyStatus(NotifyStatus.ACCOMPLISH)
                .setTradeStatus(TradeStatus.SUCCESS_PAYMENT);
        paymentInfoService.updateById(paymentInfo);
        paymentRecordService.lambdaUpdate()
                .eq(PaymentRecord::getPaymentId, paymentInfo.getId())
                .set(PaymentRecord::getNotifyParam, JSON.toJSONString(notifyResult))
                .update();
        this.notifyRecords(notifyResult.getCombineAppid(), notifyResult.getCombineMchid(), orderNo, subOrders);
        IManualTransaction.afterCommit(
                //支付成功回调
                () -> rabbitTemplate.convertAndSend(
                        paymentInfo.getExchange(),
                        paymentInfo.getRouteKey(),
                        new PayNotifyResultDTO()
                                .setPayOrderType(PayOrderType.COMBINE)
                                .setOutTradeNo(orderNo)
                                .setBusinessParams(JSON.parseObject(paymentInfo.getBusinessParams(), PaymentInfoDTO.class))
                                .setShopIdTransactionMap(this.shopIdTransactionIdMap(subOrders))
                )
        );
        return WxPayNotifyV3Response.success("成功");
    }

    private void notifyRecords(String appid, String mchId, String orderNo, List<CombineTransactionsResult.SubOrders> subOrders) {
        List<PaymentNotifyRecord> records = subOrders.stream()
                .map(
                        subOrder -> {
                            CombineShopOrderConf currentConf = JSON.parseObject(subOrder.getAttach(), CombineShopOrderConf.class);
                            return new PaymentNotifyRecord()
                                    .setAppid(appid)
                                    .setMchId(mchId)
                                    .setOrderType(PayOrderType.COMBINE)
                                    .setOrderNo(orderNo)
                                    .setSubOrderNo(subOrder.getOutTradeNo())
                                    .setShopId(currentConf.getShopId())
                                    .setTransactionId(subOrder.getTransactionId())
                                    .setNotify(subOrder)
                                    .setNotifyTime(LocalDateTime.from(TIME_FORMATTER.parse(subOrder.getSuccessTime())));
                        }
                ).toList();
        paymentNotifyRecordService.saveBatch(records);
    }

    /**
     * 店铺id和交易流水id映射
     *
     * @param subOrders 子订单信息
     * @return 店铺id和交易流水id映射
     */
    private Map<Long, Transaction> shopIdTransactionIdMap(List<CombineTransactionsResult.SubOrders> subOrders) {
        Map<Long, Transaction> shopIdTransactionIdMap = new HashMap<>(CommonPool.NUMBER_NINE);
        subOrders.forEach(
                subOrder -> {
                    CombineShopOrderConf currentConf = JSON.parseObject(subOrder.getAttach(), CombineShopOrderConf.class);
                    shopIdTransactionIdMap.put(
                            currentConf.getShopId(),
                            new Transaction()
                                    .setTransactionId(subOrder.getTransactionId())
                                    .setProfitSharing(currentConf.getSharing())
                    );
                }
        );
        return shopIdTransactionIdMap;
    }

    /**
     * 根据合单参数 渲染合单子单信息
     *
     * @param param           合单参数
     * @param shopSubMchIdMap 店铺id和子商户号映射
     * @return 合单子单信息
     */
    private List<CombineTransactionsRequest.SubOrders> subOrders(CombineOrderParam param, Map<Long, String> shopSubMchIdMap) {
        List<CombineOrderParam.SubOrder> subOrders = param.getSubOrders();
        //渲染子订单数据
        return Stream.concat(
                //有子商户号的子单
                subOrders.stream().filter(subOrder -> shopSubMchIdMap.containsKey(subOrder.getShopId())),
                //无子商户号的子单 合并成平台订单
                subOrders.stream()
                        .filter(subOrder -> !shopSubMchIdMap.containsKey(subOrder.getShopId()))
                        .reduce((subOrder1, subOrder2) -> subOrder1.setTotalAmount(subOrder1.getTotalAmount() + subOrder2.getTotalAmount()))
                        .stream()
                        .peek(subOrder -> subOrder.setShopId(0L).setProfitSharing(false).setSubsidyAmount(0L))
        ).map(subOrder -> {
            //子单信息
            CombineTransactionsRequest.SubOrders subOrderParam = new CombineTransactionsRequest.SubOrders();
            subOrderParam.setMchid(paymentProperty.getWxServiceConf().getMchId());
            subOrderParam.setAttach(JSON.toJSONString(new CombineShopOrderConf().setShopId(subOrder.getShopId()).setSharing(subOrder.getProfitSharing())));
            //订单金额
            CombineTransactionsRequest.Amount amount = new CombineTransactionsRequest.Amount();
            amount.setTotalAmount((int) (subOrder.getTotalAmount() / CommonPool.UNIT_CONVERSION_HUNDRED));
            amount.setCurrency(subOrder.getCurrency().getType());
            subOrderParam.setAmount(amount);
            subOrderParam.setOutTradeNo(subOrder.getSubOrderNo());
            subOrderParam.setSubMchid(shopSubMchIdMap.getOrDefault(subOrder.getShopId(), paymentProperty.getWxServiceConf().getSelfSubMchid()));
            subOrderParam.setDescription(StrUtil.blankToDefault(subOrder.getDescription(), subOrder.getSubOrderNo()));
            //结算信息
            CombineTransactionsRequest.SettleInfo settleInfo = new CombineTransactionsRequest.SettleInfo();
            settleInfo.setProfitSharing(subOrder.getProfitSharing());
            settleInfo.setSubsidyAmount((int) (subOrder.getSubsidyAmount() / CommonPool.UNIT_CONVERSION_HUNDRED));
            subOrderParam.setSettleInfo(settleInfo);
            return subOrderParam;
        }).toList();
    }

    /**
     * 根据平台类型获取交易类型
     *
     * @param platform 平台类型
     * @return 交易类型
     */
    private TradeTypeEnum getTradeTypeEnum(Platform platform) {
        return switch (platform) {
            case IOS, ANDROID, HARMONY -> TradeTypeEnum.APP;
            case PC, H5 -> TradeTypeEnum.MWEB;
            case WECHAT_MP, WECHAT_MINI_APP -> TradeTypeEnum.JSAPI;
        };
    }

    @Override
    public Map<Long, String> shopSubMchIdMap(Set<Long> shopIds) {
        List<MerchantSub> merchantSubs = merchantSubService.lambdaQuery()
                .select(MerchantSub::getShopId, MerchantSub::getSubMchid)
                .eq(MerchantSub::getApplymentState, ApplymentStateEnum.APPLYMENT_STATE_FINISHED.name())
                .eq(MerchantSub::getConfirmed, Boolean.TRUE)
                .in(MerchantSub::getShopId, shopIds)
                .list();
        return merchantSubs.stream()
                .collect(Collectors.toMap(MerchantSub::getShopId, MerchantSub::getSubMchid));
    }

    @Override
    public WxPayApplymentCreateResult createApply(Long shopId, WxPayApplyment4SubCreateRequest request) {
        request.setBusinessCode(RedisUtil.noStr(PayConst.WX_SERVICE_BUSINESS_NO_KEY));
        WxPayApplyment4SubCreateRequest.BusinessInfo businessInfo = request.getBusinessInfo();
        WxPayApplyment4SubCreateRequest.BusinessInfo.SalesInfo salesInfo = businessInfo.getSalesInfo();
        if (salesInfo == null) {
            salesInfo = new WxPayApplyment4SubCreateRequest.BusinessInfo.SalesInfo();
            businessInfo.setSalesInfo(salesInfo);
        }
        PaymentProperty.WxServiceConf wxServiceConf = paymentProperty.getWxServiceConf();
        //小程序appid
        String appid = wxServiceConf.getAppid();
        salesInfo.setSalesScenesType(new ArrayList<>());
        salesInfo.getSalesScenesType().add(SalesScenesTypeEnum.SALES_SCENES_MINI_PROGRAM);
        salesInfo.setMiniProgramInfo(
                WxPayApplyment4SubCreateRequest.BusinessInfo.SalesInfo.MiniProgramInfo.builder()
                        .miniProgramAppid(appid)
                        .build()
        );
        // 开放平台 app 的appid
        String openAppid = wxServiceConf.getOpenAppid();
        if (StrUtil.isNotBlank(openAppid)) {
            salesInfo.getSalesScenesType().add(SalesScenesTypeEnum.SALES_SCENES_APP);
            salesInfo.setAppInfo(
                    WxPayApplyment4SubCreateRequest.BusinessInfo.SalesInfo.AppInfo.builder()
                            .appAppid(openAppid)
                            .appPics(appPics())
                            .build()
            );
        }
        //native支付网站域名
        String domain = wxServiceConf.getDomain();
        if (StrUtil.isNotBlank(domain)) {
            WxPayApplyment4SubCreateRequest.BusinessInfo.SalesInfo.WebInfo webInfo = salesInfo.getWebInfo();
            if (webInfo == null || StrUtil.isBlank(webInfo.getWebAuthorisation())) {
                throw PaymentError.MISS_WEBSITE_AUTHORIZATION_LETTER.exception();
            }
            salesInfo.getSalesScenesType().add(SalesScenesTypeEnum.SALES_SCENES_WEB);
            webInfo.setDomain(domain);
            salesInfo.setWebInfo(webInfo);
        }
        WxPayApplymentCreateResult apply;
        try {
            apply = new Applyment4SubServiceImpl(wxPayService).createApply(request);
        } catch (WxPayException e) {
            log.error("微信特约商户申请失败", e);
            throw PaymentError.SUB_MERCHANT_APPLY_ERROR.dataEx(JSON.toJSON(e));
        }
        wxServiceService.applyBound(shopId, apply.getApplymentId());
        return apply;
    }

    private List<String> appPics() {
        List<String> pics = RedisUtil.getCacheList(PayConst.APP_PICS_KEY);
        if (CollUtil.isNotEmpty(pics)) {
            return pics;
        }
        pics = new ArrayList<>();
        pics.add(this.imageUploadV3(ResourceUtil.getStream("image/app/home.jpg"), "home.jpg").getMediaId());
        pics.add(this.imageUploadV3(ResourceUtil.getStream("image/app/mine.jpg"), "mine.jpg").getMediaId());
        pics.add(this.imageUploadV3(ResourceUtil.getStream("image/app/order.jpg"), "order.jpg").getMediaId());
        pics.add(this.imageUploadV3(ResourceUtil.getStream("image/app/pay.jpg"), "pay.jpg").getMediaId());
        RedisUtil.setCacheList(PayConst.APP_PICS_KEY, pics);
        return pics;
    }

    @Override
    public ApplymentStateQueryResult applyState(String applymentId) {
        try {
            return new Applyment4SubServiceImpl(wxPayService).queryApplyStatusByApplymentId(applymentId);
        } catch (WxPayException e) {
            log.error("微信特约商户申请状态查询失败", e);
            throw PaymentError.SUB_MERCHANT_APPLY_QUERY_ERROR.dataEx(JSON.toJSON(e));
        }
    }

    @Override
    @Redisson(name = PayConst.SHOP_MERCHANT_SUB_LOCK_KEY, key = "#shopId")
    public ApplymentStateQueryResult applyBound(Long shopId, String applymentId) {
        // 查询是否已经绑定成功
        MerchantSub merchantSub = merchantSubService.lambdaQuery()
                .eq(MerchantSub::getShopId, shopId)
                .one();
        boolean isNew = merchantSub == null;
        if (isNew) {
            merchantSub = new MerchantSub()
                    .setShopId(shopId)
                    .setConfirmed(Boolean.FALSE)
                    .setCreateTime(LocalDateTime.now());
        }
        ApplymentStateQueryResult result = applyState(applymentId);
        //申请单不一致 重置确认状态
        if (!isNew && !result.getApplymentId().equals(merchantSub.getApplymentId())) {
            merchantSub.setConfirmed(Boolean.FALSE)
                    .setConfirmTime(null)
                    .setCreateTime(LocalDateTime.now());

        }
        merchantSub.setApplymentId(applymentId)
                .setBusinessCode(result.getBusinessCode())
                .setApplymentState(result.getApplymentState())
                .setSubMchid(result.getSubMchid())
                .setQueryResp(result);
        merchantSubService.saveOrUpdate(merchantSub);
        return result;
    }

    @Override
    public ImageUploadResult imageUploadV3(MultipartFile multipartFile) {
        try {
            return this.imageUploadV3(multipartFile.getInputStream(), multipartFile.getOriginalFilename());
        } catch (IOException e) {
            throw PaymentError.IMAGE_MATERIAL_UPLOAD_ERROR.exception();
        }
    }

    public ImageUploadResult imageUploadV3(InputStream inputStream, String fileName) {
        try {
            return wxPayService.getMerchantMediaService()
                    .imageUploadV3(inputStream, IdUtil.fastSimpleUUID() + fileName);
        } catch (WxPayException e) {
            throw PaymentError.IMAGE_MATERIAL_UPLOAD_ERROR.dataEx(JSON.toJSON(e));
        } catch (IOException e) {
            throw PaymentError.IMAGE_MATERIAL_UPLOAD_ERROR.exception();
        }
    }


    @Override
    public MerchantSub applyment(Long shopId) {
        MerchantSub sub = merchantSubService.lambdaQuery()
                .eq(MerchantSub::getShopId, shopId)
                .orderByDesc(MerchantSub::getBusinessCode)
                .one();
        if (sub == null || sub.getConfirmed()) {
            return sub;
        }
        ApplymentStateQueryResult result = wxServiceService.applyBound(sub.getShopId(), sub.getApplymentId());
        sub.setApplymentState(result.getApplymentState())
                .setSubMchid(result.getSubMchid())
                .setQueryResp(result);

        return sub;
    }

    @Override
    @Redisson(name = PayConst.SHOP_MERCHANT_SUB_LOCK_KEY, key = "#shopId")
    @Transactional(rollbackFor = Exception.class)
    public void confirmApply(Long shopId) {
        MerchantSub one = merchantSubService.lambdaQuery()
                .eq(MerchantSub::getShopId, shopId)
                .one();
        if (one == null || ApplymentStateEnum.APPLYMENT_STATE_FINISHED != one.getApplymentState()) {
            throw SecureCodes.PERMISSION_DENIED.exception();
        }
        //校验商户是否已开通app和h5权限
        //分别用不同平台下单
        this.platformPayTest(one.getSubMchid());
        //支付测试通过 更新数据
        merchantSubService.lambdaUpdate()
                .eq(MerchantSub::getShopId, shopId)
                .eq(MerchantSub::getApplymentState, ApplymentStateEnum.APPLYMENT_STATE_FINISHED)
                .eq(MerchantSub::getConfirmed, Boolean.FALSE)
                .set(MerchantSub::getConfirmed, Boolean.TRUE)
                .update();
        //添加平台作为分账方
        this.addReceiverBatch(
                paymentProperty.getWxServiceConf().getAppid(),
                Set.of(
                        new PaymentReceiver()
                                .setSubMchid(one.getSubMchid())
                                .setType(ReceiverType.PLATFORM)
                                .setAccount(paymentProperty.getWxServiceConf().getMchId())
                )
        );

    }

    private CombineOrderParam shopTestOrder(Platform platform) {
        Long shopId = IdUtil.getSnowflakeNextId();
        return new CombineOrderParam()
                .setOrderNo(shopId.toString())
                .setPayType(PayType.WECHAT)
                .setSeconds(20L)
                .setAttach(platform.name() + "支付测试")
                .setConsumer(
                        new CombineOrderParam.Consumer()
                                .setPlatform(platform)
                                .setUserId(1L)
                                .setDeviceId("11.5223.#2231")
                                .setClientIp("66.249.65.97")
                )
                .setSubOrders(
                        List.of(
                                new CombineOrderParam.SubOrder()
                                        .setShopId(shopId)
                                        .setSubOrderNo(shopId + "-1")
                                        .setTotalAmount(100L)
                                        .setCurrency(FeeType.CNY)
                                        .setDescription("店铺合单-测试支付是否正常")
                                        .setProfitSharing(false)
                                        .setSubsidyAmount(0L)
                        )
                );
    }

    /**
     * 不同平台的支付测试 以验证支付配置的正确
     *
     * @param subMchid 子商户号
     */
    @SuppressWarnings("SpringTransactionalMethodCallsInspection")
    private void platformPayTest(String subMchid) {
        //h5测试
        CombineOrderParam h5param = shopTestOrder(Platform.H5);
        this.combineOrder(h5param, Map.of(h5param.getSubOrders().get(0).getShopId(), subMchid));
        //app测试
        CombineOrderParam appParam = shopTestOrder(Platform.IOS);
        this.combineOrder(appParam, Map.of(appParam.getSubOrders().get(0).getShopId(), subMchid));
        //网页二维码
        CombineOrderParam pcParam = shopTestOrder(Platform.PC);
        this.combineOrder(pcParam, Map.of(pcParam.getSubOrders().get(0).getShopId(), subMchid));
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void profitSharing(ProfitSharingParam param) {
        PaymentNotifyRecord record = paymentNotifyRecordService.lambdaQuery()
                .eq(PaymentNotifyRecord::getOrderNo, param.getShopOrderKey().getOrderNo())
                .eq(PaymentNotifyRecord::getShopId, param.getShopOrderKey().getShopId())
                .one();
        if (record == null) {
            throw SystemCode.DATA_NOT_EXIST.exception();
        }
        CombineTransactionsResult.SubOrders subOrder = record.getNotify();
        String appid = record.getAppid();
        String subMchid = subOrder.getSubMchid();
        Set<Long> userIds = param.getReceivers()
                .stream()
                .filter(receiver -> receiver.getType() != ReceiverType.PLATFORM)
                .map(ProfitSharingParam.Receiver::getAccountId)
                .collect(Collectors.toSet());
        Map<Long, UserInfoVO> userDataBatchByUserIds = CollUtil.isEmpty(userIds) ? Map.of() : uaaRpcService.getUserDataBatchByUserIds(userIds);
        //添加分账接收方
        Set<PaymentReceiver> receivers = param.getReceivers()
                .stream()
                .filter(receiver -> receiver.getType() != ReceiverType.PLATFORM)
                .map(receiver -> new PaymentReceiver()
                        .setSubMchid(subMchid)
                        .setType(receiver.getType())
                        .setAccount(userDataBatchByUserIds.get(receiver.getAccountId()).getOpenid())
                )
                .collect(Collectors.toSet());
        this.addReceiverBatch(appid, receivers);
        String mchId = paymentProperty.getWxServiceConf().getMchId();
        String sharingNo = param.getSharingNo();
        Map<ServiceReceiverKey, ReceiverKey> accountMap = new HashMap<>(CommonPool.NUMBER_EIGHT);
        ProfitSharingRequest request = ProfitSharingRequest.newBuilder()
                .transactionId(record.getTransactionId())
                .outOrderNo(sharingNo)
                .receivers(
                        JSONArray.toJSONString(
                                param.getReceivers().stream()
                                        .map(receiver -> {
                                                    ReceiverType type = receiver.getType();
                                                    Receiver receiverRequest = new Receiver(
                                                            type.getType(),
                                                            type == ReceiverType.PLATFORM ? mchId : userDataBatchByUserIds.get(receiver.getAccountId()).getOpenid(),
                                                            receiver.getAmount().intValue() / 100,
                                                            receiver.getDescription()
                                                    );
                                                    accountMap.put(
                                                            new ServiceReceiverKey().setType(type.getType()).setAccount(type == ReceiverType.PLATFORM ? mchId : userDataBatchByUserIds.get(receiver.getAccountId()).getOpenid()),
                                                            new ReceiverKey().setReceiverType(type).setAccountId(receiver.getAccountId())
                                                    );
                                                    return receiverRequest;
                                                }
                                        ).collect(Collectors.toList())
                        )
                )
                .build();
        request.setSubMchId(subMchid);
        request.setAppid(appid);
        PaymentSharingRecord sharingRecord = new PaymentSharingRecord()
                .setService(SharingService.WECHAT)
                .setSharingNo(sharingNo)
                .setShopId(record.getShopId())
                .setSubMchid(subMchid)
                .setTransactionId(record.getTransactionId())
                .setRequest(request)
                .setAccountMap(accountMap)
                .setNotifyKey(param.getNotifyKey())
                .setCreateTime(LocalDateTime.now());
        this.sharingResult(sharingRecord, request);
        paymentSharingRecordService.save(sharingRecord);
        //如果分账处理成功 立即发送回调通知
        if (sharingRecord.getStatus() != SharingStatus.PROCESSING) {
            IManualTransaction.afterCommit(
                    () -> rabbitTemplate.convertAndSend(sharingRecord.getNotifyKey().getExchange(), sharingRecord.getNotifyKey().getRoutingKey(), sharingRecord.sharingResult())
            );
        }


    }

    private void sharingResult(PaymentSharingRecord record, ProfitSharingRequest request) {
        try {
            ProfitSharingResult result = wxPayService.getProfitSharingService().profitSharing(request);
            record.setStatus(SharingStatus.valueOf(result.getStatus())).setResult(result);
        } catch (WxPayException wxPayException) {
            //重试
            if (RETRY_ERROR_CODE.equals(wxPayException.getErrCode())) {
                this.sharingResult(record, request);
                return;
            }
            //失败
            record.setErrorReason(wxPayException).setStatus(SharingStatus.ERROR);
        }
    }


//    todo 暂不使用
//    @Scheduled(cron = "0 0/5 * * * ?")
//    public void profitSharingQueryBatch() {
//        Page<PaymentSharingRecord> page;
//        long total;
//        int perSize = 1000;
//        do {
//            page = paymentSharingRecordService.lambdaQuery()
//                    .eq(PaymentSharingRecord::getStatus, SharingStatus.PROCESSING)
//                    .page(new Page<>(1, perSize));
//            if ((total = page.getTotal()) == 0) {
//                break;
//            }
//            List<PaymentSharingRecord> records = page.getRecords();
//            //多线程 分片处理
//            //比如 一共1000条数据 分给10个线程 每个线程处理100条数据
//            int size = records.size();
//            int threadSize = CommonPool.NUMBER_TEN;
//            int shardSize = size / threadSize;
//            if (shardSize == CommonPool.NUMBER_ZERO) {
//                shardSize = CommonPool.NUMBER_ONE;
//            }
//            List<List<PaymentSharingRecord>> shardList = ListUtil.split(records, shardSize);
//            //继续
//            for (List<PaymentSharingRecord> sharingRecords : shardList) {
//                //多线程处理
//                List<PaymentSharingRecord> updateRecords = new ArrayList<>();
//                CompletableFuture.runAsync(() -> {
//                            //添加分布式锁
//                            for (PaymentSharingRecord record : sharingRecords) {
//                                this.sharingResult(record);
//                                updateRecords.add(record);
//                                if (record.getStatus() != SharingStatus.PROCESSING) {
//                                    IManualTransaction.afterCommit(
//                                            () -> rabbitTemplate.convertAndSend(record.getNotifyKey().getExchange(), record.getNotifyKey().getRoutingKey(), record.sharingResult())
//                                    );
//                                }
//                            }
//                            paymentSharingRecordService.updateBatchById(updateRecords);
//                        }
//                        , globalExecutor);
//            }
//            total -= perSize;
//        } while (total > 0);
//    }

//    private void sharingResult(PaymentSharingRecord record) {
//        try {
//            ProfitSharingResult result = wxPayService.getProfitSharingService().profitSharing(new ProfitSharingRequest(record.getSharingNo(), record.getTransactionId(), record.getSubMchid()));
//            record.setResult(result)
//                    .setStatus(SharingStatus.valueOf(result.getStatus()));
//        } catch (WxPayException wxPayException) {
//            //重试
//            if (!RETRY_ERROR_CODE.equals(wxPayException.getErrCode())) {
//                this.sharingResult(record);
//                return;
//            }
//            //失败
//            record.setErrorReason(wxPayException).setStatus(SharingStatus.ERROR);
//        }
//
//    }


    @Override
    public void profitSharingUnfreeze(ProfitSharingUnfreezeParam param) {
        PaymentNotifyRecord record = paymentNotifyRecordService.lambdaQuery()
                .eq(PaymentNotifyRecord::getOrderNo, param.getShopOrderKey().getOrderNo())
                .eq(PaymentNotifyRecord::getShopId, param.getShopOrderKey().getShopId())
                .one();
        if (record == null) {
            return;
        }
        try {
            //查询剩余待分金额
            ProfitSharingOrderAmountQueryV3Result amountResult = wxPayService.getProfitSharingService().profitSharingUnsplitAmountQueryV3(record.getTransactionId());
            String unsplitAmount = amountResult.getUnsplitAmount();
            if (StrUtil.isBlank(unsplitAmount)) {
                return;
            }
            int amount = Integer.parseInt(unsplitAmount);
            if (amount <= 0) {
                return;
            }
            wxPayService.getProfitSharingService().profitSharingUnfreeze(
                    ProfitSharingUnfreezeV3Request.newBuilder()
                            .subMchId(record.getNotify().getSubMchid())
                            .transactionId(record.getTransactionId())
                            .outOrderNo(param.getSharingNo())
                            .description("解冻全部剩余资金")
                            .build()
            );
        } catch (WxPayException exception) {
            log.error("解冻分账失败", exception);
            throw PaymentError.UNFREEZE_REMAINING_FUNDS_ERROR.dataEx(JSON.toJSON(exception));
        }
    }

    @Override
    public void addReceiverBatch(String appid, Set<PaymentReceiver> receivers) {
        if (CollUtil.isEmpty(receivers)) {
            return;
        }
        //过滤已经是接收方的数据
        LambdaQueryChainWrapper<PaymentReceiver> queryWrapper = paymentReceiverService.lambdaQuery();
        receivers.forEach(
                receiver -> queryWrapper.or()
                        .eq(PaymentReceiver::getSubMchid, receiver.getSubMchid())
                        .eq(PaymentReceiver::getType, receiver.getType())
                        .eq(PaymentReceiver::getAccount, receiver.getAccount())
        );
        Set<PaymentReceiver> existedReceivers = new HashSet<>(queryWrapper.list());
        //真实需要添加的接收方
        Set<PaymentReceiver> addReceivers = receivers.stream()
                .filter(receiver -> !existedReceivers.contains(receiver))
                .collect(Collectors.toSet());
        if (CollUtil.isEmpty(addReceivers)) {
            return;
        }
        addReceivers.forEach(
                receiver -> {
                    ReceiverType type = receiver.getType();
                    try {
                        wxPayService.getProfitSharingService().addReceiverV3(
                                ProfitSharingReceiverV3Request.newBuilder().subMchId(receiver.getSubMchid())
                                        .appid(appid)
                                        .type(type.getType())
                                        .account(receiver.getAccount())
                                        .relationType(type.getRelations())
                                        .build()
                        );
                    } catch (WxPayException exception) {
                        log.error("添加分账方失败", exception);
                        throw PaymentError.ADD_PROFIT_SHARING_RECEIVER_ERROR.dataEx(JSON.toJSON(exception));
                    }
                }
        );
        paymentReceiverService.saveBatch(addReceivers);
    }

    @Override
    public boolean serviceEnable() {
        //todo 临时修改为 false
        return false;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    @Redisson(name = PayConst.SHOP_ORDER_AFS_LOCK_KEY, key = "#refundRequest.afsNum")
    public boolean refund(RefundRequestDTO refundRequest) {
        PaymentNotifyRecord record = paymentNotifyRecordService.lambdaQuery()
                .eq(PaymentNotifyRecord::getOrderNo, refundRequest.getOrderNum())
                .eq(PaymentNotifyRecord::getShopId, refundRequest.getShopId())
                .one();
        if (record == null) {
            return false;
        }
        WxPayRefundV3Request request = new WxPayRefundV3Request()
                .setSubMchid(record.getNotify().getSubMchid())
                .setTransactionId(record.getTransactionId())
                .setOutRefundNo(refundRequest.getAfsNum())
                .setReason("售后退款申请")
                .setNotifyUrl(globalAppProperties.fullUrl(Services.GRUUL_MALL_PAYMENT + "/wx/service/merchant/refund/notify"))
                .setAmount(
                        new WxPayRefundV3Request.Amount()
                                .setTotal(record.getNotify().getAmount().getTotalAmount())
                                .setRefund((int) (refundRequest.getRefundFee() / 100))
                                .setCurrency(FeeType.CNY.getType())
                );
        WxPayRefundV3Result wxPayRefundV3Result;
        try {
            wxPayRefundV3Result = wxPayService.refundV3(request);
        } catch (WxPayException exception) {
            log.error("退款申请失败", exception);
            throw PaymentError.REFUND_APPLY_ERROR.dataEx(JSON.toJSON(exception));
        }
        PaymentRefund refund = new PaymentRefund();
        refund.setOrderNum(refundRequest.getOrderNum());
        refund.setRouteKey(refundRequest.getRouteKey());
        refund.setExchange(refundRequest.getExchange());
        refund.setAfsNum(refundRequest.getAfsNum());
        refund.setRefundNo(refundRequest.getAfsNum());
        refund.setAsynRequest(JSON.toJSONString(request));
        refund.setAsynResult(JSON.toJSONString(wxPayRefundV3Result));
        paymentRefundService.save(refund);
        return true;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    @Redisson(name = PayConst.SHOP_ORDER_AFS_LOCK_KEY, key = "#notifyResult.outRefundNo")
    public String refundNotify(WxPayRefundNotifyV3Result.DecryptNotifyResult notifyResult) {
        String outRefundNo = notifyResult.getOutRefundNo();
        PaymentRefund refund = paymentRefundService.lambdaQuery()
                .eq(PaymentRefund::getRefundNo, outRefundNo)
                .isNull(PaymentRefund::getSynCallback)
                .one();
        if (refund == null) {
            return WxPayNotifyV3Response.success("已处理");
        }
        if (!WxConst.SUCCESS.equals(notifyResult.getRefundStatus())) {
            return WxPayNotifyV3Response.success("退款失败");
        }
        paymentRefundService.lambdaUpdate()
                .set(PaymentRefund::getSynCallback, JSON.toJSONString(notifyResult))
                .eq(PaymentRefund::getRefundNo, outRefundNo)
                .update();
        //如果路由key为空不进行回调
        if (StrUtil.isNotEmpty(refund.getExchange())) {
            rabbitTemplate.convertAndSend(
                    refund.getExchange(),
                    refund.getRouteKey(),
                    new RefundNotifyResultDTO()
                            .setOutTradeNo(outRefundNo)
                            .setAfsNum(outRefundNo)
                            .setRefundNum(notifyResult.getRefundId())

            );
        }
        return WxPayNotifyV3Response.success("成功");
    }


    @Autowired
    public void setWxServiceService(WxServiceService wxServiceService) {
        this.wxServiceService = wxServiceService;
    }
}
