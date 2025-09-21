package com.medusa.gruul.afs.api.model;

import com.medusa.gruul.common.module.app.afs.AfsStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * <p></p>
 *
 * @author 张治保
 * date 2022/8/30
 */
@Getter
@Setter
@ToString
@Accessors(chain = true)
public class AfsCloseDTO implements Serializable {
    /**
     * 售后工单号
     */
    @NotBlank
    private String afsNo;

    /**
     * 关闭前的售后工单状态 如果不为空且 售后工单状态不想等，表示状态已更改 不再做调整
     */
    private AfsStatus currentStatus;

    /**
     * 是否是系统自动操作
     */
    @NotNull
    private Boolean isSystem;

    /**
     * 关闭原因
     */
    private String reason;

    /**
     * 是否需要继续执行 更新订单商品状态
     */
    @NotNull
    private Boolean updateShopOrderItem;
}
