package com.medusa.gruul.goods.service.model.copy;

import com.medusa.gruul.goods.service.model.vo.CopySkuVO;
import com.medusa.gruul.storage.api.dto.SpecGroupDTO;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;

import java.util.List;

/**
 * @author 张治保
 * @since 2024/4/12
 */
@Getter
@Setter
@ToString
@Accessors(chain = true)
public class SpecSku {
    /**
     * 规格组
     */
    private List<SpecGroupDTO> specs;

    /**
     * 规格值
     */
    private List<CopySkuVO> skus;
}
