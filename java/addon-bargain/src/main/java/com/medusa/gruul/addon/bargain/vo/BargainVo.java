package com.medusa.gruul.addon.bargain.vo;

import jakarta.validation.constraints.NotNull;
import lombok.Data;


/**
 * 砍价VO
 *
 * @author jipeng
 * @since 2024/3/14
 */
@Data
public class BargainVo {

    /**
     * 砍价id
     */
    @NotNull
    private Long bargainId;
    /**
     * 店铺Id
     */
    private Long shopId;

    /**
     * 违规下架原因
     */
    private String violationReason;

}
