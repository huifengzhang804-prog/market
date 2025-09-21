package com.medusa.gruul.search.service.model.vo;

import com.medusa.gruul.storage.api.vo.ProductSaleVolumeVO;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.util.List;

@Getter
@Setter
@Accessors(chain = true)
public class ShopProductSalesTopVO {

    private Long shopId;


    private List<ProductSaleVolumeVO> productSaleVolumes;

}
