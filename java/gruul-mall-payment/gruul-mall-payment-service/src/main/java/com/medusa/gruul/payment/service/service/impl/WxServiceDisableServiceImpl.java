package com.medusa.gruul.payment.service.service.impl;

import com.github.binarywang.wxpay.bean.applyment.ApplymentStateQueryResult;
import com.github.binarywang.wxpay.bean.applyment.WxPayApplyment4SubCreateRequest;
import com.github.binarywang.wxpay.bean.applyment.WxPayApplymentCreateResult;
import com.github.binarywang.wxpay.bean.ecommerce.CombineTransactionsResult;
import com.github.binarywang.wxpay.bean.media.ImageUploadResult;
import com.github.binarywang.wxpay.bean.notify.WxPayRefundNotifyV3Result;
import com.medusa.gruul.payment.api.model.dto.RefundRequestDTO;
import com.medusa.gruul.payment.api.model.param.CombineOrderParam;
import com.medusa.gruul.payment.api.model.param.ProfitSharingParam;
import com.medusa.gruul.payment.api.model.param.ProfitSharingUnfreezeParam;
import com.medusa.gruul.payment.service.mp.entity.MerchantSub;
import com.medusa.gruul.payment.service.mp.entity.PaymentReceiver;
import com.medusa.gruul.payment.service.service.WxServiceService;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;
import java.util.Set;

/**
 * 未开启服务商模式时 默认服务商接口实现
 *
 * @author 张治保
 * date 2023/6/6
 */
@Service
@ConditionalOnProperty(prefix = "gruul.pay", name = "enable-service-mode", havingValue = "false", matchIfMissing = true)
public class WxServiceDisableServiceImpl implements WxServiceService {
	@Override
	public Object combineOrder(CombineOrderParam param, Map<Long, String> shopSubMchIdMap) {
		return null;
	}

	@Override
	public String combinePayNotify(CombineTransactionsResult notifyResult) {
		return null;
	}

	@Override
	public Map<Long, String> shopSubMchIdMap(Set<Long> shopIds) {
		return Map.of();
	}

	@Override
	public WxPayApplymentCreateResult createApply(Long shopId, WxPayApplyment4SubCreateRequest request) {
		return null;
	}

	@Override
	public ApplymentStateQueryResult applyState(String applymentId) {
		return null;
	}


	@Override
	public ImageUploadResult imageUploadV3(MultipartFile multipartFile) {
		return null;
	}

	@Override
	public ApplymentStateQueryResult applyBound(Long shopId, String applymentId) {
		return null;
	}

	@Override
	public MerchantSub applyment(Long shopId) {
		return null;
	}


	@Override
	public void confirmApply(Long shopId) {

	}


	@Override
	public void profitSharing(ProfitSharingParam param) {

	}


	@Override
	public void profitSharingUnfreeze(ProfitSharingUnfreezeParam param) {

	}

	@Override
	public void addReceiverBatch(String appid, Set<PaymentReceiver> receivers) {

	}

	@Override
	public boolean serviceEnable() {
		return false;
	}

	@Override
	public boolean refund(RefundRequestDTO refundRequest) {
		return false;
	}

	@Override
	public String refundNotify(WxPayRefundNotifyV3Result.DecryptNotifyResult notifyResult) {
		return null;
	}

}
