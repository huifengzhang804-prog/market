package com.medusa.gruul.carrier.pigeon.service.im.model.dto;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.medusa.gruul.carrier.pigeon.api.enums.ChatWithType;
import com.medusa.gruul.carrier.pigeon.service.model.vo.MessageShopVO;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;


/**
 * <p>供应商-店铺消息列表DTO</p>
 *
 * @author An.Yan
 */
@Getter
@Setter
@ToString
public class PlatformShopAndUserPageQueryDTO extends Page<MessageShopVO> {

    private Long shopId;

    private String keywords;

    private Long userId;

    @NotNull
    private ChatWithType chatWithType;
}
