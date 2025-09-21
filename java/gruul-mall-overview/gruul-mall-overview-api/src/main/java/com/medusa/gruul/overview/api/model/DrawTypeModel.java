package com.medusa.gruul.overview.api.model;

import cn.hutool.core.lang.RegexPool;
import cn.hutool.core.util.StrUtil;
import com.medusa.gruul.common.model.resp.SystemCode;
import com.medusa.gruul.global.model.o.BaseDTO;
import com.medusa.gruul.overview.api.enums.WithdrawType;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;


/**
 * @author 张治保
 * date 2022/11/21
 */
@Getter
@Setter
@ToString
@Accessors(chain = true)
public class DrawTypeModel implements BaseDTO {

    /**
     * 提现方式
     */
    @NotNull
    private WithdrawType type;

    /**
     * 提现金额
     */
    @NotNull
    @Min(100)
    private Long amount;

    /**
     * 微信openid
     */
    private String openid;

    /**
     * 支付宝账号 使用支付宝时不为空
     */
    @Pattern(regexp = "(" + RegexPool.MOBILE + ")|(" + RegexPool.EMAIL + ")")
    private String alipayAccount;

    /**
     * 姓名 使用支付宝或银行卡时不为空
     */
    private String name;

    /**
     * 开户行 使用银行卡时不为空
     */
    private String bank;

    /**
     * 银行卡 银行卡卡号
     */
    private String cardNo;

    @Override
    public void validParam() {
        WithdrawType withdrawType = getType();
        if (WithdrawType.WECHAT == withdrawType) {
            SystemCode.PARAM_VALID_ERROR.trueThrow(StrUtil.isEmpty(getOpenid()));
            setName(null);
            setAlipayAccount(null);
            setBank(null);
            setCardNo(null);
            return;
        }
        if (WithdrawType.ALIPAY == withdrawType) {
            SystemCode.PARAM_VALID_ERROR.trueThrow(StrUtil.isEmpty(getName()) || StrUtil.isEmpty(getAlipayAccount()));
            setOpenid(null);
            setBank(null);
            setCardNo(null);
            return;
        }
        if (WithdrawType.BANK_CARD == withdrawType) {
            SystemCode.PARAM_VALID_ERROR.trueThrow(StrUtil.isEmpty(getName()) || StrUtil.isEmpty(getBank()) || StrUtil.isEmpty(getCardNo()));
            setOpenid(null);
            setAlipayAccount(null);
        }
    }


}
