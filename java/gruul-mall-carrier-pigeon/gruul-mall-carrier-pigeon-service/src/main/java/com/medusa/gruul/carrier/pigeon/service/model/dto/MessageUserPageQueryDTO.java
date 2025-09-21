package com.medusa.gruul.carrier.pigeon.service.model.dto;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.medusa.gruul.carrier.pigeon.api.enums.UserType;
import com.medusa.gruul.carrier.pigeon.service.model.vo.MessageShopVO;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * @author 张治保
 * date 2022/10/10
 */
@Getter
@Setter
@ToString
public class MessageUserPageQueryDTO extends Page<MessageShopVO> {

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
}
