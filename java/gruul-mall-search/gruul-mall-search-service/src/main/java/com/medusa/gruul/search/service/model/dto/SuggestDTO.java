package com.medusa.gruul.search.service.model.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;

/**
 * @author 张治保
 * date 2023/4/20
 */
@Getter
@Setter
@ToString
public class SuggestDTO implements Serializable {

    /**
     * 关键词
     */
    @NotBlank
    @Size(max = 30)
    private String keyword;

    /**
     * 店铺id
     */
    private Long shopId;
}
