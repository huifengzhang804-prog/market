package com.medusa.gruul.overview.api.enums;

import cn.hutool.json.JSONObject;
import com.medusa.gruul.global.model.exception.GlobalException;
import com.medusa.gruul.overview.api.model.DrawTypeModel;
import java.util.function.Consumer;
import java.util.function.Supplier;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.function.BiFunction;

/**
 * 提现类型
 *
 * @author 张治保
 * date 2023/5/15
 */
@Getter
@RequiredArgsConstructor
public enum DrawType {


	/**
	 * 微信
	 */
	WECHAT((model, detail) -> {
		model.setType(WithdrawType.WECHAT);
		model.setOpenid(detail.getStr("openid"));
		return model;
	},(amount)->{
        if (amount<1000L||amount>5000000L) {
			throw new GlobalException("单笔提现范围为0.1元~500元");
        }
	}),

	/**
	 * 支付宝
	 */
	ALIPAY((model, detail) -> {
		model.setType(WithdrawType.ALIPAY);
		model.setName(detail.getStr("name"));
		model.setAlipayAccount(detail.getStr("alipayAccount"));
		return model;
	},(amount)->{
		if (amount<1000L||amount>1000000000L) {
			throw new GlobalException("单笔提现范围为0.1元~10万元");
		}
	}),

	/**
	 * 银行卡
	 */
	BANK_CARD((model, detail) -> {
		model.setType(WithdrawType.BANK_CARD);
		model.setName(detail.getStr("name"));
		model.setBank(detail.getStr("bank"));
		model.setCardNo(detail.getStr("cardNo"));
		return model;
	},(amount)->{

	});



	private final BiFunction<DrawTypeModel, JSONObject, DrawTypeModel> modelFunction;

	private final Consumer<Long> amountChecker;

}
