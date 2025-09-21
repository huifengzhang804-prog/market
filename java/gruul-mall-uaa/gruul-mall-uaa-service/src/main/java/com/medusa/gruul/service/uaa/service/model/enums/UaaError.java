package com.medusa.gruul.service.uaa.service.model.enums;

import com.medusa.gruul.common.security.model.constant.SecureCode;
import com.medusa.gruul.global.i18n.I18N;
import com.medusa.gruul.global.model.exception.Error;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * @author 张治保
 * date 2023/6/17
 */
@Getter
@RequiredArgsConstructor
public enum UaaError implements Error {

	/**
	 * 滑块验证码校验失败
	 */
	INVALID_SLIDE_CAPTCHA(10300, "uaa.captcha.slide.invalid"),

	/**
	 * 该用户已有其它权限,请直接修改或调整该用户权限
	 */
	USER_HAS_OTHER_ROLE(10301, "uaa.user.has.other.role"),

	/**
	 * 手机号输入不正确
	 */
	MOBILE_INCORRECT_FORMAT(10302, "uaa.user.mobile.incorrect.format"),
	/**
	 * 二次确认密码不正确
	 */
	CONFIRM_PASSWORD_INCORRECT(10303, "uaa.user.confirm.password.incorrect"),

	/**
	 * 用户名已被使用
	 */
	USERNAME_HAS_BEEN_USED(10304, "uaa.user.username.has.been.used"),

	/**
	 * 手机号已被使用
	 */
	MOBILE_HAS_BEEN_USED(10305, "uaa.user.mobile.has.been.used"),

	/**
	 * 已存在同名角色
	 */
	ROLE_NAME_HAS_BEEN_USED(10306, "uaa.role.name.has.been.used"),

	/**
	 * excel 文件格式读取失败
	 */
	EXCEL_DATA_READ_ERROR(10307, "uaa.excel.data.read.error"),

	/**
	 * 会员类型和会员等级必须同时存在或同时为空
	 */
	MEMBER_TYPE_AND_LEVEL_MUST_EXIST_OR_EMPTY(10308, "uaa.member.type.and.level.must.exist.or.empty"),

	/**
	 * 会员类型和会员等级不匹配
	 */
	MEMBER_TYPE_AND_LEVEL_NOT_MATCH(10309, "uaa.member.type.and.level.not.match"),

	/**
	 * 免费会员等级设置错误
	 */
	FREE_MEMBER_LEVEL_SETTING_ERROR(10310, "uaa.free.member.level.setting.error"),

	/**
	 * 付费会员等级设置错误
	 */
	PAID_MEMBER_LEVEL_SETTING_ERROR(10311, "uaa.paid.member.level.setting.error"),

	/**
	 * 角色已和用户绑定
	 */
	ROLE_HAS_USER(10311, "uaa.role.has.user"),

	/**
	 * 特殊角色无法删除
	 */
	SPECIAL_ROLE_CANNOT_DELETE(10312, "uaa.special.role.cannot.delete"),

	/**
	 * 用户状态异常
	 */
	USER_STATUS_ERROR(10313, "uaa.user.status.error"),

	/**
	 * 访问路径不能为空
	 */
	ACCESS_PATH_CANNOT_BE_EMPTY(10314, "uaa.access.path.cannot.be.empty"),

	/**
	 * 访问权限不能为空
	 */
	ACCESS_PERM_CANNOT_BE_EMPTY(10315, "uaa.access.perm.cannot.be.empty"),

	/**
	 * 存在同名或同路径菜单
	 */
	MENU_NAME_OR_PATH_HAS_EXIST(10316, "uaa.menu.name.or.path.has.exist"),

	/**
	 * 短信验证码已过期
	 */
	SMS_CODE_EXPIRED(10317, "uaa.sms.code.expired"),

	/**
	 * 短信验证码错误
	 */
	SMS_CODE_ERROR(SecureCode.SMS_CAPTCHA_INVALID, "uaa.sms.code.error"),

	/**
	 * 菜单包含子菜单 不能删除
	 */
	MENU_HAS_CHILDREN(10318, "uaa.menu.has.children"),

	CAN_NOT_DELETE_ENABLE_ADMIN(10319, "uaa.can.not.delete.enable.admin"),
	/**
	 * 卖家不存在
	 */
	MERCHANDISER_NOT_EXIST(10320, "uaa.merchandiser.not.exist"),

	/**
	 * 卖家没有店铺权限
	 */
	MERCHANDISER_NOT_HAS_SHOP(10322, "uaa.merchandiser.not.has.shop")
	;
	private final int code;
	private final String msgCode;


	@Override
	public int code() {
		return code;
	}

	@Override
	public String msg() {
		return I18N.msg(msgCode);
	}

	@Override
	public String msg(Object... args) {
		return I18N.msg(getMsgCode(), args);
	}


}
