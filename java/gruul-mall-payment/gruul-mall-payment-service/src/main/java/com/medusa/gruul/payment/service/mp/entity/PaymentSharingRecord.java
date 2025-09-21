package com.medusa.gruul.payment.service.mp.entity;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.handlers.Fastjson2TypeHandler;
import com.github.binarywang.wxpay.bean.profitsharing.request.ProfitSharingRequest;
import com.github.binarywang.wxpay.bean.profitsharing.result.ProfitSharingResult;
import com.github.binarywang.wxpay.exception.WxPayException;
import com.medusa.gruul.global.model.o.MessageKey;
import com.medusa.gruul.payment.api.enums.SharingService;
import com.medusa.gruul.payment.api.enums.SharingStatus;
import com.medusa.gruul.payment.api.model.param.ReceiverKey;
import com.medusa.gruul.payment.api.model.param.ServiceReceiverKey;
import com.medusa.gruul.payment.api.model.param.SharingResult;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author 张治保
 * date 2023/6/20
 */
@Getter
@Setter
@TableName(value = "t_payment_sharing_record", autoResultMap = true)
@Accessors(chain = true)
public class PaymentSharingRecord implements Serializable {


    @TableId(type = IdType.ASSIGN_ID)
    private Long id;

    /**
     * 分账服务方
     */
    private SharingService service;

    /**
     * 分账单号
     */
    private String sharingNo;

    /**
     * 店铺id
     */
    private Long shopId;

    /**
     * 子商户号
     */
    private String subMchid;

    /**
     * 微信支付订单号
     */
    private String transactionId;

    /**
     * 请求分账参数
     */
    @TableField(typeHandler = Fastjson2TypeHandler.class)
    private ProfitSharingRequest request;

    /**
     * 分账服务方
     */
    @TableField(typeHandler = Fastjson2TypeHandler.class)
    private ProfitSharingResult result;

    /**
     * 账户映射器
     */
    @TableField(typeHandler = Fastjson2TypeHandler.class)
    private Map<ServiceReceiverKey, ReceiverKey> accountMap;

    /**
     * 分账通知回调key
     */
    @TableField(typeHandler = Fastjson2TypeHandler.class)
    private MessageKey notifyKey;

    /**
     * 分账状态
     */
    @TableField("`status`")
    private SharingStatus status;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 失败原因
     */
    @TableField(typeHandler = Fastjson2TypeHandler.class)
    private WxPayException errorReason;

    public SharingResult sharingResult() {
        SharingResult sharingResult = new SharingResult()
                .setSharingNo(sharingNo)
                .setStatus(status)
                .setError(errorReason == null ? null : JSONUtil.parseObj(errorReason));
        if (SharingStatus.ERROR == status) {
            return sharingResult;
        }
        sharingResult.setReceiverResultMap(
                result.getReceiverList()
                        .stream()
                        .collect(
                                Collectors.toMap(
                                        receiver -> accountMap.get(new ServiceReceiverKey().setType(receiver.getType()).setAccount(receiver.getAccount())),
                                        receiver -> new SharingResult.ReceiverResult()
                                                .setResult(receiver.getResult())
                                                .setDetailId(receiver.getDetailId())
                                                .setFailReason(receiver.getFailReason())
                                                .setCreateTime(DateUtil.parseUTC(receiver.getCreateTime()).toLocalDateTime())
                                                .setFinishTime(StrUtil.isBlank(receiver.getFinishTime()) ? null : DateUtil.parseUTC(receiver.getFinishTime()).toLocalDateTime())
                                )
                        )
        );
        return sharingResult;
    }


}
