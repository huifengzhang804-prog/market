package com.medusa.gruul.goods.service.model.copy;

import lombok.Data;
import lombok.experimental.Accessors;

import java.util.List;

/**
 * @author miskw
 * @date 2023/2/7
 * @describe 淘宝规格组
 */
@Data
@Accessors(chain = true)
public class TbSpecGroup {

    private List<TbSpec> values;
    /**
     * 规格名称
     */
    private String name;
    private String pid;


}
