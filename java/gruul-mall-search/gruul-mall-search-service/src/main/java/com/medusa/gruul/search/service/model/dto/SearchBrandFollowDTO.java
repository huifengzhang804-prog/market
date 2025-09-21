package com.medusa.gruul.search.service.model.dto;


import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;


@Getter
@Setter
@ToString
@Accessors(chain = true)
public class SearchBrandFollowDTO {

    /**
     * 品牌id
     */
    @NotNull
    private Long brandId;

    /**
     * 是否关注
     */
    @NotNull
    private Boolean isFollow;
}
