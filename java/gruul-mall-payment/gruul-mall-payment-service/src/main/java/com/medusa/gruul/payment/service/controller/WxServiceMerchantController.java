package com.medusa.gruul.payment.service.controller;

import com.github.binarywang.wxpay.bean.applyment.ApplymentStateQueryResult;
import com.github.binarywang.wxpay.bean.applyment.WxPayApplyment4SubCreateRequest;
import com.github.binarywang.wxpay.bean.applyment.WxPayApplymentCreateResult;
import com.github.binarywang.wxpay.bean.ecommerce.CombineTransactionsNotifyResult;
import com.github.binarywang.wxpay.bean.ecommerce.SignatureHeader;
import com.github.binarywang.wxpay.bean.media.ImageUploadResult;
import com.github.binarywang.wxpay.bean.notify.WxPayNotifyV3Response;
import com.github.binarywang.wxpay.bean.notify.WxPayRefundNotifyV3Result;
import com.github.binarywang.wxpay.exception.WxPayException;
import com.github.binarywang.wxpay.service.WxPayService;
import com.medusa.gruul.common.idem.annotation.Idem;
import com.medusa.gruul.common.log.annotation.Log;
import com.medusa.gruul.common.model.resp.Result;
import com.medusa.gruul.common.security.resource.helper.ISecurity;
import com.medusa.gruul.common.web.valid.annotation.File;
import com.medusa.gruul.common.web.valid.enums.FileFormat;
import com.medusa.gruul.payment.service.mp.entity.MerchantSub;
import com.medusa.gruul.payment.service.service.WxServiceService;
import jakarta.annotation.security.PermitAll;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;


/**
 * 微信服务商接口
 *
 * @author 张治保
 * date 2023/5/31
 */
@Slf4j
@Validated
@RestController
@RequestMapping("/wx/service/merchant")
@RequiredArgsConstructor
@PreAuthorize("@S.shopPerm('mall:general:setting')")
@ConditionalOnProperty(prefix = "gruul.pay", name = "enable-service-mode", havingValue = "true")
public class WxServiceMerchantController {

    private final WxPayService wxPayService;
    private final WxServiceService wxServiceMerchantService;

    /**
     * 微信服务商支付回调
     *
     * @param data      支付回调数据
     * @param timestamp 时间戳
     * @param nonce     随机串
     * @param serialNo  微信平台证书序列号
     * @param signature 签名串
     * @return 回调返回数据
     */
    @Log("微信服务商支付回调")
    @RequestMapping("/combine/notify")
    @PermitAll
    public String combinePayNotify(
            @RequestHeader("Wechatpay-Timestamp") String timestamp,
            @RequestHeader("Wechatpay-Nonce") String nonce,
            @RequestHeader("Wechatpay-Serial") String serialNo,
            @RequestHeader("Wechatpay-Signature") String signature,
            @RequestBody String data
    ) {
        CombineTransactionsNotifyResult notifyResult;
        try {
            notifyResult = wxPayService.getEcommerceService().parseCombineNotifyResult(data, new SignatureHeader(timestamp, nonce, signature, serialNo));
        } catch (WxPayException ex) {
            log.error("回调通知异常", ex);
            return WxPayNotifyV3Response.fail("失败");
        }
        return wxServiceMerchantService.combinePayNotify(notifyResult.getResult());
    }

    /**
     * 微信退款回调
     *
     * @param data      退款回调数据
     * @param timestamp 时间戳
     * @param nonce     随机串
     * @param serialNo  微信平台证书序列号
     * @param signature 签名串
     * @return 返回给微信的数据
     */
    @Log("微信退款回调")
    @RequestMapping("/refund/notify")
    @PermitAll
    public String refundNotify(
            @RequestHeader("Wechatpay-Timestamp") String timestamp,
            @RequestHeader("Wechatpay-Nonce") String nonce,
            @RequestHeader("Wechatpay-Serial") String serialNo,
            @RequestHeader("Wechatpay-Signature") String signature,
            @RequestBody String data
    ) {
        WxPayRefundNotifyV3Result notifyV3Result;
        try {
            notifyV3Result = wxPayService.parseRefundNotifyV3Result(data, new com.github.binarywang.wxpay.bean.notify.SignatureHeader(timestamp, nonce, signature, serialNo));
        } catch (WxPayException ex) {
            log.error("回调通知异常", ex);
            return WxPayNotifyV3Response.fail("失败");
        }
        return wxServiceMerchantService.refundNotify(notifyV3Result.getResult());
    }



    /**
     * 查询商户 特约商户申请状态
     *
     * @return 特约商户申请数据
     */
    @GetMapping
    public Result<MerchantSub> applyment() {
        return Result.ok(wxServiceMerchantService.applyment(ISecurity.userMust().getShopId()));
    }

    /**
     * 创建特约商户申请单
     *
     * @param request 申请单参数
     * @return 申请单id
     */
    @Idem
    @PostMapping("/apply/create")
    public Result<WxPayApplymentCreateResult> createApply(@RequestBody WxPayApplyment4SubCreateRequest request) {
        return Result.ok(wxServiceMerchantService.createApply(ISecurity.userMust().getShopId(), request));
    }

    /**
     * 根据申请单号 查询申请单信息
     *
     * @param applymentId 申请单id
     * @return 申请单状态
     */
    @GetMapping("/apply/{applymentId}/state")
    public Result<ApplymentStateQueryResult> applyState(@PathVariable String applymentId) {
        return Result.ok(wxServiceMerchantService.applyState(applymentId));
    }


    /**
     * 商户与申请单绑定
     *
     * @param applymentId 申请单id
     * @return 绑定结果
     */
    @Idem
    @PutMapping("/apply/{applymentId}/bound")
    public Result<ApplymentStateQueryResult> applyBound(@PathVariable String applymentId) {
        return Result.ok(wxServiceMerchantService.applyBound(ISecurity.userMust().getShopId(), applymentId));
    }

    /**
     * 服务商图片上传
     *
     * @param file 图片文件
     * @return 图片上传结果
     */
    @Idem(200)
    @PostMapping("/image/upload")
    public Result<ImageUploadResult> imageUploadV3(
            @RequestPart @File(maxSize = 2, formats = {FileFormat.JPG, FileFormat.PNG, FileFormat.BMP}) MultipartFile file
    ) {
        return Result.ok(
                wxServiceMerchantService.imageUploadV3(file)
        );
    }

    /**
     * 特约商户确认
     *
     * @return void
     */
    @Idem
    @PutMapping("/apply/confirm")
    public Result<Void> confirmApply() {
        wxServiceMerchantService.confirmApply(ISecurity.userMust().getShopId());
        return Result.ok();
    }


}
