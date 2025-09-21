package com.medusa.gruul.payment.service.service;

import com.medusa.gruul.common.model.enums.PayType;
import com.medusa.gruul.payment.service.model.dto.MerchantDetailsDTO;
import com.medusa.gruul.payment.service.model.vo.MerchantDetailsVO;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * @author xiaoq
 * @ Description
 * @date 2022-07-13 09:13
 */
public interface PaymentMerchantService {
    /**
     * 编辑(新增/或修改)商户支付配置
     *
     * @param param 支付商户渠道配置DTO
     */
    void editPaymentMerchant(MerchantDetailsDTO param);

    /**
     * 获取商户支付配置
     *
     * @param payType 支付渠道
     * @return 商户支付配置
     */
    List<MerchantDetailsVO> getMerchantDetail(PayType payType);

    /**
     * 支付证书上传
     *
     * @param file 证书文件
     * @return 存储路径
     */
    String uploadCertificate(MultipartFile file);
}
