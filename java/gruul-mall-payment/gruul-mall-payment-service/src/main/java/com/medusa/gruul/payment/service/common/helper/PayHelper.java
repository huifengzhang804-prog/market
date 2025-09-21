package com.medusa.gruul.payment.service.common.helper;

import com.egzosn.pay.ali.bean.AliTransactionType;
import com.egzosn.pay.common.bean.CurType;
import com.egzosn.pay.common.bean.DefaultCurType;
import com.egzosn.pay.common.bean.TransactionType;
import com.egzosn.pay.wx.v3.bean.WxTransactionType;
import com.medusa.gruul.common.model.constant.CommonPool;
import com.medusa.gruul.common.model.enums.PayType;
import com.medusa.gruul.common.system.model.model.Platform;
import com.medusa.gruul.payment.api.enums.FeeType;
import com.medusa.gruul.payment.api.enums.TradeStatus;
import com.medusa.gruul.payment.api.enums.WxPayType;
import com.medusa.gruul.payment.service.common.enums.INotifyResponse;
import com.medusa.gruul.payment.service.common.enums.NotifyResponse;
import com.medusa.gruul.payment.service.model.enums.CustomPayType;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * <p>
 *
 * </p>
 *
 * @author xiaoq
 * @ Description PayHelper.java
 * @date 2022-07-25 16:56
 */
public class PayHelper {

    /**
     * 交易类型，交易方式，
     * 查看{@link com.egzosn.pay.common.bean.TransactionType#getType()}及子类 ()}
     * CustomPayType.BALANCE_PAID 自定义余额pay
     * 例如，网页支付，扫码付等等
     */
    public static TransactionType wayType(Platform platform, PayType payType) {
        if (PayType.BALANCE == payType) {
            return CustomPayType.BALANCE_PAID;
        }
        switch (platform) {
            //app
            case IOS:
            case ANDROID:
            case HARMONY:
                switch (payType) {
                    case WECHAT:
                        return WxTransactionType.APP;
                    case ALIPAY:
                        return AliTransactionType.APP;
                    default:
                        break;
                }
                //网页端H5
            case PC:
                switch (payType) {
                    case WECHAT:
                        return WxTransactionType.MWEB;
                    case ALIPAY:
                        return AliTransactionType.PAGE;
                    default:
                        break;
                }
                //移动端H5
            case H5:
                switch (payType) {
                    case WECHAT:
                        return WxTransactionType.MWEB;
                    case ALIPAY:
                        return AliTransactionType.WAP;
                    default:
                        break;
                }
                //其它
            case WECHAT_MINI_APP:
            case WECHAT_MP:
                return WxTransactionType.JSAPI;
            default:
                break;
        }
        throw new IllegalArgumentException("TransactionType not found");
    }

    /**
     * tradeType 交易类型
     * {@link WxPayType#getPayType()}
     */
    public static Integer tradeType(Platform platform) {
        return switch (platform) {
            case WECHAT_MINI_APP -> WxPayType.JSAPI_MINI.getPayType();
            case WECHAT_MP -> WxPayType.JSAPI_MP.getPayType();
            case PC, H5 -> WxPayType.H5.getPayType();
            case IOS, HARMONY, ANDROID -> WxPayType.APP.getPayType();
        };
    }


    /**
     * 获取对应的支付货币类型
     */
    public static CurType curType(FeeType feeType) {
        if (feeType == null) {
            return DefaultCurType.CNY;
        }
        return switch (feeType) {
            case CNY -> DefaultCurType.CNY;
            case USD -> DefaultCurType.USD;
        };
    }

    /**
     * PayType payType
     * 根据支付类型获取汇率
     */
    public static BigDecimal getRate(PayType payType) {
        return switch (payType) {
            case ALIPAY, WECHAT -> CommonPool.BIG_DECIMAL_UNIT_CONVERSION_TEN_THOUSAND;
            default -> BigDecimal.ONE;
        };
    }

    public static BigDecimal getPrice(Long totalFee, PayType payType) {
        assert totalFee != null;
        return BigDecimal.valueOf(totalFee)
                .divide(
                        PayHelper.getRate(payType),
                        CommonPool.NUMBER_TWO,
                        RoundingMode.DOWN
                );
    }

    public static INotifyResponse notifyResponse(TradeStatus tradeStatus, PayType payType) {
        return switch (tradeStatus) {
            case OVERTIME_CLOSE, SUCCESS_PAYMENT -> switch (payType) {
                case WECHAT -> NotifyResponse.WECHAT_SUCCESS;
                case ALIPAY -> NotifyResponse.ALI_SUCCESS;
                default -> NotifyResponse.OTHER_SUCCESS;
            };
            default -> switch (payType) {
                case WECHAT -> NotifyResponse.WECHAT_FAIL;
                case ALIPAY -> NotifyResponse.ALI_FAIL;
                default -> NotifyResponse.OTHER_FAIL;
            };
        };
    }
}
