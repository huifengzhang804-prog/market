package com.medusa.gruul.storage.api.vo;

import java.io.Serializable;
import java.util.List;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * @author 张治保 date 2022/7/15
 */
@Getter
@Setter
@ToString
public class ProductSpecVO implements Serializable {

    private static final long serialVersionUID = 1L;
    /**
     * id
     */
    private Long id;
    /**
     * 规格组/规格名称
     */
    private String name;
    /**
     * 排序
     */
    private Integer order;
    /**
     * 规格
     */
    private List<ProductSpecVO> children;

}
