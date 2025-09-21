package com.medusa.gruul.addon.distribute.model;

/**
 * @author 张治保
 * date 2022/11/15
 */
public interface DistributeErrorCode {

	/**
	 * 分销配置不存在
	 */
	int CONFIG_NOT_EXISTS = 34301;

	/**
	 * 当前用户已绑定分销商
	 */
	int DISTRIBUTOR_BOUND = 34302;

	/**
	 * 当前用户已是分销商
	 */
	int USER_IS_DISTRIBUTOR_AFFAIRS = 34303;

	/**
	 * 错误的分销码
	 */
	int WRONG_CODE_OF_DISTRIBUTOR = 34304;

	/**
	 * 申请已提交，请勿重复提交
	 */
	int REPEATED_APPLY_SUBMISSION = 34305;

	/**
	 * 手机号已被使用
	 */
	int MOBILE_BEAN_USED = 34306;

	/**
	 * 验证码已过期
	 */
	int SMS_CODE_EXPIRED = 34307;

	/**
	 * 错误的短信验证码
	 */
	int SMS_CODE_WRONG = 34308;

	/**
	 * 申请不存在
	 */
	int APPLY_NOT_EXISTED = 34309;

	/**
	 * 分销商信息不存在
	 */
	int AFFAIRS_NOT_EXISTED = 34310;

	/**
	 * 不可绑定自己的分销码
	 */
	int CANNOT_BIND_OWN_CODE = 34311;

	/**
	 * 不可交叉绑定
	 */
	int CANNOT_BIND_CROSS = 34415;

	/**
	 * 申请不可用
	 */
	int CANNOT_APPLY = 34312;

	/**
	 * 微信未绑定
	 */
	int WECHAT_NOT_BOUND = 34313;

	/**
	 * 提现账户不存在
	 */
	int WITHDRAW_ACCOUNT_NOT_EXISTED = 34314;


}
