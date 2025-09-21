package com.medusa.gruul.payment.service.controller;

import com.medusa.gruul.common.idem.annotation.Idem;
import com.medusa.gruul.common.log.annotation.Log;
import com.medusa.gruul.common.model.enums.PayType;
import com.medusa.gruul.common.model.resp.Result;
import com.medusa.gruul.payment.service.model.dto.MerchantDetailsDTO;
import com.medusa.gruul.payment.service.model.vo.MerchantDetailsVO;
import com.medusa.gruul.payment.service.service.PaymentMerchantService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * 商户配置支付信息控制层
 *
 * @author xiaoq
 * @since 2022-07-12 17:03
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/merchant")
public class PaymentMerchantController {

    private final PaymentMerchantService paymentMerchantService;


    /**
     * 获取商户配置信息
     *
     * @return 商户配置信息
     */
    @Log("获取商户配置信息")
    @GetMapping("/get")
    @PreAuthorize("@S.platformPerm('generalSet')")
    public Result<List<MerchantDetailsVO>> getMerchantDetail(@RequestParam PayType payType) {
        List<MerchantDetailsVO> merchantDetail = paymentMerchantService.getMerchantDetail(payType);
        return Result.ok(merchantDetail);
    }

    /**
     * 编辑(新增/或修改)商户支付配置
     *
     * @param param 支付商户渠道配置DTO
     */
    @Idem(500)
    @Log("编辑 支付商户信息")
    @PostMapping("/edit")
    @PreAuthorize("@S.platformPerm('generalSet')")
    public Result<Void> editPaymentMerchant(@RequestBody @Valid MerchantDetailsDTO param) {
        param.validParam();
        paymentMerchantService.editPaymentMerchant(param);
        return Result.ok();
    }

    /**
     * 商户支付证书上传
     *
     * @param file 证书文件
     * @return 存储路径
     */
    @Idem(500)
    @Log("商户支付证书上传")
    @PostMapping("/upload")
    @PreAuthorize("@S.platformPerm('generalSet')")
    public Result<String> uploadCertificate(@RequestParam("file") MultipartFile file) {
        String path = paymentMerchantService.uploadCertificate(file);
        return Result.ok(path);
    }
}
