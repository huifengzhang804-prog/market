package com.medusa.gruul.payment.service.service;

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
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;
import java.util.Set;

/**
 * 微信服务商服务
 *
 * @author 张治保
 * date 2023/6/3
 */
public interface WxServiceService {

	/**
	 * 合单支付 订单
	 *
	 * @param param           合单支付参数
	 * @param shopSubMchIdMap 店铺id与子商户号映射
	 * @return Object 支付需要的数据
	 */
	Object combineOrder(CombineOrderParam param, Map<Long, String> shopSubMchIdMap);

	/**
	 * 合单支付通知
	 *
	 * @param notifyResult 支付通知数据
	 * @return String 返回给微信的数据
	 */
	String combinePayNotify(CombineTransactionsResult notifyResult);

	/**
	 * 查询店铺与子商户号关系映射
	 *
	 * @param shopIds 店铺id集合
	 * @return Map<Long, String> 店铺id与子商户号映射
	 */
	Map<Long, String> shopSubMchIdMap(Set<Long> shopIds);

	/**
	 * 创建特约商户申请单
	 *
	 * @param shopId  店铺id
	 * @param request 申请单信息
	 * @return WxPayApplymentCreateResult 申请单结果
	 */
	WxPayApplymentCreateResult createApply(Long shopId, WxPayApplyment4SubCreateRequest request);

	/**
	 * 申请单状态查询
	 *
	 * @param applymentId 申请单id
	 * @return ApplymentStateQueryResult 申请查询结果
	 */
	ApplymentStateQueryResult applyState(String applymentId);


	/**
	 * 已绑定信息更新
	 *
	 * @param shopId      店铺id
	 * @param applymentId 申请单id
	 * @return ApplymentStateQueryResult 申请查询结果
	 */
	ApplymentStateQueryResult applyBound(Long shopId, String applymentId);


	/**
	 * 上传图片
	 *
	 * @param multipartFile 图片文件
	 * @return ImageUploadResult 图片上传结果
	 */
	ImageUploadResult imageUploadV3(MultipartFile multipartFile);

	/**
	 * 查询商户 特约商户申请状态
	 *
	 * @param shopId 店铺id
	 * @return 特约商户申请数据
	 */
	MerchantSub applyment(Long shopId);

	/**
	 * 确认绑定特约商户
	 *
	 * @param shopId 店铺id
	 */
	void confirmApply(Long shopId);

	/**
	 * 分账接口
	 *
	 * @param param 分账参数
	 */
	void profitSharing(ProfitSharingParam param);

	/**
	 * 解冻分账剩余资金
	 *
	 * @param param 解冻参数
	 */
	void profitSharingUnfreeze(ProfitSharingUnfreezeParam param);

	/**
	 * 批量添加分账接收方
	 *
	 * @param appid     应用id
	 * @param receivers 分账接收方数据列表
	 */
	void addReceiverBatch(String appid, Set<PaymentReceiver> receivers);

	/**
	 * 检查是否开启了服务商模式
	 *
	 * @return 是否开启了服务商模式
	 */
	boolean serviceEnable();

	/**
	 * 合单退款申请
	 *
	 * @param refundRequest 退款参数
	 * @return 是否走合单退款流程
	 */
	boolean refund(RefundRequestDTO refundRequest);

	/**
	 * 退款通知
	 *
	 * @param notifyResult 回调通知数据
	 * @return String 返回给微信的数据
	 */
	String refundNotify(WxPayRefundNotifyV3Result.DecryptNotifyResult notifyResult);
}