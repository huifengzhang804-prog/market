package com.medusa.gruul.storage.api.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;
import java.util.List;

/**
 * 规格组
 *
 * @author 张治保
 */
@Getter
@Setter
@ToString
public class SpecGroupDTO implements Serializable {
    /**
     * 规格组名称
     */
    @NotBlank(message = "规格名称不能为空")
    @Size(max = 10, message = "规格名称不能超过10个字符")
    private String name;
    /**
     * 排序
     */
    private Integer order;
    /**
     * 规格列表
     */
    @NotNull
    @Size(min = 1)
    @Valid
    private List<SpecDTO> children;
}