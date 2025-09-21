package com.medusa.gruul.carrier.pigeon.service.im.model.dto;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.medusa.gruul.carrier.pigeon.api.enums.UserType;
import com.medusa.gruul.carrier.pigeon.service.model.vo.MessageShopVO;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;


/**
 * <p>供应商-店铺消息列表DTO</p>
 * @author An.Yan
 */
@Getter
@Setter
@Accessors(chain = true)
@ToString
public class MessageSupplierShopPageQueryDTO extends Page<MessageShopVO> {

    @JsonIgnore
    private Long shopId;

    /**
     * 检索关键词
     */
    private String keywords;

    /**
     * 发送方类型,参考{@link UserType}
     */
    private Integer senderType;

    /**
     * 接受方类型,参考{@link UserType}
     */
    private Integer receiverType;

    private Long receiverUserId;
}
