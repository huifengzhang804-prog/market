package com.medusa.gruul.afs.service.model.dto;

import com.medusa.gruul.afs.api.enums.AfsReason;
import com.medusa.gruul.afs.api.enums.AfsType;
import com.medusa.gruul.common.web.valid.annotation.Price;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.List;

/**
 * <p></p>
 *
 * @author 张治保
 * date 2022/8/3
 */
@Getter
@Setter
@ToString
public class AfsApplyDTO {

    /**
     * 订单号
     */
    @NotBlank
    private String orderNo;

    /**
     * 店铺id
     */
    @NotNull
    private Long shopId;

    /**
     * 订单商品id
     */
    @NotNull
    private Long itemId;

    /**
     * 申请退款金额
     */
    @Min(0)
    @Price
    private Long refundAmount;

    /**
     * 售后原因
     */
    @NotNull
    private AfsReason reason;

    /**
     * 售后工单类型
     */
    @NotNull
    private AfsType type;

    /**
     * 说明
     */
    private String remark;

    /**
     * 证据 图片/视频
     */
    @Size(max = 6)
    private List<String> evidences;

}
