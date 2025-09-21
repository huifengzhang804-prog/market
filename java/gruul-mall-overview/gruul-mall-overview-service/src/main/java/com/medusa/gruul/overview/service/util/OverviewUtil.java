package com.medusa.gruul.overview.service.util;

import com.medusa.gruul.overview.api.entity.OverviewWithdraw;
import com.medusa.gruul.overview.api.model.DrawTypeModel;
import com.medusa.gruul.overview.service.model.enums.OverviewError;
import com.medusa.gruul.payment.api.model.transfer.AliTransferParam;
import com.medusa.gruul.payment.api.model.transfer.TransferParam;
import com.medusa.gruul.payment.api.model.transfer.WechatTransferParam;
import lombok.extern.slf4j.Slf4j;

/**
 * @author 张治保
 * date 2022/11/28
 */

public interface OverviewUtil {


	/**
	 * 提现工单 转成 线上转账需要的参数
	 *
	 * @param withdraw 提现工单
	 * @return 线上转账参数
	 */
	static TransferParam withdraw2Transfer(OverviewWithdraw withdraw) {
		DrawTypeModel drawType = withdraw.getDrawType();
		TransferParam transferParam;
		String desc = withdraw.getSourceType().getDesc();
		transferParam = switch (drawType.getType()) {
			case WECHAT -> new WechatTransferParam().setOpenid(drawType.getOpenid());
			case ALIPAY ->
					new AliTransferParam().setTitle(desc).setAccount(drawType.getAlipayAccount()).setName(drawType.getName());
			default -> throw OverviewError.NOT_SUPPORT_DRAW_TYPE.exception();
		};
		transferParam.setOutNo(withdraw.getNo())
				.setAmount(drawType.getAmount())
				.setRemark(withdraw.getNo() + "，" + desc);
		return transferParam;
	}


}
