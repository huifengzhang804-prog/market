package com.medusa.gruul.live.service.model.vo;

import com.medusa.gruul.live.api.enums.AuditStatus;
import lombok.Data;

/**
 * @author miskw
 * @date 2022/11/18
 * @describe 直播间商品返回对象
 */
@Data
public class BroadcastRoomGoodsVo {
    /**
     * 商品名称
     */
    private String productName;

    /**
     * 商品图片
     */
    private String ossImgUrl;

    /**
     * 审核状态 0：未审核，1：审核中，2:审核通过，3审核未通过，4：违规下架
     */
    private AuditStatus auditStatus;

    /**
     * 店铺id
     */
    private Long shopId;

    private Long roomId;

}
