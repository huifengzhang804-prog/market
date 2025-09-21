package com.medusa.gruul.user.service.model.dto;

import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.List;

/**
 * 用户收藏 DTO
 *
 * @author Administrator
 * @since 2024/2/24
 */
@Getter
@Setter
@ToString
public class CollectDTO {

    /**
     * 收藏、取消收藏
     * true：收藏
     * false: 取消收藏
     */
    Boolean whetherCollect = Boolean.TRUE;
    /**
     * 收藏商品信息 DTO
     */
    @Size(min = 1, max = 1000)
    private List<UserCollectDTO> userCollectDTOList;
}
