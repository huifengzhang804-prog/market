package com.medusa.gruul.carrier.pigeon.service.model.dto;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.medusa.gruul.carrier.pigeon.service.mp.entity.Message;
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
public class MessagePageQueryDTO extends Page<Message> {

    private Long userId;

    private Long shopId;
}
