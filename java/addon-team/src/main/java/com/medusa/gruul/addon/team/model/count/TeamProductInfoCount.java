package com.medusa.gruul.addon.team.model.count;

import com.medusa.gruul.common.model.base.ActivityShopProductKey;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;

/**
 * @author 张治保
 * date 2023/3/30
 */

@Getter
@Setter
@ToString(callSuper = true)
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = true, exclude = {"productName", "productImage"})
public class TeamProductInfoCount extends ActivityShopProductKey {

    /**
     * 商品名称
     */
    private String productName;

    /**
     * 商品图片
     */
    private String productImage;

}
