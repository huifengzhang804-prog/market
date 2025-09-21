package com.medusa.gruul.carrier.pigeon.service.model.dto;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.medusa.gruul.carrier.pigeon.service.model.vo.MessageUserVO;
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
public class MessageShopPageQueryDTO extends Page<MessageUserVO> {

    @JsonIgnore
    private Long userId;

    /**
     * 查询关键词
     */
    private String keywords;
}
